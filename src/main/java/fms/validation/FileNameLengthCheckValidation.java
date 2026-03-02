package fms.validation;

import java.util.Locale;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import fms.annotation.FileNameLengthCheck;
import fms.domain.MessageDomain;
import fms.form.FileInputForm;

/**
 * ファイル名長さチェック用バリデーション
 *
 * @author 髙橋 真澄
 */
public class FileNameLengthCheckValidation implements ConstraintValidator<FileNameLengthCheck, FileInputForm> {

    /** メッセージプロパティ */
    @Autowired
    private MessageSource messageSource;

    /**
     * ファイル名長さチェック
     *
     * @author 髙橋 真澄
     */
    @Override
    public boolean isValid(FileInputForm value, ConstraintValidatorContext context) {

        // ファイル名が入力されているかどうか
        if (value.getFileName().isEmpty()) {

            // されていないならエラー
            return false;
        }

        // ファイル名長さが100文字より大きならエラー
        for (String fileName : value.getFileName()) {
            if (fileName.length() > 100) {
                String errorMessage = messageSource.getMessage("{" + MessageDomain.VALID_KEY_ERROR0018 + "}", null,
                        context.getDefaultConstraintMessageTemplate(), Locale.getDefault());
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(errorMessage)
                        .addPropertyNode("fileName")
                        .addConstraintViolation();
                return false;
            }
        }
        return true;
    }
}
