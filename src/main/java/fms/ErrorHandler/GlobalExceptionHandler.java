package fms.ErrorHandler;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeoutException;

import jakarta.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.security.access.AccessDeniedException;
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

    /** SQLセッション */
    @Autowired
    private SqlSession sqlSession;

    /**
     * データベース関連のSQL例外（DataIntegrityViolationException）を処理する。
     * 発生したDataIntegrityViolationExceptionの内容をログに記録し、セッションを無効化した後、ログイン画面にリダイレクトする。
     *
     * @param session
     * @param redirectAttributes
     * @param DataIntegrityViolationException ex
     * @return ログイン画面
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleDataIntegrityViolationException(HttpSession session, RedirectAttributes redirectAttributes,
            DataIntegrityViolationException ex) {

        // userIdを取得
        String userId = (mUser.getUserId() != null) ? mUser.getUserId() : "SYSTEM_USER";

        // エラーログをLogUtilを使ってデータベースに追加
        logUtil.addLog(LogDomain.CODE_LOG_SECTION_ERROR, "データ整合性違反", "DataIntegrityViolationException",
                userId, Thread.currentThread().getStackTrace()[1].getClassName());

        // sqlsessionをクローズ
        closeSqlSession();

        // エラーログをコンソール上に出力
        ex.printStackTrace();

        // エラーメッセージをリダイレクトスコープに保存
        String exceptionErrorMessage = messageSource.getMessage(MessageDomain.VALID_KEY_ERROR0022, null, Locale.JAPAN);
        redirectAttributes.addFlashAttribute("exceptionErrorMessage", exceptionErrorMessage);

        // セッションを無効化
        session.invalidate();

        // ログイン画面にリダイレクト
        return "redirect:/";
    }

    /**
     * データベース関連のSQL例外（PermissionDeniedDataAccessException）を処理する。
     * 発生したPermissionDeniedDataAccessExceptionの内容をログに記録し、セッションを無効化した後、ログイン画面にリダイレクトする。
     *
     * @param session
     * @param redirectAttributes
     * @param PermissionDeniedDataAccessException ex
     * @return ログイン画面
     */
    @ExceptionHandler(PermissionDeniedDataAccessException.class)
    public String handlePermissionDeniedDataAccessException(HttpSession session, RedirectAttributes redirectAttributes,
            PermissionDeniedDataAccessException ex) {

        // userIdを取得
        String userId = (mUser.getUserId() != null) ? mUser.getUserId() : "SYSTEM_USER";

        // エラーログをLogUtilを使ってデータベースに追加
        logUtil.addLog(LogDomain.CODE_LOG_SECTION_ERROR, "アクセス権限不足", "PermissionDeniedDataAccessException",
                userId, Thread.currentThread().getStackTrace()[1].getClassName());

        // sqlsessionをクローズ
        closeSqlSession();

        // エラーログをコンソール上に出力
        ex.printStackTrace();

        // エラーメッセージをリダイレクトスコープに保存
        String exceptionErrorMessage = messageSource.getMessage(MessageDomain.VALID_KEY_ERROR0023, null, Locale.JAPAN);
        redirectAttributes.addFlashAttribute("exceptionErrorMessage", exceptionErrorMessage);

        // セッションを無効化
        session.invalidate();

        // ログイン画面にリダイレクト
        return "redirect:/";
    }

    /**
     * データベース関連のSQL例外（DeadlockLoserDataAccessException）を処理する。
     * 発生したDeadlockLoserDataAccessExceptionの内容をログに記録し、セッションを無効化した後、ログイン画面にリダイレクトする。
     *
     * @param session
     * @param redirectAttributes
     * @param DeadlockLoserDataAccessException ex
     * @return ログイン画面
     */
    @ExceptionHandler(DeadlockLoserDataAccessException.class)
    public String handleDeadlockLoserDataAccessException(HttpSession session, RedirectAttributes redirectAttributes,
            DeadlockLoserDataAccessException ex) {

        // userIdを取得
        String userId = (mUser.getUserId() != null) ? mUser.getUserId() : "SYSTEM_USER";

        // エラーログをLogUtilを使ってデータベースに追加
        logUtil.addLog(LogDomain.CODE_LOG_SECTION_ERROR, "デッドロックによるトランザクション失敗", "DeadlockLoserDataAccessException",
                userId, Thread.currentThread().getStackTrace()[1].getClassName());

        // sqlsessionをクローズ
        closeSqlSession();

        // エラーログをコンソール上に出力
        ex.printStackTrace();

        // エラーメッセージをリダイレクトスコープに保存
        String exceptionErrorMessage = messageSource.getMessage(MessageDomain.VALID_KEY_ERROR0024, null,
                Locale.JAPAN);
        redirectAttributes.addFlashAttribute("exceptionErrorMessage", exceptionErrorMessage);

        // セッションを無効化
        session.invalidate();

        // ログイン画面にリダイレクト
        return "redirect:/";
    }

    /**
     * データベース関連のSQL例外（UncategorizedSQLException）を処理する。
     * 発生したUncategorizedSQLExceptionの内容をログに記録し、セッションを無効化した後、ログイン画面にリダイレクトする。
     *
     * @param session
     * @param redirectAttributes
     * @param UncategorizedSQLException ex
     * @return ログイン画面
     */
    @ExceptionHandler(UncategorizedSQLException.class)
    public String handleDeadlockLoserDataAccessException(HttpSession session, RedirectAttributes redirectAttributes,
            UncategorizedSQLException ex) {

        // userIdを取得
        String userId = (mUser.getUserId() != null) ? mUser.getUserId() : "SYSTEM_USER";

        // エラーログをLogUtilを使ってデータベースに追加
        logUtil.addLog(LogDomain.CODE_LOG_SECTION_ERROR, "予期しないエラーコード", "UncategorizedSQLException",
                userId, Thread.currentThread().getStackTrace()[1].getClassName());

        // sqlsessionをクローズ
        closeSqlSession();

        // エラーログをコンソール上に出力
        ex.printStackTrace();

        // エラーメッセージをリダイレクトスコープに保存
        String exceptionErrorMessage = messageSource.getMessage(MessageDomain.VALID_KEY_ERROR0025, null, Locale.JAPAN);
        redirectAttributes.addFlashAttribute("exceptionErrorMessage", exceptionErrorMessage);

        // セッションを無効化
        session.invalidate();

        // ログイン画面にリダイレクト
        return "redirect:/";
    }

    /**
     * データベース関連のSQL例外（DataAccessException）を処理する。
     * 発生したDataAccessExceptionの内容をログに記録し、セッションを無効化した後、ログイン画面にリダイレクトする。
     *
     * @param session
     * @param redirectAttributes
     * @param DataAccessException ex
     * @return ログイン画面
     */
    @ExceptionHandler(DataAccessException.class)
    public String handleDataAccessException(HttpSession session, RedirectAttributes redirectAttributes,
            DataAccessException ex) {

        // userIdを取得
        String userId = (mUser.getUserId() != null) ? mUser.getUserId() : "SYSTEM_USER";

        // DataAccessExceptionのメッセージや原因がOutOfMemoryであるかを判定
        if (ex.getMessage().contains("out of memory")
                || (ex.getCause() != null && ex.getCause().getMessage().contains("out of memory"))) {

            // OutOfMemoryエラーの場合の処理
            // エラーログをLogUtilを使ってデータベースに追加
            logUtil.addLog(LogDomain.CODE_LOG_SECTION_ERROR, "メモリ不足のSQLエラー", "OutOfMemory",
                    userId, Thread.currentThread().getStackTrace()[1].getClassName());

            // エラーログをコンソール上に出力
            ex.printStackTrace();

            // エラーメッセージをリダイレクトスコープに保存
            String exceptionErrorMessage = messageSource.getMessage(MessageDomain.VALID_KEY_ERROR0036, null,
                    Locale.JAPAN);
            redirectAttributes.addFlashAttribute("exceptionErrorMessage", exceptionErrorMessage);

        } else {

            // DataAccessExceptionエラーの場合の処理
            // エラーログをLogUtilを使ってデータベースに追加
            logUtil.addLog(LogDomain.CODE_LOG_SECTION_ERROR, "その他のSQLエラー", "DataAccessException",
                    userId, Thread.currentThread().getStackTrace()[1].getClassName());

            // エラーログをコンソール上に出力
            ex.printStackTrace();

            // エラーメッセージをリダイレクトスコープに保存
            String exceptionErrorMessage = messageSource.getMessage(MessageDomain.VALID_KEY_ERROR0020, null,
                    Locale.JAPAN);
            redirectAttributes.addFlashAttribute("exceptionErrorMessage", exceptionErrorMessage);

        }

        // sqlsessionをクローズ
        closeSqlSession();

        // セッションを無効化
        session.invalidate();

        // ログイン画面にリダイレクト
        return "redirect:/";

    }

    /**
     * 例外（IOException）を処理する。
     * 発生したIOExceptionの内容をログに記録し、セッションを無効化した後、ログイン画面にリダイレクトする。
     *
     * @param session
     * @param redirectAttributes
     * @param IOException ex
     * @return ログイン画面
     */
    @ExceptionHandler(IOException.class)
    public String handleIOException(HttpSession session, RedirectAttributes redirectAttributes, IOException ex) {

        // userIdを取得
        String userId = (mUser.getUserId() != null) ? mUser.getUserId() : "SYSTEM_USER";

        // エラーログをLogUtilを使ってデータベースに追加
        logUtil.addLog(LogDomain.CODE_LOG_SECTION_ERROR, "入出力中にエラー", "IOException",
                userId, Thread.currentThread().getStackTrace()[1].getClassName());

        // sqlsessionをクローズ
        closeSqlSession();

        // エラーログをコンソール上に出力
        ex.printStackTrace();

        // エラーメッセージをリダイレクトスコープに保存
        String exceptionErrorMessage = messageSource.getMessage(MessageDomain.VALID_KEY_ERROR0026, null, Locale.JAPAN);
        redirectAttributes.addFlashAttribute("exceptionErrorMessage", exceptionErrorMessage);

        // セッションを無効化
        session.invalidate();

        // ログイン画面にリダイレクト
        return "redirect:/";
    }

    /**
     * 例外（NullPointerException）を処理する。
     * 発生したNullPointerExceptionの内容をログに記録し、セッションを無効化した後、ログイン画面にリダイレクトする。
     *
     * @param session
     * @param redirectAttributes
     * @param NullPointerException ex
     * @return ログイン画面
     */
    @ExceptionHandler(NullPointerException.class)
    public String handleNullPointerException(HttpSession session, RedirectAttributes redirectAttributes,
            NullPointerException ex) {

        // userIdを取得
        String userId = (mUser.getUserId() != null) ? mUser.getUserId() : "SYSTEM_USER";

        // エラーログをLogUtilを使ってデータベースに追加
        logUtil.addLog(LogDomain.CODE_LOG_SECTION_ERROR, "nullオブジェクトに対する操作エラー", "NullPointerException",
                userId, Thread.currentThread().getStackTrace()[1].getClassName());

        // sqlsessionをクローズ
        closeSqlSession();

        // エラーログをコンソール上に出力
        ex.printStackTrace();

        // エラーメッセージをリダイレクトスコープに保存
        String exceptionErrorMessage = messageSource.getMessage(MessageDomain.VALID_KEY_ERROR0027, null, Locale.JAPAN);
        redirectAttributes.addFlashAttribute("exceptionErrorMessage", exceptionErrorMessage);

        // セッションを無効化
        session.invalidate();

        // ログイン画面にリダイレクト
        return "redirect:/";
    }

    /**
     * 例外（OutOfMemoryError）を処理する。
     * 発生したOutOfMemoryErrorの内容をログに記録し、セッションを無効化した後、ログイン画面にリダイレクトする。
     *
     * @param session
     * @param redirectAttributes
     * @param OutOfMemoryError ex
     * @return ログイン画面
     */
    @ExceptionHandler(OutOfMemoryError.class)
    public String handleOutOfMemoryError(HttpSession session, RedirectAttributes redirectAttributes,
            OutOfMemoryError ex) {

        // userIdを取得
        String userId = (mUser.getUserId() != null) ? mUser.getUserId() : "SYSTEM_USER";

        // エラーログをLogUtilを使ってデータベースに追加
        logUtil.addLog(LogDomain.CODE_LOG_SECTION_ERROR, "メモリ不足", "OutOfMemoryError",
                userId, Thread.currentThread().getStackTrace()[1].getClassName());

        // sqlsessionをクローズ
        closeSqlSession();

        // エラーログをコンソール上に出力
        ex.printStackTrace();

        // エラーメッセージをリダイレクトスコープに保存
        String exceptionErrorMessage = messageSource.getMessage(MessageDomain.VALID_KEY_ERROR0028, null, Locale.JAPAN);
        redirectAttributes.addFlashAttribute("exceptionErrorMessage", exceptionErrorMessage);

        // セッションを無効化
        session.invalidate();

        // ログイン画面にリダイレクト
        return "redirect:/";
    }

    /**
     * 例外（StackOverflowError）を処理する。
     * 発生したStackOverflowErrorの内容をログに記録し、セッションを無効化した後、ログイン画面にリダイレクトする。
     *
     * @param session
     * @param redirectAttributes
     * @param StackOverflowError ex
     * @return ログイン画面
     */
    @ExceptionHandler(StackOverflowError.class)
    public String handleStackOverflowError(HttpSession session, RedirectAttributes redirectAttributes,
            StackOverflowError ex) {

        // userIdを取得
        String userId = (mUser.getUserId() != null) ? mUser.getUserId() : "SYSTEM_USER";

        // エラーログをLogUtilを使ってデータベースに追加
        logUtil.addLog(LogDomain.CODE_LOG_SECTION_ERROR, "スタック領域不足", "StackOverflowError",
                userId, Thread.currentThread().getStackTrace()[1].getClassName());

        // sqlsessionをクローズ
        closeSqlSession();

        // エラーログをコンソール上に出力
        ex.printStackTrace();

        // エラーメッセージをリダイレクトスコープに保存
        String exceptionErrorMessage = messageSource.getMessage(MessageDomain.VALID_KEY_ERROR0029, null,
                Locale.JAPAN);
        redirectAttributes.addFlashAttribute("exceptionErrorMessage", exceptionErrorMessage);

        // セッションを無効化
        session.invalidate();

        // ログイン画面にリダイレクト
        return "redirect:/";
    }

    /**
     * 例外（IllegalStateException）を処理する。
     * 発生したIllegalStateExceptionの内容をログに記録し、セッションを無効化した後、ログイン画面にリダイレクトする。
     *
     * @param session
     * @param redirectAttributes
     * @param IllegalStateException ex
     * @return ログイン画面
     */
    @ExceptionHandler(IllegalStateException.class)
    public String handleIllegalStateException(HttpSession session, RedirectAttributes redirectAttributes,
            IllegalStateException ex) {

        // userIdを取得
        String userId = (mUser.getUserId() != null) ? mUser.getUserId() : "SYSTEM_USER";

        // エラーログをLogUtilを使ってデータベースに追加
        logUtil.addLog(LogDomain.CODE_LOG_SECTION_ERROR, "不正なオブジェクトの状態", "IllegalStateException",
                userId, Thread.currentThread().getStackTrace()[1].getClassName());

        // sqlsessionをクローズ
        closeSqlSession();

        // エラーログをコンソール上に出力
        ex.printStackTrace();

        // エラーメッセージをリダイレクトスコープに保存
        String exceptionErrorMessage = messageSource.getMessage(MessageDomain.VALID_KEY_ERROR0030, null, Locale.JAPAN);
        redirectAttributes.addFlashAttribute("exceptionErrorMessage", exceptionErrorMessage);

        // セッションを無効化
        session.invalidate();

        // ログイン画面にリダイレクト
        return "redirect:/";
    }

    /**
     * 例外（TimeoutException）を処理する。
     * 発生したTimeoutExceptionの内容をログに記録し、セッションを無効化した後、ログイン画面にリダイレクトする。
     *
     * @param session
     * @param redirectAttributes
     * @param TimeoutException ex
     * @return ログイン画面
     */
    @ExceptionHandler(TimeoutException.class)
    public String handleTimeoutException(HttpSession session, RedirectAttributes redirectAttributes,
            TimeoutException ex) {

        // userIdを取得
        String userId = (mUser.getUserId() != null) ? mUser.getUserId() : "SYSTEM_USER";

        // エラーログをLogUtilを使ってデータベースに追加
        logUtil.addLog(LogDomain.CODE_LOG_SECTION_ERROR, "操作タイムアウト", "TimeoutException",
                userId, Thread.currentThread().getStackTrace()[1].getClassName());

        // sqlsessionをクローズ
        closeSqlSession();

        // エラーログをコンソール上に出力
        ex.printStackTrace();

        // エラーメッセージをリダイレクトスコープに保存
        String exceptionErrorMessage = messageSource.getMessage(MessageDomain.VALID_KEY_ERROR0031, null, Locale.JAPAN);
        redirectAttributes.addFlashAttribute("exceptionErrorMessage", exceptionErrorMessage);

        // セッションを無効化
        session.invalidate();

        // ログイン画面にリダイレクト
        return "redirect:/";
    }

    /**
     * 例外（AccessDeniedException）を処理する。
     * 発生したAccessDeniedExceptionの内容をログに記録し、セッションを無効化した後、ログイン画面にリダイレクトする。
     *
     * @param session
     * @param redirectAttributes
     * @param AccessDeniedException ex
     * @return ログイン画面
     */
    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDeniedException(HttpSession session, RedirectAttributes redirectAttributes,
            AccessDeniedException ex) {

        // userIdを取得
        String userId = (mUser.getUserId() != null) ? mUser.getUserId() : "SYSTEM_USER";

        // エラーログをLogUtilを使ってデータベースに追加
        logUtil.addLog(LogDomain.CODE_LOG_SECTION_ERROR, "アクセス権限不足", "AccessDeniedException",
                userId, Thread.currentThread().getStackTrace()[1].getClassName());

        // sqlsessionをクローズ
        closeSqlSession();

        // エラーログをコンソール上に出力
        ex.printStackTrace();

        // エラーメッセージをリダイレクトスコープに保存
        String exceptionErrorMessage = messageSource.getMessage(MessageDomain.VALID_KEY_ERROR0032, null, Locale.JAPAN);
        redirectAttributes.addFlashAttribute("exceptionErrorMessage", exceptionErrorMessage);

        // セッションを無効化
        session.invalidate();

        // ログイン画面にリダイレクト
        return "redirect:/";
    }

    /**
     * 例外（IllegalArgumentException）を処理する。
     * 発生したIllegalArgumentExceptionの内容をログに記録し、セッションを無効化した後、ログイン画面にリダイレクトする。
     *
     * @param session
     * @param redirectAttributes
     * @param IllegalArgumentException ex
     * @return ログイン画面
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(HttpSession session, RedirectAttributes redirectAttributes,
            IllegalArgumentException ex) {

        // userIdを取得
        String userId = (mUser.getUserId() != null) ? mUser.getUserId() : "SYSTEM_USER";
        // エラーログをLogUtilを使ってデータベースに追加
        logUtil.addLog(LogDomain.CODE_LOG_SECTION_ERROR, "不正な引数", "IllegalArgumentException",
                userId, Thread.currentThread().getStackTrace()[1].getClassName());

        // sqlsessionをクローズ
        closeSqlSession();

        // エラーログをコンソール上に出力
        ex.printStackTrace();

        // エラーメッセージをリダイレクトスコープに保存
        String exceptionErrorMessage = messageSource.getMessage(MessageDomain.VALID_KEY_ERROR0033, null, Locale.JAPAN);
        redirectAttributes.addFlashAttribute("exceptionErrorMessage", exceptionErrorMessage);

        // セッションを無効化
        session.invalidate();

        // ログイン画面にリダイレクト
        return "redirect:/";
    }

    /**
     * 例外（UnsupportedOperationException）を処理する。
     * 発生したUnsupportedOperationExceptionの内容をログに記録し、セッションを無効化した後、ログイン画面にリダイレクトする。
     *
     * @param session
     * @param redirectAttributes
     * @param UnsupportedOperationException ex
     * @return ログイン画面
     */
    @ExceptionHandler(UnsupportedOperationException.class)
    public String handleUnsupportedOperationException(HttpSession session, RedirectAttributes redirectAttributes,
            UnsupportedOperationException ex) {

        // userIdを取得
        String userId = (mUser.getUserId() != null) ? mUser.getUserId() : "SYSTEM_USER";

        // エラーログをLogUtilを使ってデータベースに追加
        logUtil.addLog(LogDomain.CODE_LOG_SECTION_ERROR, "サポートされていない操作", "UnsupportedOperationException",
                userId, Thread.currentThread().getStackTrace()[1].getClassName());

        // sqlsessionをクローズ
        closeSqlSession();

        // エラーログをコンソール上に出力
        ex.printStackTrace();

        // エラーメッセージをリダイレクトスコープに保存
        String exceptionErrorMessage = messageSource.getMessage(MessageDomain.VALID_KEY_ERROR0034, null, Locale.JAPAN);
        redirectAttributes.addFlashAttribute("exceptionErrorMessage", exceptionErrorMessage);

        // セッションを無効化
        session.invalidate();

        // ログイン画面にリダイレクト
        return "redirect:/";
    }

    /**
     * 例外（Exception）を処理する。
     * 発生したExceptionの内容をログに記録し、セッションを無効化した後、ログイン画面にリダイレクトする。
     *
     * @param session
     * @param redirectAttributes
     * @param Exception ex
     * @return ログイン画面
     */
    @ExceptionHandler(Exception.class)
    public String handleException(HttpSession session, RedirectAttributes redirectAttributes, Exception ex) {

        // userIdを取得
        String userId = (mUser.getUserId() != null) ? mUser.getUserId() : "SYSTEM_USER";

        // エラーログをLogUtilを使ってデータベースに追加
        logUtil.addLog(LogDomain.CODE_LOG_SECTION_ERROR, "その他の異常終了", "Exception",
                userId, Thread.currentThread().getStackTrace()[1].getClassName());

        // sqlsessionをクローズ
        closeSqlSession();

        // エラーログをコンソール上に出力
        ex.printStackTrace();

        // エラーメッセージをリダイレクトスコープに保存
        String exceptionErrorMessage = messageSource.getMessage(MessageDomain.VALID_KEY_ERROR0035, null, Locale.JAPAN);
        redirectAttributes.addFlashAttribute("exceptionErrorMessage", exceptionErrorMessage);

        // セッションを無効化
        session.invalidate();

        // ログイン画面にリダイレクト
        return "redirect:/";
    }

    /**
     * 例外が発生した際にsqlsessionをクローズする。
     * クローズに失敗した場合コンソールにエラーを出力。
     */
    private void closeSqlSession() {
        if (sqlSession != null) {
            try {
                sqlSession.close(); // MyBatisの場合

            } catch (Exception ex) {

                // エラーログをコンソール上に出力
                ex.printStackTrace();
            }
        }
    }

}
