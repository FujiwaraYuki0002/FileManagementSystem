package fms.util;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

/**
 * メッセージユーティリティ
 *
 * @author 大塚 月愛
 */
@Component
public class MessageUtil {

    @Autowired
    private MessageSource messageSource;

    /**
     * メッセージ取得（第二引数なし）
     *
     * @param messageId
     * @return メッセージ
     */
    public String getMessage(String messageId) {
        String message = messageSource.getMessage(messageId, null,
                Locale.getDefault());
        if (message == null || message.equals("")) {
            return "";
        }
        return message;
    }

    /**
     * メッセージ取得（第二引数あり）
     *
     * @param messageId
     * @param values
     * @return メッセージ
     */
    public String getMessage(String messageId, String[] values) {
        String message = messageSource.getMessage(messageId, values,
                Locale.getDefault());
        if (message == null || message.equals("")) {
            return "";
        }
        return message;
    }

}
