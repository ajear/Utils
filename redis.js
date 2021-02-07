'use strict';

const env           = process.env.DAOKEENV || 'test';
// const notCluster    = env == 'test' || env == 'qa'; // isCluster
const axios         = require('axios');
const msgpack       = require('msgpack5')();
const { skynet }    = require('./log');
const Redis         = require('ioredis');

// get redis ip
async function getRedisIp() {

    let redisConfs = [];

    // let redisConfs = [{
    //     host: '10.100.217.77',
    //     port: 6379,
    //     keyPrefix: 'nodejs_tehy_test_',
    //     connectTimeout: 5000
    // }];

    // if (!notCluster) { 

        let user = '';
        let password = '';

        let url = ``;

        let data = [
     
        ];
        try {
            let re = await axios(url);
            data = re.data[0].instances.map((v) => v.ip.split(':'));

        } catch (e) {
            console.log(`[ Error Redis ] Unified configuration fetch redis ips fail.`);
        }
        redisConfs = data.map((v) => ({
            connectTimeout: 5000,
            host: v[0],
            keyPrefix: `nodejs_tehy_${env}_`,
            port: v[1]
        }));
    // }
    
    return redisConfs;
}

// redis scanStream
function getScanStream(rc, opt) {
    return new Promise((res) => {
        var _obj = [];
        var stream = rc.scanStream(opt);
        stream.on('data', (v) => {
            _obj = _obj.concat(v);
        });
        stream.on('end', function () {
            res(_obj);
        });
    });
}
/**
 * 防止超时操作
 *
 * @param {promise} fn 可能超时操作
 * @param {string} logStr 超时日志附加内容
 * @returns
 */
async function redisRace(fn, logStr) {
    let timerCount;
    // 设置超时，1秒
    let timer = new Promise((res) => {
        timerCount = setTimeout(() => {
            skynet(`[redis timeout]${logStr}}`, 'debug');
            res(false);
        }, 1000);
    });
    let result = await Promise.race([timer, fn]);
    // 清除 timerCount
    clearTimeout(timerCount);
    return result;
}

// 生成客户端
exports.createClient = async function (project = 'tehynip_fe') {
    // Get config
    let flag = true;
    let conf = await getRedisIp();
    let _client;
    // if (notCluster) {
    //     let _conf = conf[0];
    //     _conf['showFriendlyErrorStack'] = true;
    //     _conf['retryStrategy'] = () => 10000;
    //     _client = new Redis(_conf);
    // } else {
    //     _client = new Redis.Cluster(conf, {
    //         clusterRetryStrategy: () => 10000,
    //         retryDelayOnTryAgain: 10000
    //     });
    // }
    _client = new Redis.Cluster(conf, {
        clusterRetryStrategy: () => 10000,
        retryDelayOnTryAgain: 10000
    });

    let remind = (project + '          ').slice(0, 10);
    console.log(`[ Success Redis ${remind} ] New Redis Client has created.`);
    await waitConnect();
    return _client;
    function waitConnect() {
        return new Promise((res) => {
            _client
                .on('connect', function () {
                    console.log(`[ Success Redis ${remind} ] Connect Success.`);
                    res('connect success');
                })
                .on('error', async function (e) {
                if (!flag)
                    return;
                flag = false;
                skynet(`[redis]${e.stack}}`, 'warn');
                let _conf = await getRedisIp();
                let selfClient = _client;
                // if (notCluster) {
                //     if (selfClient.options && _conf[0].port && _conf[0].host) {
                //         selfClient.options.port = _conf[0].port;
                //         selfClient.options.host = _conf[0].host;
                //     }
                // } else {
                //     selfClient.startupNodes = _conf;
                // }
                selfClient.startupNodes = _conf;
                flag = true;
            });
        });
    }
};

