package fms.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import fms.dto.UserDto;
import fms.entity.MUser;

/**
 * ユーザーマッパー
 *
 * @author 大塚 月愛
 */
@Mapper
public interface MUserMapper {

    /**
     * ユーザー情報リスト取得
     *
     * @author 大塚 月愛
     *
     * @param retirementFlg
     * @param userId
     *
     * @return ユーザーDTOリスト
     */
    List<UserDto> getUserDtoList(@Param("retirementFlg") Integer retirementFlg,
            @Param("userId") String userId);

    /**
    * ユーザー情報取得
    *
    * @author 大塚 月愛
    *
    * @param retirementFlg
    * @param userId
    *
    * @return ユーザーDTOリスト
    */
    UserDto getUserDto(@Param("userId") String userId);

    /**
     * ユーザーID重複チェック
     *
     * @author 大塚 月愛
     *
     * @param userId
     *
     * @return userId
     */
    Integer checkUserId(@Param("userId") String userId);

    /**
     * ユーザーマスタ登録
     *
     * @author 大塚 月愛
     *
     * @param userId
     *
     * @return 登録可否
     */
    boolean insertMUser(MUser mUser);

    /**
     * ユーザーマスタ更新
     *
     * @author 大塚 月愛
     *
     * @param mUser
     *
     * @return 更新可否
     */
    Integer getVersion(@Param("userId") String userId);

    /**
     * ユーザーマスタ更新
     *
     * @author 大塚 月愛
     *
     * @param mUser
     *
     * @return 更新可否
     */
    boolean updateMUser(MUser mUser);

    /**
     * パスワード更新
     *
     * @author 安藤 優海
     *
     * @param mUser
     *
     * @return パスワード更新可否
     */
    boolean updateMUserPassword(MUser mUser);
}
