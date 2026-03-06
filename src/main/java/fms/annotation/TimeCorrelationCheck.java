package fms.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import fms.validation.TimeCorrelationCheckValidation;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * 時間相関チェックアノテーション
 *
 * @author 藤田 誠也
*/
@Documented
@Constraint(validatedBy = TimeCorrelationCheckValidation.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TimeCorrelationCheck {
    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}