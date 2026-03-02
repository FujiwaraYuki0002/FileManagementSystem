package fms.domain;

import org.springframework.stereotype.Component;

/**
 * ユーザードメイン
 *
 * @author 安藤 優海
 */
@Component
public class UserDomain {

    /** 権限：管理者 */
    public static final int CODE_ROLE_ADMIN = 1;
    /** 権限：一般 */
    public static final int CODE_ROLE_GENERAL = 2;

    /** 退職フラグオフ */
    public static final int RETIREMENT_FLG_FALSE = 0;
    /** 退職フラグオン */
    public static final int RETIREMENT_FLG_TRUE = 1;
    /** 退職フラグ判定なし */
    public static final int RETIREMENT_FLG_NONE = -1;
}
