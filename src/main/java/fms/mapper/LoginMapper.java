package fms.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import fms.entity.MUser;

/**
 * ログインマッパー
 *
 * @author 髙橋 真澄
 */
@Mapper
public interface LoginMapper {

    /**
     * ログイン情報取得
     *
     * @author 髙橋 真澄
     *
     * @param userId ユーザーID
     * @param password パスワード
     * @param retirementFlg 退職者フラグ
     *
     * @return MUser ユーザーエンティティ
     */
    MUser getLoginInformation(
            @Param("userId") String userId,
            @Param("retirementFlg") int retirementFlg);
}
