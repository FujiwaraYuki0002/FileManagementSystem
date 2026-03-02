package fms.controller.user;

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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import fms.domain.LogDomain;
import fms.domain.MessageDomain;
import fms.domain.UserDomain;
import fms.entity.MPost;
import fms.entity.MTeam;
import fms.entity.MUser;
import fms.form.UserForm;
import fms.service.PostService;
import fms.service.TExclusiveControlService;
import fms.service.TeamService;
import fms.service.UserService;
import fms.util.LogUtil;

/**
 * ユーザ更新コントローラー
 *
 * @author 大塚 月愛
 */
@Controller
@RequestMapping("/user")
public class UserRegistController {

    /** ユーザーサービス */
    @Autowired
    private UserService userService;

    /** 役職サービス */
    @Autowired
    private PostService postService;

    /** 所属サービス */
    @Autowired
    private TeamService teamService;

    /** 排他ロックサービス */
    @Autowired
    private TExclusiveControlService tExclusiveControlService;

    /** ユーザーエンティティ */
    @Autowired
    private MUser mUser;

    /** メッセージプロパティ */
    @Autowired
    private MessageSource messageSource;

    /** ログユーティリティ */
    @Autowired
    private LogUtil logUtil;

    /**
     * ユーザ登録画面・ユーザー更新画面 初期表示
     *
     * @author 大塚 月愛
     *
     * @param userForm ユーザーフォーム
     * @param bindingResult エラーチェック
     * @param userId ユーザーID
     * @param model モデル
     * @param httpSession
     *
     * @return ユーザー登録画面 or ユーザー更新画面
     */
    @RequestMapping(path = { "/insert", "/update" }, method = RequestMethod.POST)
    public String mUserUpdate(@Valid @ModelAttribute UserForm userForm, BindingResult bindingResult, String userId,
            Model model, HttpSession httpSession) {

        // 登録の場合
        if (userForm.getUserId() == null) {

            // フォームにScreenIdをセット
            userForm.setScreenId("user-update");
            // 所属リストを取得
            List<MTeam> mTeamList = teamService.getMTeamList();
            // 役職リストを取得
            List<MPost> mPostList = postService.getMPostList();

            model.addAttribute("mTeamList", mTeamList);
            model.addAttribute("mPostList", mPostList);
            model.addAttribute("insert", true); // 登録フラグ

            return "user/userRegist";
        }
        model.addAttribute("update", true); // 更新フラグ

        // ユーザーの更新情報を取得し、フォームに初期値をセット
        userService.setMUserForm(userForm);

        // 画面IDとユーザーIDをセッションに保存
        httpSession.setAttribute("screenId", userForm.getScreenId());
        httpSession.setAttribute("userId", userId);

        // 排他ロックがかかっているか確認
        boolean exclusiveControl = tExclusiveControlService.checkUpdateExclusiveControl(userForm, bindingResult);

        // 排他ロックがかかっている場合(true)、エラーアラートを表示
        // 排他ロックがかかっていない場合(false)
        model.addAttribute("exclusiveControl", exclusiveControl);

        // 所属リストを取得
        List<MTeam> mTeamList = teamService.getMTeamList();

        // 役職リストを取得
        List<MPost> mPostList = postService.getMPostList();

        // ユーザーが所属している所属リストを取得
        List<Integer> selectedTeamIds = userService.selectedTeamIds(UserDomain.RETIREMENT_FLG_NONE, userId);

        model.addAttribute("mTeamList", mTeamList);
        model.addAttribute("mPostList", mPostList);
        model.addAttribute("selectedTeamIds", selectedTeamIds);

        return "user/userRegist";
    }

