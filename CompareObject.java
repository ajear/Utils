package com.tehy.nip.be.walkingapi.utils;

import com.baomidou.mybatisplus.annotation.TableName;
import com.tehy.nip.be.walkingapi.config.FieldName;
import org.springframework.util.ObjectUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author zcq
 * @date 2021/1/6 16:03
 */
public class CompareObject<T> {
    private T original;

    private T current;

    /**
     * @param cls
     * @return
     */
    public String contrastObj(Class<T> cls) {
        StringBuilder sb = new StringBuilder();
        try {
            Field[] fields = cls.getDeclaredFields();
            for (Field field : fields) {
                PropertyDescriptor pd = new PropertyDescriptor(field.getName(), cls);
                Method getMethod = pd.getReadMethod();
                String type = field.getType().getName();
                if (!"java.util.Set".equals(type)) {
                    Object o1 = getMethod.invoke(this.original);
                    Object o2 = getMethod.invoke(this.current);
                    if (null != o2) {
                        String s1 = o1 == null ? "" : o1.toString();
                        String s2 = o2 == null ? "" : o2.toString();
                        if (!s1.equals(s2)) {
                            //System.out.println("不一样的属性：" + field.getName() + " 属性值：[" + s1 + "," + s2 + "]");
                            sb.append(field.getName() + ":" + "[" + s1 + "," + s2 + "];");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 获取当前对象的所有成员变量值
     * @param pojo 当前对象
     * @return
     */
    public static String objectValue(Object pojo) {
        String str = "";
        try {
            Class clazz = pojo.getClass();
            Field[] fields = pojo.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("serialVersionUID".equals(field.getName())) {
                    continue;
                }
                PropertyDescriptor pd = new PropertyDescriptor(field.getName(), clazz);
                Method getMethod = pd.getReadMethod();
                Object obj = getMethod.invoke(pojo);
                if (obj == null) {
                    continue;
                }
                String[] strings = new String[4];
                FieldName fieldName = field.getAnnotation(FieldName.class);
                strings[0] = field.getName();//字段名
                strings[1] = fieldName == null ? field.getName() : fieldName.value();//中文（如果没空就展示字段名)
                if(field.getType() == LocalDateTime.class){
                    strings[2] = ObjectUtils.isEmpty(obj) ? "" : LocalDateTime.parse(obj.toString()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));//原值
                }else {
                    strings[2] = ObjectUtils.isEmpty(obj) ? "" : obj.toString();//原值
                }
                str = str + (fieldName == null ? field.getName() : fieldName.value()) + "==>新值: [" + strings[2] + "];\n";
            }
        } catch (Exception e) {
            LogHelper.error(e, e.getMessage(), "CompareObject", "objectValue");
        }
        return str;
    }

    /**
     * 比较两个对象，返回不一样的字符串
     * @param oldPojo 旧对象
     * @param newPojo 新对象
     * @return
     */
    public static String contrastObj(Object oldPojo, Object newPojo) {
        String str = "";
        //T pojo1 = (T) oldBean;
        //T pojo2 = (T) newBean;
        try {
            Class clazz = oldPojo.getClass();
            Field[] fields = oldPojo.getClass().getDeclaredFields();
            int i = 1;
            for (Field field : fields) {
                if ("serialVersionUID".equals(field.getName())) {
                    continue;
                }
                PropertyDescriptor pd = new PropertyDescriptor(field.getName(), clazz);
                Method getMethod = pd.getReadMethod();
                Object o1 = getMethod.invoke(oldPojo);
                Object o2 = getMethod.invoke(newPojo);
                if (o1 == null || o2 == null) {
                    continue;
                }
                if (!o1.toString().equals(o2.toString())) {
                    String[] strings = new String[4];
                    FieldName fieldName = field.getAnnotation(FieldName.class);
                    strings[0] = field.getName();//字段名
                    strings[1] = fieldName == null ? field.getName() : fieldName.value();//中文（如果没空就展示字段名)
                    if(field.getType() == LocalDateTime.class){
                        strings[2] = ObjectUtils.isEmpty(o1) ? "" : LocalDateTime.parse(o1.toString()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));//原值
                        strings[3] = LocalDateTime.parse(o2.toString()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));//现值
                    }else {
                        strings[2] = ObjectUtils.isEmpty(o1) ? "" : o1.toString();//原值
                        strings[3] = o2.toString();//现值
                    }
                    str = str + (fieldName == null ? field.getName() : fieldName.value()) + "==>旧值: [" + strings[2] + "]->新值: [" + strings[3] + "];\n";
                    i++;
                }
            }
        } catch (Exception e) {
            LogHelper.error(e, e.getMessage(), "CompareObject", "contrastObj");
        }
        return str;
    }

    public T getOriginal() {
        return original;
    }

    public void setOriginal(T original) {
        this.original = original;
    }

    public T getCurrent() {
        return current;
    }

    public void setCurrent(T current) {
        this.current = current;
    }

    /**
     * 复制对象属性（对象类型必须相同）
     *
     * @param orig        资源对象
     * @param dest        目标对象
     * @param clazz       源对象类
     * @param ignoreNull  是否忽略空（true:忽略，false：不忽略）
     * @param ignoreExist 是否只复制dest值为空的数据  true 是，false 否
     * @return
     */
    public static <T> T copyProperties(T orig, T dest, Class<?> clazz, boolean ignoreNull, boolean ignoreExist) {
        if (orig == null || dest == null)
            return null;
        if (!clazz.isAssignableFrom(orig.getClass()))
            return null;
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value = field.get(orig);
                Object value2 = field.get(dest);
                System.out.println(field + "----" + value2);
                if (!java.lang.reflect.Modifier.isFinal(field.getModifiers())) {
                    if (!(ignoreNull && value == null)) {
                        if (ignoreExist && value2 != null) {

                        } else {
                            field.set(dest, value);
                        }
                    }
                }
                field.setAccessible(false);
            } catch (Exception e) {
            }
        }
        if (clazz.getSuperclass() == Object.class) {
            return dest;
        }
        return copyProperties(orig, dest, clazz.getSuperclass(), ignoreNull, ignoreExist);
    }

    public static void main(String[] args) {
        System.out.println(LocalDateTime.parse("2020-12-25T18:25:25.256").format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
}
