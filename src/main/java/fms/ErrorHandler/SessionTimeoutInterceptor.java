package fms.ErrorHandler;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import fms.domain.LogDomain;
import fms.domain.MessageDomain;
import fms.util.LogUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * セッションタイムアウト確認用インターセプター
 *
 * @author 大塚 月愛
 *
 */
@Component
public class SessionTimeoutInterceptor implements HandlerInterceptor {

    /** メッセージプロパティ */
    @Autowired
    private MessageSource messageSource;

    /** ログユーティリティ */
    @Autowired
    private LogUtil logUtil;

    /**
     *
     * リクエストが処理される前にセッションの有効性をチェック。
     *
     * セッションが存在しない場合、またはログインユーザーがセッションに設定されていない場合は、
     * エラーログを記録し、ログイン画面にリダイレクト。
     *
     * @author 大塚 月愛
     *
     * @param request HTTPリクエスト
     * @param response HTTPレスポンス
     * @param handler リクエストを処理するハンドラー
     *
     * @return セッションが有効ならtrue、タイムアウトしているならfalse
     *
     * @throws Exception セッションの確認中にエラーが発生した場合
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        // セッションを確認
        HttpSession session = request.getSession(false);
        // セッションが存在しない場合（タイムアウトを意味する）
        if (session == null || session.getAttribute("loginUser") == null) {

            // エラーログをLogUtilを使ってデータベースに追加
            logUtil.addLog(LogDomain.CODE_LOG_SECTION_ERROR, "セッションタイムアウト", "SessionTimeout",
                    "SYSTEM_USER", Thread.currentThread().getStackTrace()[1].getClassName());

            String exceptionErrorMessage = messageSource.getMessage(MessageDomain.VALID_KEY_ERROR0022, null,
                    Locale.JAPAN);

            // RedirectAttributes を使ってメッセージをフラッシュスコープに追加
            request.getSession().setAttribute("exceptionErrorMessage", exceptionErrorMessage);

            // ログイン画面にリダイレクト
            //本番
            response.sendRedirect("/file_management_system/");
            //ローカル
            response.sendRedirect("/");

            return false;

        }
        // セッションが有効な場合は処理を続行
        return true;
    }
}
