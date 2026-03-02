package fms.validation;

import java.util.Locale;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import fms.annotation.FileNameDuplicationCheck;
import fms.domain.MessageDomain;
import fms.form.FileInputForm;

/**
 * ファイル名重複チェック用バリデーション
 *
 * @author 髙橋 真澄
 */
public class FileNameDuplicationCheckValidation
        implements ConstraintValidator<FileNameDuplicationCheck, FileInputForm> {

    /** メッセージプロパティ */
    @Autowired
    private MessageSource messageSource;

    /**
     * ファイル名重複チェック
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

        // 未入力が2つ以上あるかどうか
        if (value.getFileName().stream().filter(s -> s.isEmpty()).count() >= 2) {

            // されていないならエラー
            return true;
        }

        // 無いならファイル名の重複チェックを行う

        long distinctCount = value.getFileName().stream().distinct().count();

        if (distinctCount < value.getFileName().size()) {
            String errorMessage = messageSource.getMessage("{" + MessageDomain.VALID_KEY_ERROR0020 + "}", null,
                    context.getDefaultConstraintMessageTemplate(), Locale.getDefault());
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(errorMessage)
                    .addPropertyNode("fileName")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
