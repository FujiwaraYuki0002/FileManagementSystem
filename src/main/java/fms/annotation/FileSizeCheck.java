package fms.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import fms.validation.FileSizeCheckValidation;

/**
 * ファイルサイズチェックアノテーション
 *
 * @author 髙橋 真澄
 */
@Documented
@Constraint(validatedBy = FileSizeCheckValidation.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FileSizeCheck {
    String message() default "{ERROR0015}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
