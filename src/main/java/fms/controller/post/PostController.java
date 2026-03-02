package fms.controller.post;

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
import fms.entity.MPost;
import fms.entity.MUser;
import fms.form.PostForm;
import fms.service.PostService;
import fms.service.TExclusiveControlService;
import fms.util.LogUtil;

/**
 * 役職管理コントローラー
 *
 * @author 安藤 優海
 */
@Controller
@RequestMapping("/post")
public class PostController {

    /** 役職サービス */
    @Autowired
    private PostService postService;

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
     * 役職管理画面 初期表示
     *
     * @author 安藤 優海
     *
     * @param postForm 役職フォーム
     * @param model モデル
     * @param httpSession セッション
     *
     * @return 役職管理画面
     */
    @RequestMapping(path = "/index")
    public String mPostindex(@ModelAttribute PostForm postForm, Model model, HttpSession httpSession) {

        // 操作ログ登録
        logUtil.addLog(LogDomain.CODE_LOG_SECTION_OPE, "役職管理", "POST_INDEX",
                mUser.getUserId(), Thread.currentThread().getStackTrace()[1].getClassName());

        // ☆更新の戻るボタンからの遷移
        Integer postId = (Integer) httpSession.getAttribute("postId");
        postForm.setPostId(postId);

        // 役職リストを取得
        List<MPost> mPostList = postService.getMPostList();

        model.addAttribute("mPostList", mPostList);

        return "post/index";
    }

    /**
     * 役職管理画面 役職削除
     *
     * @author 安藤 優海
     *
     * @param postForm 役職フォーム
     * @param bindingResult エラーチェック
     * @param model モデル
     * @param redirectAttributes リダイレクト用スコープ
     * @param httpSession セッション
     * @param page ページ番号（削除後のページ保持のため使用）
     *
     * @return 役職管理画面
     */
    @RequestMapping(path = "/delete", method = RequestMethod.POST)
    public String mPostDelete(@Valid @ModelAttribute PostForm postForm, BindingResult bindingResult, Model model,
            RedirectAttributes redirectAttributes, HttpSession httpSession, @Param("page") Integer page) {

        // 操作ログ登録
        logUtil.addLog(LogDomain.CODE_LOG_SECTION_OPE, "役職削除", MessageDomain.PROP_KEY_MESSAGE0003,
                mUser.getUserId(), Thread.currentThread().getStackTrace()[1].getClassName());

        // 排他ロック確認
        tExclusiveControlService.checkDeleteExclusiveControl("post-update", postForm.getPostId(), bindingResult);

        // 排他ロックがかかっている場合、エラーアラートを表示
        if (bindingResult.hasErrors()) {

            // 役職リスト取得
            List<MPost> mPostList = postService.getMPostList();

            model.addAttribute("mPostList", mPostList);

            return "post/index";
        }

        // 削除処理
        postService.isMPostDelete(postForm.getPostId(), bindingResult);

        // 削除済みの場合、エラーアラートを表示
        if (bindingResult.hasErrors()) {

            // 役職リスト取得
            List<MPost> mPostList = postService.getMPostList();

            model.addAttribute("mPostList", mPostList);

            return "post/index";
        }

        // 役職IDをセッションに保存
        httpSession.setAttribute("postId", postForm.getPostId());

        // エラーではない場合、削除処理。完了メッセージを表示する
        String message = messageSource.getMessage(MessageDomain.PROP_KEY_MESSAGE0003, new String[] { "役職" },
                Locale.JAPAN);

        redirectAttributes.addFlashAttribute("completeMessage", message);

        // 元のページ番号をリダイレクトで渡す
        redirectAttributes.addFlashAttribute("page", page);

        return "redirect:/post/index";
    }
}
