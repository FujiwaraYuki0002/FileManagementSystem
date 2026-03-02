package fms.controller.team;

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
import fms.entity.MTeam;
import fms.entity.MUser;
import fms.form.TeamForm;
import fms.service.TExclusiveControlService;
import fms.service.TeamService;
import fms.util.LogUtil;

/**
 * 所属登録・更新コントローラー
 *
 * @author 安藤 優海
 */
@Controller
@RequestMapping("/team")
public class TeamRegistController {

    /** 所属サービス */
    @Autowired
    private TeamService teamService;

    /** メッセージプロパティ */
    @Autowired
    private MessageSource messageSource;

    /** ユーザーエンティティ */
    @Autowired
    private MUser mUser;

    /** ログエンティティ */
    @Autowired
    private LogUtil logUtil;

    /** 排他ロックサービス */
    @Autowired
    private TExclusiveControlService tExclusiveControlService;

    /**
     * 所属登録・更新画面 初期表示
     *
     * @author 安藤 優海
     *
     * @param teamForm 所属フォーム
     * @param bindingResult エラーチェック
     * @param model モデル
     * @param httpSession セッション
     *
     * @return 所属登録画面or所属更新画面
     */
    @RequestMapping(path = { "/insert", "/update" }, method = RequestMethod.POST)
    public String mTeamUpdate(@Valid @ModelAttribute TeamForm teamForm, BindingResult bindingResult, Model model,
            HttpSession httpSession) {

        // 登録の場合
        if (teamForm.getTeamId() == null) {

            // 所属リストを取得
            List<MTeam> mTeamList = teamService.getMTeamList();

            model.addAttribute("mTeamList", mTeamList);

            model.addAttribute("insert", true); // 登録フラグ

            return "team/teamRegist";
        }

        // 更新の場合

        // 所属の更新情報取得し、フォームに初期値をセット
        teamService.setMTeamForm(teamForm, bindingResult);

        if (teamForm.getTeamName() == null) {

            // 所属リストを取得
            List<MTeam> mTeamList = teamService.getMTeamList();

            model.addAttribute("mTeamList", mTeamList);

            return "team/index";
        }

        // 画面IDと所属IDをセッションに保存
        httpSession.setAttribute("screenId", teamForm.getScreenId());
        httpSession.setAttribute("teamId", teamForm.getTeamId());

        // 所属リスト取得
        List<MTeam> mTeamList = teamService.getMTeamList();

        model.addAttribute("mTeamList", mTeamList);

        // 排他ロックがかかっているか確認
        boolean exclusiveControl = tExclusiveControlService.checkUpdateExclusiveControl(teamForm, bindingResult);

        // 排他ロックがかかっている場合(true)、エラーアラートを表示してフォームを非活性にする
        // 排他ロックがかかっていない場合(false)
        model.addAttribute("exclusiveControl", exclusiveControl);

        model.addAttribute("update", true); // 更新フラグ

        return "team/teamRegist";
    }

    /**
     * 所属登録画面 登録
     *
     * @author 安藤 優海
     *
     * @param teamForm 所属フォーム
     * @param bindingResult エラーチェック
     * @param redirectAttributes リダイレクト用スコープ
     * @param model モデル
     *
     * @return 正常:所属管理画面 異常:所属登録画面
     */
    @RequestMapping(path = "/insert", params = "insertButton", method = RequestMethod.POST)
    public String mTeamInsertExecute(@Valid @ModelAttribute TeamForm teamForm, BindingResult bindingResult,
            RedirectAttributes redirectAttributes, Model model) {

        // 操作ログ登録
        logUtil.addLog(LogDomain.CODE_LOG_SECTION_OPE, "所属登録", MessageDomain.PROP_KEY_MESSAGE0001, mUser.getUserId(),
                Thread.currentThread().getStackTrace()[1].getClassName());

        // チェックか重複チェックがエラーの場合、エラーアラートを表示
        teamService.checkMTeam(teamForm, bindingResult);
        if (bindingResult.hasErrors()) {

            // 所属リストを取得
            List<MTeam> mTeamList = teamService.getMTeamList();

            model.addAttribute("mTeamList", mTeamList);

            model.addAttribute("insert", true); // 登録フラグ

            return "team/teamRegist";
        }

        // ★エラーではない場合、登録処理。完了メッセージを表示
        teamService.mTeamInsert(teamForm);

        String message = messageSource.getMessage(MessageDomain.PROP_KEY_MESSAGE0001, new String[] { "所属" },
                Locale.JAPAN);

        model.addAttribute("completeMessage", message);

        // ★所属リスト取得
        List<MTeam> mTeamList = teamService.getMTeamList();

        model.addAttribute("mTeamList", mTeamList);

        model.addAttribute("insert", true); // 更新フラグ

        return "team/teamRegist";
    }

