package fms.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import fms.entity.TExclusiveControl;

/**
 * 排他ロックマッパー
 *
 * @author 安藤 優海
 *
 */
@Mapper
public interface TExclusiveControlMapper {

    /**
     * 排他ロック取得
     *
     * @author 安藤 優海
     *
     * @param screenId
     * @param fileId
     * @param userId
     * @param teamId
     * @param postId
     *
     * @return 排他ロックエンティティ
     */
    TExclusiveControl getTExclusiveControl(@Param("screenId") String screenId,
            @Param("fileId") Integer fileId,
            @Param("userId") String userId,
            @Param("teamId") Integer teamId,
            @Param("postId") Integer postId);

    /**
     * 排他ロック登録
     *
     * @author 安藤 優海
     *
     * @param tExclusiveControl
     *
     * @return boolean
     */
    boolean insertTExclusiveControl(TExclusiveControl tExclusiveControl);

    /**
     * 排他ロック削除
     *
     * @author 安藤 優海
     *
     * @param hostName
     *
     * @return boolean
     */
    boolean deleteTExclusiveControl(@Param("hostName") String hostName);
}
