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

import fms.annotation.DateFraudCheck;
import fms.form.FileInputForm;
import fms.util.DateUtil;

/**
 * 日付妥当性チェックバリデーション
 *
 * @author 髙橋 真澄
 */
public class DateFraudCheckValidation implements ConstraintValidator<DateFraudCheck, FileInputForm> {

    /** Dateユーティリティ */
    @Autowired
    private DateUtil dateUtil;

    /** メッセージプロパティ */
    @Autowired
    private MessageSource messageSource;

    /**
     * 日付妥当性チェック
     *
     * @author 髙橋 真澄
     */
    @Override
    public boolean isValid(FileInputForm value, ConstraintValidatorContext context) {

        // 日付が入力されているかどうか
        if (StringUtils.isEmpty(value.getMeetingDate())) {

            // されていないならエラー
            return true;
        }

        // 入力されている場合は日付型に変換して比較する

        // フォームの日付型変更用フォーマット
        SimpleDateFormat formDateChangeFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date meetingDateParsed = null;
        try {
            meetingDateParsed = formDateChangeFormat.parse(value.getMeetingDate());
        } catch (ParseException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }

        // 日付の相関チェック
        if (!meetingDateParsed.before(dateUtil.getToday())) {
            // 日付が今日以降の場合
            String errorMessage = messageSource.getMessage("{ERROR0004}", null,
                    context.getDefaultConstraintMessageTemplate(), Locale.getDefault());
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(errorMessage)
                    .addPropertyNode("meetingDate")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
