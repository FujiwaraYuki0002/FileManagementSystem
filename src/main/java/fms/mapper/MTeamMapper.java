package fms.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DataIntegrityViolationException;

import fms.entity.MTeam;

/**
 * 所属マッパー
 *
 * @author 安藤 優海
 *
 */
@Mapper
public interface MTeamMapper {

    /**
     * 所属マスタ情報取得
     *
     * @author 安藤 優海
     *
     * @return 所属マスタリスト
     */
    List<MTeam> getMTeam();

    /**
     * 所属マスタ削除
     *
     * @author 安藤 優海
     *
     * @param teamId 所属ID
     *
     * @return 削除可否
     */
    boolean deleteMTeam(@Param("teamId") Integer teamId) throws DataIntegrityViolationException;

    /**
     * 所属名取得
     *
     * @author 安藤 優海
     *
     * @param teamId 所属ID
     *
     * @return 所属マスタエンティティ
     */
    MTeam getTeamName(@Param("teamId") Integer teamId);

    /**
     * 所属名重複チェック
     *
     * @author 安藤 優海
     *
     * @param teamName 所属名
     *
     * @return 0:重複していない 1:重複している
     */
    Integer checkTeamName(@Param("teamName") String teamName);

    /**
     * 所属マスタ登録
     *
     * @author 安藤 優海
     *
     * @param mTeam 所属マスタエンティティ
     *
     * @return 登録可否
     */
    boolean insertMTeam(MTeam mTeam);

    /**
     * 所属マスタ情報取得(1件)
     *
     * @author 安藤 優海
     *
     * @param teamName 所属名
     *
     * @return 所属マスタエンティティ
     */
    MTeam mTeamInfo(@Param("teamId") Integer teamId);

    /**
     * 所属マスタ更新
     *
     * @author 安藤 優海
     *
     * @param mTeam 所属マスタエンティティ
     *
     * @return 更新可否
     */
    boolean updateMTeam(MTeam mTeam);
}
