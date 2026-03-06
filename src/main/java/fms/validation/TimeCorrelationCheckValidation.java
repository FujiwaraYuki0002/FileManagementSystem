package fms.validation;

import org.springframework.beans.factory.annotation.Autowired;

import fms.annotation.TimeCorrelationCheck;
import fms.form.FileInputForm;
import fms.util.DateUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 日付相関チェックバリデーション
 *
 * @author 藤田 誠也
 */
public class TimeCorrelationCheckValidation implements ConstraintValidator<TimeCorrelationCheck, FileInputForm> {

    @Autowired
    DateUtil dateUtil;

    /**
     * 時間相関チェック
     *
     * @author 藤田 誠也
     */
    @Override
    public boolean isValid(FileInputForm value, ConstraintValidatorContext context) {

        int timeFrom = Integer.parseInt(dateUtil.noColonTime(value.getTimeFrom()));
        int timeTo = Integer.parseInt(dateUtil.noColonTime(value.getTimeTo()));

        System.out.println(timeFrom - timeTo);
        if (timeFrom - timeTo > 0) {
            context.buildConstraintViolationWithTemplate("仮メッセージ！")
                    .addPropertyNode("timeTo")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
