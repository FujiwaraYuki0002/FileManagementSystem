package fms.controller.post;

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
import fms.entity.MPost;
import fms.entity.MUser;
import fms.form.PostForm;
import fms.service.PostService;
import fms.service.TExclusiveControlService;
import fms.util.LogUtil;

/**
 * 役職登録・更新コントローラー
 *
 * @author 安藤 優海
 */
@Controller
@RequestMapping("/post")
public class PostRegistController {

    /** 役職サービス */
    @Autowired
    private PostService postService;

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
     * 役職登録・更新画面 初期表示
     *
     * @author 安藤 優海
     *
     * @param postForm 役職フォーム
     * @param bindingResult エラーチェック
     * @param model モデル
     * @param httpSession セッション
     *
     * @return 役職登録画面or役職更新画面
     */
    @RequestMapping(path = { "/insert", "/update" }, method = RequestMethod.POST)
    public String mPostUpdate(@Valid @ModelAttribute PostForm postForm, BindingResult bindingResult, Model model,
            HttpSession httpSession) {

        // 役職リストを取得
        List<MPost> mPostList = postService.getMPostList();

        model.addAttribute("mPostList", mPostList);

        // 登録の場合
        if (postForm.getPostId() == null) {

            model.addAttribute("insert", true); // 登録フラグ

            // 役職登録画面に遷移
            return "post/postRegist";
        }

        // 更新の場合

        // 役職の更新情報取得し、フォームに初期値をセット
        postService.setMPostForm(postForm, bindingResult);

        // 情報が見つからない、既に削除されている場合はエラーメッセージを表示
        if (postForm.getPostName() == null) {

            // 役職管理画面に遷移
            return "post/index";
        }

        // 画面IDと役職IDをセッションに保存
        httpSession.setAttribute("screenId", postForm.getScreenId());
        httpSession.setAttribute("postId", postForm.getPostId());

        // 排他ロックがかかっているか確認
        boolean exclusiveControl = tExclusiveControlService.checkUpdateExclusiveControl(postForm, bindingResult);

        // 排他ロックがかかっている場合(true)、エラーアラートを表示してフォームを非活性にする
        // 排他ロックがかかっていない場合(false)
        model.addAttribute("exclusiveControl", exclusiveControl);

        model.addAttribute("update", true); // 更新フラグ

        // 役職更新画面に遷移
        return "post/postRegist";
    }

    /**
     * 役職登録画面 登録
     *
     * @author 安藤 優海
     *
     * @param postForm 役職フォーム
     * @param bindingResult エラーチェック
     * @param redirectAttributes リダイレクト用スコープ
     * @param model モデル
     *
     * @return 正常:役職管理画面 異常:役職登録画面
     */
    @RequestMapping(path = "/insert", params = "insertButton", method = RequestMethod.POST)
    public String mPostInsertExecute(@Valid @ModelAttribute PostForm postForm, BindingResult bindingResult,
            RedirectAttributes redirectAttributes, Model model) {

        // 操作ログ登録
        logUtil.addLog(LogDomain.CODE_LOG_SECTION_OPE, "役職登録", MessageDomain.PROP_KEY_MESSAGE0001, mUser.getUserId(),
                Thread.currentThread().getStackTrace()[1].getClassName());

        // 役職リストを取得
        List<MPost> mPostList = postService.getMPostList();

        model.addAttribute("mPostList", mPostList);

        model.addAttribute("insert", true); // 登録フラグ

        // エラーの場合、エラーアラートを表示
        postService.checkMPost(postForm, bindingResult);
        if (bindingResult.hasErrors()) {

            // 役職登録画面に遷移
            return "post/postRegist";
        }

        // エラーではない場合、登録処理。完了メッセージを表示
        postService.mPostInsert(postForm);

        String message = messageSource.getMessage(MessageDomain.PROP_KEY_MESSAGE0001, new String[] { "役職" },
                Locale.JAPAN);

        model.addAttribute("completeMessage", message);

        // 役職登録画面に遷移
        return "post/postRegist";
    }

    /**
     * 役職登録・更新画面 「戻る」ボタン押下
     *
     * @author 安藤 優海
     *
     * @param httpSession セッション
     * @param redirectAttributes リダイレクト
     * @param HttpServletRequest サーバーリクエスト
     *
     * @return 役職管理画面
     */
    @RequestMapping(path = { "/updateBack", "/insertBack" }, method = RequestMethod.POST)
    public String updateBack(HttpSession httpSession, RedirectAttributes redirectAttributes,
            HttpServletRequest httpServletRequest) {

        // 登録画面からの遷移かどうか
        if (httpServletRequest.getRequestURI().equals("/file_management_system/post/insertBack") ||
                httpServletRequest.getRequestURI().equals("/post/insertBack")) {

            // その場合はIDを削除
            httpSession.removeAttribute("postId");
        }

        // 排他ロック削除
        tExclusiveControlService.isTExclusiveControlDelete();

        // 画面IDをセッションから削除
        httpSession.removeAttribute("screenId");

        // 役職管理画面へリダイレクト
        return "redirect:/post/index";
    }

    /**
     * 役職更新画面 更新
     *
     * @author 安藤 優海
     *
     * @param postForm 役職フォーム
     * @param bindingResult エラーチェック
     * @param redirectAttributes リダイレクト用スコープ
     * @param model モデル
     * @param httpSession セッション
     *
     * @return 正常:役職管理画面 異常:役職更新画面
     */
    @RequestMapping(path = "/update", params = "updateButton", method = RequestMethod.POST)
    public String mPostUpdateExecute(@Valid @ModelAttribute PostForm postForm, BindingResult bindingResult,
            RedirectAttributes redirectAttributes, Model model, HttpSession httpSession) {

        // 操作ログ登録
        logUtil.addLog(LogDomain.CODE_LOG_SECTION_OPE, "役職更新", MessageDomain.PROP_KEY_MESSAGE0002, mUser.getUserId(),
                Thread.currentThread().getStackTrace()[1].getClassName());

        // 役職リスト取得
        List<MPost> mPostList = postService.getMPostList();

        model.addAttribute("mPostList", mPostList);

        model.addAttribute("update", true); // 更新フラグ

        // 削除済みまたはエラーの場合、エラーアラートを表示
        postService.checkMPost(postForm, bindingResult);

        if (bindingResult.hasErrors()) {

            // 役職更新画面へ遷移
            return "post/postRegist";
        }

        // 更新処理
        postService.mPostUpdate(postForm);

        // 画面IDをセッションから削除
        httpSession.removeAttribute("screenId");

        // エラーではない場合、更新処理。完了メッセージを表示する
        String message = messageSource.getMessage(MessageDomain.PROP_KEY_MESSAGE0002, new String[] { "役職" },
                Locale.JAPAN);

        model.addAttribute("completeMessage", message);

        // 役職更新画面へ遷移
        return "post/postRegist";
    }
}
