package fms.controller.password;

import java.util.Locale;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import fms.domain.LogDomain;
import fms.domain.MessageDomain;
import fms.entity.MUser;
import fms.form.PasswordForm;
import fms.service.PasswordService;
import fms.service.TExclusiveControlService;
import fms.util.LogUtil;

/**
 * パスワードコントローラー
 *
 * @author 安藤 優海
 */
@Controller
@RequestMapping("/password")
public class PasswordController {

    /** メッセージプロパティ */
    @Autowired
    private MessageSource messageSource;

    /** パスワードサービス */
    @Autowired
    private PasswordService passwordService;

    /** ログユーティリティ */
    @Autowired
    private LogUtil logUtil;

    /** ユーザーエンティティ */
    @Autowired
    private MUser mUser;

    /** 排他ロックサービス */
    @Autowired
    private TExclusiveControlService tExclusiveControlService;

    /**
     * パスワード変更画面 初期表示
     *
     * @author 安藤 優海
     *
     * @param passwordForm パスワードフォーム
     * @param bindingResult エラーチェック
     * @param model モデル
     * @param httpSession セッション
     *
     * @return パスワード変更画面
     */
    @RequestMapping(path = "/changePassword")
    public String userUpddate(@ModelAttribute PasswordForm passwordForm, BindingResult bindingResult, Model model,
            HttpSession httpSession) {

        // 画面IDとユーザーIDをフォームにセット
        passwordForm.setScreenId("user-update"); //ユーザー更新に排他ロックをかけるため
        passwordForm.setUserId(mUser.getUserId());

        //画面IDとユーザーIDをセッションに保存
        httpSession.setAttribute("screenId", passwordForm.getScreenId());
        httpSession.setAttribute("userId", passwordForm.getUserId());

        // 排他ロックがかかっているか確認
        boolean exclusiveControl = tExclusiveControlService.checkUpdateExclusiveControl(passwordForm, bindingResult);

        // 排他ロックがかかっている場合(true)、エラーアラートを表示してフォームを非活性にする
        // 排他ロックがかかっていない場合(false)
        model.addAttribute("exclusiveControl", exclusiveControl);

        // パスワード変更画面に遷移
        return "password/changePassword";
    }

    /**
     *
     * パスワード変更 更新
     *
     * @author 安藤 優海
     *
     * @param passwordForm パスワードフォーム
     * @param bindingResult エラーチェック
     * @param httpSession セッション
     * @param redirectAttributes リダイレクト
     * @param mode モデル
     *
     * @return 正常:ファイル検索画面 異常:パスワード変更画面
     */
    @RequestMapping(path = "/changePassword", params = "updateButton", method = RequestMethod.POST)
    public String completePassword(@Valid PasswordForm passwordForm, BindingResult bindingResult,
            HttpSession httpSession, RedirectAttributes redirectAttributes, Model model) {

        // 操作ログ登録
        logUtil.addLog(LogDomain.CODE_LOG_SECTION_OPE, "パスワード変更", "CHANGE_PASSWORD",
                mUser.getUserId(), Thread.currentThread().getStackTrace()[1].getClassName());

        // IDとパスワードの照合結果、新しいパスワードと確認用のパスワードの照合結果を取得
        boolean isIdAndPasswordCheck = passwordService.idAndPasswordCheck(passwordForm);
        boolean isNewPasswordCheck = passwordForm.getNewPassword().equals(passwordForm.getCheckPassword());

        // IDとパスワードの照合がfalseの場合
        if (!isIdAndPasswordCheck) {

            // パスワードが正しくなければエラーを追加
            bindingResult.addError(new FieldError(bindingResult.getObjectName(), "password",
                    messageSource.getMessage(MessageDomain.VALID_KEY_ERROR0004, new String[] { "パスワード" },
                            Locale.JAPAN)));

            // エラーログ登録
            logUtil.addLog(LogDomain.CODE_LOG_SECTION_ERROR, "パスワード照合エラー", MessageDomain.VALID_KEY_ERROR0004,
                    mUser.getUserId(), Thread.currentThread().getStackTrace()[1].getClassName());
        }

        // 新しいパスワードと確認用パスワードを照合がfalseの場合
        if (!isNewPasswordCheck) {

            // 新しいパスワードと確認用パスワードが一致しなければエラーメッセージを追加
            bindingResult.addError(new FieldError(bindingResult.getObjectName(), "newPassword",
                    messageSource.getMessage(MessageDomain.VALID_KEY_ERROR0012, new String[] { "確認用パスワード" },
                            Locale.JAPAN)));

            // エラーログ登録
            logUtil.addLog(LogDomain.CODE_LOG_SECTION_ERROR, "新しいパスワード照合エラー", MessageDomain.VALID_KEY_ERROR0012,
                    mUser.getUserId(), Thread.currentThread().getStackTrace()[1].getClassName());
        }

        // エラーがある場合
        if (bindingResult.hasErrors()) {

            // パスワード変更画面に遷移
            return "password/changePassword";
        }

        // エラーがない場合

        // パスワード変更処理
        passwordService.passwordUpdate(passwordForm);

        // 排他ロック削除
        tExclusiveControlService.isTExclusiveControlDelete();

        // 画面IDと役職IDをセッションから削除
        httpSession.removeAttribute("screenId");
        httpSession.removeAttribute("userId");

        // 遷移後の画面で表示する完了メッセージを追加する
        String message = messageSource.getMessage(MessageDomain.PROP_KEY_MESSAGE0002, new String[] { "パスワード" },
                Locale.JAPAN);

        model.addAttribute("completeMessage", message);

        // パスワード変更画面に遷移
        return "password/changePassword";
    }
}