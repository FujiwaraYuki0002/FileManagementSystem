package fms.controller.login;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import fms.domain.LogDomain;
import fms.domain.MessageDomain;
import fms.dto.UserDto;
import fms.entity.MUser;
import fms.form.LoginForm;
import fms.service.LoginService;
import fms.service.PostService;
import fms.service.TExclusiveControlService;
import fms.service.TeamService;
import fms.service.UserService;
import fms.util.LogUtil;

/**
 * ログインコントローラー
 *
 * @author 髙橋 真澄
 */
@Controller
public class LoginController {

    /** ログインサービス */
    @Autowired
    private LoginService loginService;

    /** メッセージプロパティ */
    @Autowired
    private MessageSource messageSource;

    /** ユーザーエンティティ */
    @Autowired
    private MUser mUser;

    /** セッション */
    @Autowired
    private HttpSession httpSession;

    /** 排他ロックサービス */
    @Autowired
    private TExclusiveControlService tExclusiveControlService;

    /** ログユーティリティ */
    @Autowired
    private LogUtil logUtil;

    /** ユーザーサービス */
    @Autowired
    private UserService userService;

    /** 役職サービス */
    @Autowired
    private PostService postService;

    /** 所属サービス */
    @Autowired
    private TeamService teamService;

    /**
     * ログイン画面 表示
     *
     * @author 髙橋 真澄
     *
     * @param loginForm ログインフォーム
     *
     * @return ログイン画面
     */
    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String login(@ModelAttribute LoginForm loginForm, Model model, HttpSession session,
            HttpServletRequest request) {

        // セッションからメッセージを取得し、リクエストスコープに追加
        String exceptionErrorMessage = (String) session.getAttribute("exceptionErrorMessage");
        if (exceptionErrorMessage != null) {
            model.addAttribute("exceptionErrorMessage", exceptionErrorMessage);
        }

        // セッション情報を削除
        httpSession.invalidate();

        // ログイン時にホスト名を取得し、紐づく排他ロックを解除
        // (途中で画面が閉じられた時用の応急処置)
        tExclusiveControlService.isTExclusiveControlDelete();

        return "login/login";
    }

    /**
     *
     * ログイン情報チェック 表示
     *
     * @author 髙橋 真澄
     *
     * @param loginForm ログインフォーム
     * @param bindingResult 入力チェック
     * @param model モデル
     * @param httpServletRequest リクエスト
     *
     * @return 正常;ファイル検索画面 異常:ログイン画面
     */
    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String loginCheck(@Valid LoginForm loginForm, BindingResult bindingResult, Model model,
            HttpServletRequest httpServletRequest) {

        List<String> errorMessageList = new ArrayList<>();

        // 入力チェックエラーがあったか
        if (bindingResult.hasErrors()) {

            // 入力チェックエラーがあった場合

            // フィールドに関連するエラーを全取得
            List<FieldError> errors = bindingResult.getFieldErrors();

            // エラーリスト内に"Pattern"のエラーが存在するかどうか
            boolean hasPatternError = errors.stream()
                    .anyMatch(error -> error.getCode().equals("Pattern"));

            // エラーリスト内に"NotBlank"のエラーが存在するかどうか
            boolean hasNotBlankError = errors.stream()
                    .anyMatch(error -> error.getCode().equals("NotBlank"));

            // "Pattern"のエラーが存在する場合
            if (hasPatternError) {

                // メッセージをリストに追加
                errorMessageList.add(messageSource.getMessage(MessageDomain.VALID_KEY_ERROR0004,
                        new String[] { "IDまたはパスワード" }, Locale.JAPAN));
            }

            // "NotBlank"のエラーが存在する場合
            if (hasNotBlankError) {

                // メッセージをリストに追加
                errorMessageList.add(messageSource.getMessage(MessageDomain.VALID_KEY_ERROR0001,
                        new String[] { "IDとパスワード" }, Locale.JAPAN));
            }

            // フォームとエラーメッセージをスコープに登録
            model.addAttribute("loginForm", loginForm);
            model.addAttribute("errorMessageList", errorMessageList);

            // ログイン画面に遷移
            return "login/login";
        }

        // エラーがなかったらIDを元にユーザー情報を問い合わせ
        MUser loginUser = loginService.loginCheck(loginForm);

        /**  @author 安藤 優海 */
        // IDとパスワードが正しければファイル検索画面に遷移
        if (loginUser != null) {

            // 操作ログ登録
            logUtil.addLog(LogDomain.CODE_LOG_SECTION_OPE, "ログイン", "LOGIN",
                    loginUser.getUserId(), Thread.currentThread().getStackTrace()[1].getClassName());

            // ログインした時に古いセッションIDを破棄
            httpSession.invalidate();

            // セッションを新規で作成する
            HttpSession newSession = httpServletRequest.getSession(true);

            // パスワード照合出来た場合は@Autowired用にmUserにもログイン情報を格納
            mUser.setUserId(loginUser.getUserId());
            mUser.setUserName(loginUser.getUserName());
            mUser.setRole(loginUser.getRole());

            UserDto userDto = new UserDto();

            userDto.setUserId(loginUser.getUserId());
            userDto.setUserName(loginUser.getUserName());
            userDto.setRole(loginUser.getRole());

            newSession.setAttribute("loginUser", userDto);
            /**  ここまで */

            // ファイル検索画面に遷移
            return "redirect:/file/search";
        }

        // パスワードが正しくなければエラーメッセージを追加
        errorMessageList.add(messageSource.getMessage(MessageDomain.VALID_KEY_ERROR0004,
                new String[] { "IDまたはパスワード" }, Locale.JAPAN));

        // フォームとエラーメッセージをスコープに登録
        model.addAttribute(errorMessageList);
        model.addAttribute("errorMessageList", errorMessageList);

        // ログイン画面に遷移
        return "login/login";
    }
}
