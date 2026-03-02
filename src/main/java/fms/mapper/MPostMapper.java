package fms.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DataIntegrityViolationException;

import fms.entity.MPost;

/**
 * 役職マッパー
 *
 * @author 安藤 優海
 *
 */
@Mapper
public interface MPostMapper {

    /**
     * 役職マスタ情報取得
     *
     * @author 安藤 優海
     *
     * @return 役職マスタリスト
     */
    List<MPost> getMPost();

    /**
     * 役職マスタ削除
     *
     * @author 安藤 優海
     *
     * @param postId 役職ID
     *
     * @return 削除可否
     */
    boolean deleteMPost(@Param("postId") Integer postId) throws DataIntegrityViolationException;

    /**
     * 役職名取得
     *
     * @author 安藤 優海
     *
     * @param postId 役職ID
     *
     * @return 役職マスタエンティティ
     */
    MPost getPostName(@Param("postId") Integer postId);

    /**
     * 役職名重複チェック
     *
     * @author 安藤 優海
     *
     * @param postName 役職名
     *
     * @return 0:重複していない 1:重複している
     */
    Integer checkPostName(@Param("postName") String postName);

    /**
     * 役職マスタ登録
     *
     * @author 安藤 優海
     *
     * @param mPost 役職マスタエンティティ
     *
     * @return 登録可否
     */
    boolean insertMPost(MPost mPost);

    /**
     * 役職マスタ情報取得(1件)
     *
     * @author 安藤 優海
     *
     * @param postName 役職名
     *
     * @return 役職マスタエンティティ
     */
    MPost mPostInfo(@Param("postId") Integer postId);

    /**
     * 役職マスタ更新
     *
     * @author 安藤 優海
     *
     * @param mPost 役職マスタエンティティ
     *
     * @return 更新可否
     */
    boolean updateMPost(MPost mPost);
}
