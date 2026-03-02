package fms.controller.menu;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import fms.form.PasswordForm;
import fms.service.TExclusiveControlService;

/**
 * メニューコントローラー
 *
 * @author 安藤 優海
 */
@Controller
@RequestMapping("/menu")
public class MenuController {

    /** 排他ロックサービス */
    @Autowired
    private TExclusiveControlService tExclusiveControlService;

    /** session削除用配列 */
    public final static String[] SCREEN_IDS = { "screenId", "fileId", "userId", "teamId", "postId" };

    /**
     * ファイル検索画面へ遷移(管理者、一般権限)
     *
     * @author 安藤 優海
     *
     * @param httpSession
     *
     * @return ファイル検索画面
     */
    @RequestMapping(path = "/file/search")
    public String fileSearch(HttpSession httpSession) {

        // 排他ロック削除
        tExclusiveControlService.isTExclusiveControlDelete();

        // 画面IDとそれぞれのIDをセッションから削除
        for (String id : SCREEN_IDS) {
            httpSession.removeAttribute(id);
        }
        return "redirect:/file/search";
    }

    /**
     * ファイル管理画面へ遷移(管理者、一般権限)
     *
     * @author 安藤 優海
     *
     * @param httpSession
     *
     * @return ファイル管理画面
     */
    @RequestMapping(path = "/file/index")
    public String fileIndex(HttpSession httpSession) {

        // 排他ロック削除
        tExclusiveControlService.isTExclusiveControlDelete();

        // 画面IDとそれぞれのIDをセッションから削除
        for (String id : SCREEN_IDS) {
            httpSession.removeAttribute(id);
        }
        return "redirect:/file/index";
    }

    /**
     * 所属管理画面へ遷移(管理者権限)
     *
     * @author 安藤 優海
     *
     * @param httpSession
     *
     * @return 所属管理画面
     */
    @RequestMapping(path = "/team/index")
    public String team(HttpSession httpSession) {

        // 排他ロック削除
        tExclusiveControlService.isTExclusiveControlDelete();

        // 画面IDとそれぞれのIDをセッションから削除
        for (String id : SCREEN_IDS) {
            httpSession.removeAttribute(id);
        }
        return "redirect:/team/index";
    }

    /**
    * 役職管理画面へ遷移(管理者権限)
    *
    * @author 安藤 優海
    *
    * @param httpSession
    *
    * @return 役職管理画面
    */
    @RequestMapping(path = "/post/index")
    public String post(HttpSession httpSession) {

        // 排他ロック削除
        tExclusiveControlService.isTExclusiveControlDelete();

        // 画面IDとそれぞれのIDをセッションから削除
        for (String id : SCREEN_IDS) {
            httpSession.removeAttribute(id);
        }
        return "redirect:/post/index";
    }

    /**
     * ユーザー管理画面へ遷移(管理者権限)
     *
     * @author 安藤 優海
     *
     * @param httpSession
     *
     * @return ユーザー管理画面
     */
    @RequestMapping(path = "/user/index")
    public String user(HttpSession httpSession) {

        // 排他ロック削除
        tExclusiveControlService.isTExclusiveControlDelete();

        // 画面IDとそれぞれのIDをセッションから削除
        for (String id : SCREEN_IDS) {
            httpSession.removeAttribute(id);
        }
        return "redirect:/user/index";
    }

    /**
     * パスワード変更画面へ遷移(管理者、一般権限)
     *
     * @author 安藤 優海
     *
     * @param passwordForm
     * @param httpSession
     *
     * @return パスワード変更画面
     */
    @RequestMapping(path = "/password/changePassword")
    public String userUpddate(@ModelAttribute PasswordForm passwordForm, HttpSession httpSession) {

        // 排他ロック削除
        tExclusiveControlService.isTExclusiveControlDelete();

        // 画面IDとそれぞれのIDをセッションから削除
        for (String id : SCREEN_IDS) {
            httpSession.removeAttribute(id);
        }
        return "redirect:/password/changePassword";
    }
}
