package fms.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import fms.validation.DayCheckValidation;

/**
 * 日付相関チェックアノテーション
 *
 * @author 髙橋 真澄
 */
@Documented
@Constraint(validatedBy = DayCheckValidation.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DayCheck {
    String message() default "{ERROR0004}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
