package fms.controller.team;

import java.util.List;
import java.util.Locale;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.apache.ibatis.annotations.Param;
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
 * 所属管理コントローラー
 *
 * @author 安藤 優海
 */
@Controller
@RequestMapping("/team")
public class TeamController {

    /** 所属サービス */
    @Autowired
    private TeamService teamService;

    /** 排他ロックサービス */
    @Autowired
    private TExclusiveControlService tExclusiveControlService;

    /** メッセージプロパティ */
    @Autowired
    private MessageSource messageSource;

    /** ログエンティティ */
    @Autowired
    private LogUtil logUtil;

    /** ユーザーエンティティ */
    @Autowired
    private MUser mUser;

    /**
     * 所属管理画面 初期表示
     *
     * @author 安藤 優海
     *
     * @param teamForm 所属フォーム
     * @param model モデル
     * @param httpSession セッション
     *
     * @return 所属管理画面
     */
    @RequestMapping(path = "/index")
    public String mTeamindex(@ModelAttribute TeamForm teamForm, Model model, HttpSession httpSession) {

        // 操作ログ登録
        logUtil.addLog(LogDomain.CODE_LOG_SECTION_OPE, "所属管理", "TEAM_INDEX",
                mUser.getUserId(), Thread.currentThread().getStackTrace()[1].getClassName());

        // ☆更新の戻るボタンからの遷移
        Integer teamId = (Integer) httpSession.getAttribute("teamId");
        teamForm.setTeamId(teamId);

        // 所属リストを取得
        List<MTeam> mTeamList = teamService.getMTeamList();

        model.addAttribute("mTeamList", mTeamList);

        return "team/index";
    }

    /**
     * 所属管理画面 所属削除
     *
     * @author 安藤 優海
     *
     * @param teamForm 所属フォーム
     * @param bindingResult エラーチェック
     * @param model モデル
     * @param redirectAttributes リダイレクト用スコープ
     * @param httpSession セッション
     * @param page ページ番号（削除後のページ保持のため使用）
     *
     * @return 所属管理画面
     */
    @RequestMapping(path = "/delete", method = RequestMethod.POST)
    public String mTeamDelete(@Valid @ModelAttribute TeamForm teamForm, BindingResult bindingResult, Model model,
            RedirectAttributes redirectAttributes, HttpSession httpSession, @Param("page") Integer page) {

        // 操作ログ登録
        logUtil.addLog(LogDomain.CODE_LOG_SECTION_OPE, "所属削除", MessageDomain.PROP_KEY_MESSAGE0003,
                mUser.getUserId(), Thread.currentThread().getStackTrace()[1].getClassName());

        // 排他ロック確認
        tExclusiveControlService.checkDeleteExclusiveControl("team-update", teamForm.getTeamId(), bindingResult);

        // 排他ロックがかかっている場合、エラーアラートを表示
        if (bindingResult.hasErrors()) {

            // 所属リスト取得
            List<MTeam> mTeamList = teamService.getMTeamList();

            model.addAttribute("mTeamList", mTeamList);

            return "team/index";
        }

        // 削除処理
        teamService.isMTeamDelete(teamForm.getTeamId(), bindingResult);

        // 削除済みの場合、エラーアラートを表示
        if (bindingResult.hasErrors()) {

            // 所属リスト取得
            List<MTeam> mTeamList = teamService.getMTeamList();

            model.addAttribute("mTeamList", mTeamList);

            return "team/index";
        }

        // 所属IDをセッションに保存
        httpSession.setAttribute("teamId", teamForm.getTeamId());

        // エラーではない場合、削除処理。完了メッセージを表示する
        String message = messageSource.getMessage(MessageDomain.PROP_KEY_MESSAGE0003, new String[] { "所属" },
                Locale.JAPAN);

        redirectAttributes.addFlashAttribute("completeMessage", message);

        // 元のページ番号をリダイレクトで渡す
        redirectAttributes.addFlashAttribute("page", page);

        return "redirect:/team/index";
    }
}
