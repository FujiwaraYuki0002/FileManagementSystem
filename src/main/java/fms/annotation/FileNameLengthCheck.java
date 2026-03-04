package fms.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import fms.domain.MessageDomain;
import fms.validation.FileNameLengthCheckValidation;

/**
 * ファイル名長さチェックアノテーション
 *
 * @author 髙橋 真澄
 */
@Documented
@Constraint(validatedBy = FileNameLengthCheckValidation.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FileNameLengthCheck {
    String message() default "{" + MessageDomain.VALID_KEY_ERROR0017 + "}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
