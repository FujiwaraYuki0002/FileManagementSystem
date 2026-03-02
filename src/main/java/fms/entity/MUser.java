package fms.entity;

import java.io.Serializable;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import lombok.Data;

/**
 * ユーザーエンティティ
 *
 * @author 髙橋 真澄
 */
@Component
@SessionScope
@Data
public class MUser extends BaseEntity implements Serializable {

    /** ユーザID */
    private String userId;

    /** 役職ID */
    private Integer postId;

    /** ユーザー名(漢字) */
    private String userName;

    /** ユーザー名(カタカナ) */
    private String userNameKana;

    /** パスワード */
    private String password;

    /** 権限 */
    private Integer role;

    /** 退職フラグ */
    private int retirementFlg;
}
