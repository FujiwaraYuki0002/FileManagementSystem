package fms.validation;

import java.util.Locale;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import fms.annotation.NoInputCheck;
import fms.form.FileForm;

/**
 * 検索項目全未入力バリデーション
 *
 * @author 髙橋 真澄
 */
public class NoInputCheckValidation implements ConstraintValidator<NoInputCheck, FileForm> {

    /** メッセージプロパティ */
    @Autowired
    private MessageSource messageSource;

    /**
     * 検索項目全未入力チェック
     *
     * @author 髙橋 真澄
     */
    @Override
    public boolean isValid(FileForm value, ConstraintValidatorContext context) {

        // それぞれの項目が未入力、又は空白の入力のみかどうかチェック
        if ((value.getUserId().isEmpty()) &&
                (value.getUserName().isEmpty()) &&
                (value.getDateFrom().replaceAll("[\\s　]", "").isEmpty()) &&
                (value.getDateTo().replaceAll("[\\s　]", "").isEmpty()) &&
                (value.getTitle().replaceAll("[\\s　]", "").isEmpty()) &&
                (value.getFileName().replaceAll("[\\s　]", "").isEmpty()) &&
                (value.getFindWord().replaceAll("[\\s　]", "").isEmpty())) {

            // 未入力だった場合エラーメッセージを登録
            String errorMessage = messageSource.getMessage("{ERROR0008}", new String[] { "検索条件" },
                    context.getDefaultConstraintMessageTemplate(), Locale.getDefault());
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(errorMessage)
                    .addPropertyNode("fileId")
                    .addConstraintViolation();

            return false;
        }
        return true;
    }
}
