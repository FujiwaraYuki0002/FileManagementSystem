package fms.form;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * ユーザフォーム
 *
 * @author 大塚 月愛
 *
 */
@Data
public class UserForm {

    @Pattern(regexp = "^$|^(?=.*\\d)[a-zA-Z0-9!\"#$%&'()*+,-./:;<=>?@\\^_`{|}~]{8,20}$", message = "{ERROR0005}")
    /** ユーザID */
    private String userId;

    /** 役職ID */
    private Integer postId;

    /** 役職名 */
    private String postName;

    /** 所属ID */
    private Integer[] teamId;

    @Pattern(regexp = "^[ａ-ｚＡ-Ｚ０-９一-龯ぁ-んァ-ヶ々々ー]{1,100}$", message = "{ERROR0006}")
    /** ユーザー名(漢字) */
    private String userName;

    @Pattern(regexp = "^[ァ-ヶー]{1,100}$", message = "{ERROR0007}")
    /** ユーザー名(カタカナ) */
    private String userNameKana;

    /** パスワード */
    private String password;

    /** 新しいパスワード */
    @Pattern(regexp = "^$|^(?=(.*[a-z]))(?=(.*[A-Z]))(?=(.*\\d))(?=(.*[!\"#$%&'()*+,-./:;<=>?@\\^_`{|}~])).{10,20}$", message = "{ERROR0017}")
    private String newPassword;

    /** 確認用パスワード */
    @Pattern(regexp = "^$|^(?=(.*[a-z]))(?=(.*[A-Z]))(?=(.*\\d))(?=(.*[!\"#$%&'()*+,-./:;<=>?@\\^_`{|}~])).{10,20}$", message = "{ERROR0017}")
    private String checkPassword;

    /** 権限 */
    private Integer role;

    /** 退職フラグ */
    private int retirementFlg;

    /** バージョン */
    private Integer version;

    //更新の排他ロック確認用
    /** 画面スクリーンID */
    private String screenId;
}
