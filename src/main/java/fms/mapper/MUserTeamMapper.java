package fms.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import fms.entity.MUserTeam;

/**
 * 所属管理マッパー
 *
 * @author 大塚 月愛
 */
@Mapper
public interface MUserTeamMapper {

    /**
     * 所属管理情報取得
     *
     * @author 大塚 月愛
     *
     * @param userId
     *
     * @return Integer配列
     */
    List<Integer> getMUserTeam(@Param("userId") String userId);

    /**
     * 所属管理更新
     *
     * @author 大塚 月愛
     *
     * @param mUser
     *
     * @return 更新可否
     */
    Boolean insertMUserTeam(MUserTeam mUserTeam);

    /**
     * 所属管理削除
     *
     * @author 大塚 月愛
     *
     * @param userId
     *
     * @param teamIdsToDelete
     *
     * @return 削除可否
     */
    Boolean deleteMUserTeam(@Param("userId") String userId,
            @Param("teamId") List<Integer> teamIdsToDelete);
}
