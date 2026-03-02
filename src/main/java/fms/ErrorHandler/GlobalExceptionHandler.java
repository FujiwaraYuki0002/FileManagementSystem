package fms.ErrorHandler;

import java.util.Locale;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import fms.domain.LogDomain;
import fms.domain.MessageDomain;
import fms.entity.MUser;
import fms.util.LogUtil;

/**
 * 異常終了時ログ登録クラス
 *
 * @author 大塚 月愛
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /** メッセージプロパティ */
    @Autowired
    private MessageSource messageSource;

    /** ログユーティリティ */
    @Autowired
    private LogUtil logUtil;

    /** ユーザーエンティティ */
    @Autowired
    private MUser mUser;

    /**
     * データベース関連のSQL例外（SQLException）を処理します。
     * 発生したSQLExceptionの内容をログに記録し、セッションを無効化した後、ログイン画面にリダイレクトします。
     *
     * @param session
     * @param redirectAttributes
     * @param DataAccessException ex
     * @return ログイン画面
     */
    @ExceptionHandler(DataAccessException.class)
    public String handleSQLException(HttpSession session, RedirectAttributes redirectAttributes,
            DataAccessException ex) {

        if (mUser.getUserId() == null) {

            // エラーログをLogUtilを使ってデータベースに追加
            logUtil.addLog(LogDomain.CODE_LOG_SECTION_ERROR, "異常終了", "DataAccessException",
                    "SYSTEM_USER", Thread.currentThread().getStackTrace()[1].getClassName());
        } else {

            // エラーログをLogUtilを使ってデータベースに追加
            logUtil.addLog(LogDomain.CODE_LOG_SECTION_ERROR, "異常終了", "DataAccessException",
                    mUser.getUserId(), Thread.currentThread().getStackTrace()[1].getClassName());
        }

        ex.printStackTrace();

        String exceptionErrorMessage = messageSource.getMessage(MessageDomain.VALID_KEY_ERROR0021, null, Locale.JAPAN);
        redirectAttributes.addFlashAttribute("exceptionErrorMessage", exceptionErrorMessage);

        // セッションを無効化
        session.invalidate();

        // ログイン画面にリダイレクト
        return "redirect:/";
    }

    /**
     * 例外（Exception）を処理します。
     * 発生したExceptionの内容をログに記録し、セッションを無効化した後、ログイン画面にリダイレクトします。
     *
     * @param session
     * @param redirectAttributes
     * @param Exception ex
     * @return ログイン画面
     */
    @ExceptionHandler(Exception.class)
    public String handleException(HttpSession session, RedirectAttributes redirectAttributes, Exception ex) {

        if (mUser.getUserId() == null) {
            // エラーログをLogUtilを使ってデータベースに追加
            logUtil.addLog(LogDomain.CODE_LOG_SECTION_ERROR, "異常終了", "Exception",
                    "SYSTEM_USER", Thread.currentThread().getStackTrace()[1].getClassName());
        } else {
            // エラーログをLogUtilを使ってデータベースに追加
            logUtil.addLog(LogDomain.CODE_LOG_SECTION_ERROR, "異常終了", "Exception",
                    mUser.getUserId(), Thread.currentThread().getStackTrace()[1].getClassName());
        }

        ex.printStackTrace();

        String exceptionErrorMessage = messageSource.getMessage(MessageDomain.VALID_KEY_ERROR0021, null, Locale.JAPAN);
        redirectAttributes.addFlashAttribute("exceptionErrorMessage", exceptionErrorMessage);

        // セッションを無効化
        session.invalidate();

        // ログイン画面にリダイレクト
        return "redirect:/";
    }

}
