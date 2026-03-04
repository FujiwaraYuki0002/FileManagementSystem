package fms.service;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import fms.domain.LogDomain;
import fms.domain.MessageDomain;
import fms.entity.MPost;
import fms.entity.MUser;
import fms.form.PostForm;
import fms.mapper.MPostMapper;
import fms.util.DateUtil;
import fms.util.LogUtil;

/**
 * 役職サービス
 *
 * @author 安藤 優海
 */

@Service
public class PostService {

    /** 役職マッパー */
    @Autowired
    private MPostMapper mPostMapper;

    /** Dateユーティリティ */
    @Autowired
    private DateUtil dateUtil;

    /** メッセージプロパティ */
    @Autowired
    private MessageSource messageSource;

    /** ユーザーエンティティ */
    @Autowired
    private MUser mUser;

    /** ログエンティティ */
    @Autowired
    private LogUtil logUtil;

    /**
     * 役職マスタリストの情報取得
     *
     * @author 安藤 優海
     *
     * @return 役職リスト
     */
    public List<MPost> getMPostList() {

        List<MPost> postList = mPostMapper.getMPost();

        return postList;

    }

    /**
     * 役職マスタ削除
     *
     * @author 安藤 優海
     *
     * @param postId 役職ID
     * @param bindingResult エラーチェック
     */
    public void isMPostDelete(Integer postId, BindingResult bindingResult) {

        boolean isPostDelete = true;

        try {

            // 削除処理
            isPostDelete = mPostMapper.deleteMPost(postId);

        } catch (DataIntegrityViolationException e) {

            // リザルトに外部参照キーエラーを登録
            bindingResult.addError(new FieldError(bindingResult.getObjectName(), "postName",
                    messageSource.getMessage(MessageDomain.VALID_KEY_ERROR0018, new String[] { "ユーザー" },
                            Locale.JAPAN)));

            // エラーログ登録
            logUtil.addLog(LogDomain.CODE_LOG_SECTION_ERROR, "外部参照キーエラー", MessageDomain.VALID_KEY_ERROR0018,
                    mUser.getUserId(), Thread.currentThread().getStackTrace()[1].getClassName());

        }
        // 削除済みだった（エラー）場合
        if (!isPostDelete) {

            // リザルトに削除済みエラーを登録
            bindingResult.addError(new FieldError(bindingResult.getObjectName(), "postName",
                    messageSource.getMessage(MessageDomain.VALID_KEY_ERROR0011, null, Locale.JAPAN)));

            // エラーログ登録
            logUtil.addLog(LogDomain.CODE_LOG_SECTION_ERROR, "削除済みエラー", MessageDomain.VALID_KEY_ERROR0011,
                    mUser.getUserId(), Thread.currentThread().getStackTrace()[1].getClassName());
        }
    }

    /**
     * 重複チェック
     *
     * @author 安藤 優海
     *
     * @param postForm 役職フォーム
     * @param bindingResult エラーチェック
     */
    public void checkMPost(PostForm postForm, BindingResult bindingResult) {

        // 重複チェック
        if (mPostMapper.checkPostName(postForm.getPostName()) > 0) {

            // リザルトに重複チェックエラーを登録
            bindingResult.addError(new FieldError(bindingResult.getObjectName(), "postcheck",
                    messageSource.getMessage(MessageDomain.VALID_KEY_ERROR0010, new String[] { postForm.getPostName() },
                            Locale.JAPAN)));

            // エラーログ登録
            logUtil.addLog(LogDomain.CODE_LOG_SECTION_ERROR, "重複チェックエラー", MessageDomain.VALID_KEY_ERROR0010,
                    mUser.getUserId(), Thread.currentThread().getStackTrace()[1].getClassName());
        }
    }

    /**
     * 役職マスタ登録
     *
     * @author 安藤 優海
     *
     * @param postForm 役職フォーム
     */
    public void mPostInsert(PostForm postForm) {

        // 役職マスタ登録
        MPost mPost = new MPost();
        mPost.setPostName(postForm.getPostName());
        mPost.setFirstCreateDate(dateUtil.getToday());
        mPost.setLastModifiedDate(dateUtil.getToday());
        mPost.setLastModifiedUser(mUser.getUserId());

        // 登録処理
        mPostMapper.insertMPost(mPost);

    }

    /**
     * 役職更新情報をフォームにセット
     *
     * @author 安藤 優海
     *
     * @param postForm 役職フォーム
     */
    public void setMPostForm(PostForm postForm, BindingResult bindingResult) {

        MPost mPost = mPostMapper.mPostInfo(postForm.getPostId());

        if (mPost == null) {

            // リザルトに削除済みエラーを登録
            bindingResult.addError(new FieldError(bindingResult.getObjectName(), "teamName",
                    messageSource.getMessage(MessageDomain.VALID_KEY_ERROR0011, null, Locale.JAPAN)));

            // エラーログ登録
            logUtil.addLog(LogDomain.CODE_LOG_SECTION_ERROR, "削除済みエラー", MessageDomain.VALID_KEY_ERROR0011,
                    mUser.getUserId(), Thread.currentThread().getStackTrace()[1].getClassName());

            return;
        }

        postForm.setPostName(mPost.getPostName());
        postForm.setVersion(mPost.getVersion());
        postForm.setScreenId("post-update");

    }

    /**
     * 役職マスタ更新
     *
     * @author 安藤 優海
     *
     * @param postForm 役職フォーム
     */
    public void mPostUpdate(PostForm postForm) {

        // 役職マスタ更新
        MPost mPost = new MPost();

        mPost.setPostId(postForm.getPostId());
        mPost.setPostName(postForm.getPostName());
        mPost.setLastModifiedDate(dateUtil.getToday());
        mPost.setLastModifiedUser(mUser.getUserId());
        mPost.setVersion(postForm.getVersion());

        mPostMapper.updateMPost(mPost);

    }
}
