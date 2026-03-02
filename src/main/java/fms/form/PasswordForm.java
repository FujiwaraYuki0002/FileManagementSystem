package fms.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import lombok.Data;

/**
 * パスワードフォーム
 *
 * @author 安藤 優海
 *
 */
@Data
public class PasswordForm {

    /** ユーザーID */
    private String userId;

    /** パスワード */
    @NotBlank(message = "{ERROR0001}")
    private String password;

    /** 新しいパスワード */
    @Pattern(regexp = "^(?=(.*[a-z]))(?=(.*[A-Z]))(?=(.*\\d))(?=(.*[!\"#$%&'()*+,-./:;<=>?@\\^_`{|}~])).{10,20}$", message = "{ERROR0017}")
    @NotBlank(message = "{ERROR0001}")
    private String newPassword;

    /** 確認用パスワード */
    @Pattern(regexp = "^(?=(.*[a-z]))(?=(.*[A-Z]))(?=(.*\\d))(?=(.*[!\"#$%&'()*+,-./:;<=>?@\\^_`{|}~])).{10,20}$", message = "{ERROR0017}")
    @NotBlank(message = "{ERROR0001}")
    private String checkPassword;

    /** 画面ID */
    private String ScreenId;
}