class RedisClient {
    async init() {
        this.redisClient = await exports.createClient();
    }
    // auto complete key
    async auto(key) {
        let arr_keys = [];
        if (this.redisClient) {
            let masters = typeof this.redisClient.nodes == 'function' ? this.redisClient.nodes('master') : [this.redisClient];
            // let masters = !notCluster && typeof this.redisClient.nodes == 'function' ? this.redisClient.nodes('master') : [this.redisClient];
            let tasks = [];
            for (let rc of masters) {
                let _opt = {
                    count: 2000,
                    match: '*' + (key || '') + '*'
                };
                tasks.push(getScanStream(rc, _opt));
            }
            arr_keys = await Promise.all(tasks);
        }
        return arr_keys;
    }
    /**
     *cache a value by key
     *
     * @param {string} key
     * @param {any} value
     * @param {number} time [1800s] 如果 -1 则为永久
     * @param {boolean} needEnv [true] 是否在 key 前添加环境前缀
     * @param {boolean | string} hash [false] 默认不是hash
     * @returns set value
     * @memberof RedisClient
     */
    async set(key, value, time = 30 * 60, needEnv = true, hash = false) {
        let oKey = key + '';
        if (oKey == 'null' || oKey == 'undefined' || !key.trim() || value === undefined)
            return value;
        value = msgpack.encode(value);
        time === -1 && (time = 60 * 60 * 24 * 30);
        let result = null;
        if (typeof hash == 'string') { // hash
            result = await this.extension('hmset', [(needEnv ? env + '___' : '') + key, hash, value], `hmset ${key} ${hash}`);
        } else {
            result = await this.extension('set', [(needEnv ? env + '___' : '') + key, value, 'EX', time], `set ${key}`);
        }
        return result;
    }
    /**
     * get value by key in cache
     *
     * @param {string} key
     * @param {boolean} needEnv [true] 是否在 key 前添加环境前缀
     * @param {boolean} isStrict [false] 是否强制调用 redis（例： shoptoken 数据强依赖 redis，即使本地也希望在启动配置中未配置 isNeedRedis 时可调用 redis）
     * @param {boolean | string} hash [false] 默认不是hash
     * @param {boolean} isSecond [false] 是否第二次请求
     * @returns get value
     * @memberof RedisClient
     */
    async get(key, needEnv = true, isStrict = false, hash = false, isSecond = false) {
        let result = null;
     
        if (needEnv === true)
            key = env + '___' + key;
        if (typeof hash == 'string') { // hash
            result = await this.extension('hgetBuffer', [key, hash], `hgetBuffer ${key} ${hash}`);
        } else {
            result = await this.extension('getBuffer', [key], `getBuffer ${key}`);
        }
        if (result === null && isSecond === false) { // null的时候再次请求一次
            result = await this.get(key, false, isStrict, hash, true);
        }
        if (Buffer.isBuffer(result)) {
            let _result = msgpack.decode(result);
            if (_result == result[0] && result.length > 1) { // 此判断说明value值之前为经过 msgpack.encode
                try {
                    _result = result.toString('utf8');
                    _result = JSON.parse(_result);
                } catch (error) {
                    error;
                }
            }
            result = _result;
        }
        if (result !== null && isSecond === true) { // 打个点
            skynet(`[redis]key ${key} hash ${hash}\n result: ${JSON.stringify(result, null, 4)}`, 'warn');
        }
        return result;
    }
    // delete a value by key
    async del(key, needEnv = false) {
        let re = await this.extension('del', [(needEnv ? env + '___' : '') + key], `del ${key}`);
        return re;
    }
    // hget
    async hget(key, field, needEnv = true, isStrict = false, isSecond = false) {
        let re = await this.get(key, needEnv, isStrict, field, isSecond);
        return re;
    }
    // hmset
    async hmset(key, field, value, needEnv = true) {
        let re = await this.set(key, value, -1, needEnv, field);
        return re;
    }
    /**
     * 缓存处理方法
     * @param {String} key 缓存key
     * @param {*} [result] 缓存值，传值代表存，不传代表取
     * @param {Number} [time] 缓存有效时间，单位分钟，不传为默认10分钟
     */
    async cache(key, result, time = 10 * 60) {
        if (arguments.length == 1) {
            let res = await this.get(key);
            return res;
        } else {
            this.set(key, result, time);
        }
    }
    /**
     * 检测 rc 实例 和 方法是否存在并且执行
     *
     * @param {string} fnString 方法名
     * @param {any[]} [params=[]] 传入参数
     * @param {string} [logStr=''] 日志字段
     * @returns
     * @memberof RedisClient
     */
    async extension(fnString, params = [], logStr = '') {
        let re = null;
        let rc = this.redisClient;
        if (!rc) {
            console.warn('has not create rc ! place run [this.init] to build rc');
        } else {
            if (typeof rc[fnString] == 'function') {
                re = await redisRace(rc[fnString].apply(rc, params), logStr || fnString);
            } else {
                console.warn(fnString + ' is not a function');
            }
        }
        return re;
    }
}

exports.redisClient = new RedisClient();