    /**
     * 所属登録・更新画面 「戻る」ボタン押下
     *
     * @author 安藤 優海
     *
     * @param httpSession セッション
     * @param redirectAttributes リダイレクト
     * @param HttpServletRequest サーバーリクエスト
     *
     * @return 所属管理画面
     */
    @RequestMapping(path = { "/updateBack", "/insertBack" }, method = RequestMethod.POST)
    public String updateBack(HttpSession httpSession, RedirectAttributes redirectAttributes,
            HttpServletRequest httpServletRequest) {

        // 登録画面からの遷移かどうか
        if (httpServletRequest.getRequestURI().equals("/file_management_system/team/insertBack") ||
                httpServletRequest.getRequestURI().equals("/team/insertBack")) {

            // その場合はIDを削除
            httpSession.removeAttribute("teamId");
        }

        // 排他ロック削除
        tExclusiveControlService.isTExclusiveControlDelete();

        // 画面IDをセッションから削除
        httpSession.removeAttribute("screenId");

        return "redirect:/team/index";
    }

    /**
     * 所属更新画面 更新
     *
     * @author 安藤 優海
     *
     * @param teamForm 所属フォーム
     * @param bindingResult エラーチェック
     * @param redirectAttributes リダイレクト用スコープ
     * @param model モデル
     * @param httpSession セッション
     *
     * @return 正常:所属管理画面 異常:所属更新画面
     */
    @RequestMapping(path = "/update", params = "updateButton", method = RequestMethod.POST)
    public String mTeamUpdateExecute(@Valid @ModelAttribute TeamForm teamForm, BindingResult bindingResult,
            RedirectAttributes redirectAttributes, Model model, HttpSession httpSession) {

        // 操作ログ登録
        logUtil.addLog(LogDomain.CODE_LOG_SECTION_OPE, "所属更新", MessageDomain.PROP_KEY_MESSAGE0002, mUser.getUserId(),
                Thread.currentThread().getStackTrace()[1].getClassName());

        // 削除済みまたは入力チェックか重複チェックがエラーの場合、エラーアラートを表示
        teamService.checkMTeam(teamForm, bindingResult);
        if (bindingResult.hasErrors()) {

            // 所属リスト取得
            List<MTeam> mTeamList = teamService.getMTeamList();

            model.addAttribute("mTeamList", mTeamList);

            model.addAttribute("update", true); // 更新フラグ

            return "team/teamRegist";
        }

        // 更新処理
        teamService.mTeamUpdate(teamForm);

        // 排他ロック削除
        tExclusiveControlService.isTExclusiveControlDelete();

        // 画面IDと所属IDをセッションから削除
        httpSession.removeAttribute("screenId");

        // ★エラーではない場合、更新処理。完了メッセージを表示する
        String message = messageSource.getMessage(MessageDomain.PROP_KEY_MESSAGE0002, new String[] { "所属" },
                Locale.JAPAN);

        model.addAttribute("completeMessage", message);

        // ★所属リスト取得
        List<MTeam> mTeamList = teamService.getMTeamList();

        model.addAttribute("mTeamList", mTeamList);

        model.addAttribute("update", true); // 更新フラグ

        return "team/teamRegist";
    }
}
