package fms.validation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import fms.annotation.DayCheck;
import fms.form.FileForm;

/**
 * 日付相関チェックバリデーション
 *
 * @author 髙橋 真澄
 */
public class DayCheckValidation implements ConstraintValidator<DayCheck, FileForm> {

    /** メッセージプロパティ */
    @Autowired
    private MessageSource messageSource;

    /**
     * 日付相関チェック
     *
     * @author 髙橋 真澄
     */
    @Override
    public boolean isValid(FileForm value, ConstraintValidatorContext context) {

        // 日付が両方入力されているかどうか
        if (StringUtils.isEmpty(value.getDateFrom()) ||
                StringUtils.isEmpty(value.getDateTo())) {
            return true;
        }

        // 日付が両方同じかどうか
        if (value.getDateFrom().equals(value.getDateTo())) {
            return true;
        }

        // 入力されている場合は日付型に変換して比較する

        // フォームの日付型変更用フォーマット
        SimpleDateFormat formDateChangeFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date dateFromParsed = null;
        Date dateToParsed = null;
        try {
            dateFromParsed = formDateChangeFormat.parse(value.getDateTo());
            dateToParsed = formDateChangeFormat.parse(value.getDateFrom());
        } catch (ParseException e) {
            // TODO 自動生成された catch ブロック
            return false;
        }

        // 日付の相関チェック
        if (!dateToParsed.before(dateFromParsed)) {
            // 日付が今日以降の場合
            String errorMessage = messageSource.getMessage("{ERROR004}", null,
                    context.getDefaultConstraintMessageTemplate(), Locale.getDefault());
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(errorMessage)
                    .addPropertyNode("dateFrom")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
