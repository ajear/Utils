package com.tehy.nip.be.walkingapi.config;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author zcq
 * @date 2021/1/6 18:41
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldName {
    String value() default "";
}