    /**
     * ユーザ登録画面 登録
     *
     * @author 大塚 月愛
     *
     * @param userForm ユーザーフォーム
     * @param bindingResult エラーチェック
     * @param model モデル
     * @param redirectAttributes リダイレクト用スコープ
     *
     * @return 正常:ユーザー管理画面 異常:ユーザー登録画面
     */
    @RequestMapping(path = "/insert", params = "insertButton", method = RequestMethod.POST)
    public String mUserInsertExecute(
            @Valid @ModelAttribute UserForm userForm, BindingResult bindingResult, Model model,
            RedirectAttributes redirectAttributes) {

        // 操作ログ登録
        logUtil.addLog(LogDomain.CODE_LOG_SECTION_OPE, "ユーザー登録", MessageDomain.PROP_KEY_MESSAGE0002, mUser.getUserId(),
                Thread.currentThread().getStackTrace()[1].getClassName());

        // 所属リストを取得
        List<MTeam> mTeamList = teamService.getMTeamList();
        // 役職リストを取得
        List<MPost> mPostList = postService.getMPostList();

        model.addAttribute("mTeamList", mTeamList);
        model.addAttribute("mPostList", mPostList);

        // 入力チェック・重複チェック
        userService.insertCheckMUser(userForm, bindingResult);

        // エラーの場合エラーアラートを表示
        if (bindingResult.hasErrors()) {

            // ユーザーが所属している所属リストを取得
            List<Integer> selectedTeamIds = new ArrayList<>();

            for (Integer teamId : userForm.getTeamId()) {
                selectedTeamIds.add(teamId);
            }

            model.addAttribute("insert", true);
            model.addAttribute("mTeamList", mTeamList);
            model.addAttribute("selectedTeamIds", selectedTeamIds);
            return "user/userRegist";
        }

        // エラーではない場合、登録処理。完了メッセージを表示
        userService.mUserInsert(userForm);

        String message = messageSource.getMessage(MessageDomain.PROP_KEY_MESSAGE0001, new String[] { "ユーザー" },
                Locale.JAPAN);

        redirectAttributes.addFlashAttribute("completeMessage", message);

        return "redirect:/user/index";
    }

    /**
     * ユーザー更新画面 「戻る」ボタン押下
     *
     * @author 大塚 月愛
     *
     * @param httpSession セッション
     * @param HttpServletRequest サーバーリクエスト
     *
     * @return ユーザー管理画面
     */
    @RequestMapping(path = { "/updateBack", "/insertBack" }, method = RequestMethod.POST)
    public String updateBack(HttpSession httpSession, HttpServletRequest httpServletRequest) {

        // 登録画面からの遷移かどうか
        if (httpServletRequest.getRequestURI().equals("/file_management_system/user/insertBack") ||
                httpServletRequest.getRequestURI().equals("/user/insertBack")) {

            // その場合はIDを削除
            httpSession.removeAttribute("userId");
        }

        // 排他ロック削除
        tExclusiveControlService.isTExclusiveControlDelete();

        // 画面IDとユーザーIDをセッションから削除
        httpSession.removeAttribute("screenId");

        return "redirect:/user/index";
    }

    /**
     * ユーザー更新画面 更新
     *
     * @author 大塚 月愛
     *
     * @param userForm ユーザーフォーム
     * @param bindingResult エラーチェック
     * @param model モデル
     * @param redirectAttributes リダイレクト用スコープ
     * @param httpSession
     *
     * @return ユーザー管理画面 or ユーザー更新画面（エラーの場合）
     */
    @RequestMapping(path = "/update", params = "updateButton", method = RequestMethod.POST)
    public String mUserUpdateExecute(
            @Valid @ModelAttribute UserForm userForm, BindingResult bindingResult, Model model,
            RedirectAttributes redirectAttributes, HttpSession httpSession) {

        // 操作ログ登録
        logUtil.addLog(LogDomain.CODE_LOG_SECTION_OPE, "ユーザー更新", MessageDomain.PROP_KEY_MESSAGE0002, mUser.getUserId(),
                Thread.currentThread().getStackTrace()[1].getClassName());

        // 所属リストを取得
        List<MTeam> mTeamList = teamService.getMTeamList();
        // 役職リストを取得
        List<MPost> mPostList = postService.getMPostList();

        model.addAttribute("mTeamList", mTeamList);
        model.addAttribute("mPostList", mPostList);

        // 入力チェックか重複チェックがエラーの場合、エラーアラートを表示
        userService.updateCheckMUser(userForm, bindingResult);

        if (bindingResult.hasErrors()) {

            // ユーザーが所属している所属リストを取得
            List<Integer> selectedTeamIds = new ArrayList<>();

            for (Integer teamId : userForm.getTeamId()) {
                selectedTeamIds.add(teamId);
            }

            model.addAttribute("mTeamList", mTeamList);
            model.addAttribute("mPostList", mPostList);
            model.addAttribute("selectedTeamIds", selectedTeamIds);

            model.addAttribute("update", true); // 更新フラグ

            return "user/userRegist";
        }

        // 更新処理
        userService.mUserUpdate(userForm);

        // 排他ロック削除
        tExclusiveControlService.isTExclusiveControlDelete();

        // 画面IDと役職IDをセッションから削除
        httpSession.removeAttribute("screenId");
        httpSession.removeAttribute("userId");

        // エラーではない場合、更新処理。完了メッセージを表示
        String message = messageSource.getMessage(MessageDomain.PROP_KEY_MESSAGE0002, new String[] { "ユーザー" },
                Locale.JAPAN);

        redirectAttributes.addFlashAttribute("completeMessage", message);

        return "redirect:/user/index";
    }
}
