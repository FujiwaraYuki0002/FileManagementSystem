package fms.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * ログインフォーム
 *
 * @author 髙橋 真澄
 *
 */
@Data
public class LoginForm {

    /** ユーザーID */
    @Pattern(regexp = "^$|^(?=.*\\d)[a-zA-Z0-9!\"#$%&'()*+,-./:;<=>?@\\^_`{|}~]+$", message = "{ERROR0004}")
    @NotBlank(message = "{ERROR0001}")
    private String userId;

    /** パスワード */
    @Pattern(regexp = "^$|^(?=(.*[a-z]))(?=(.*[A-Z]))(?=(.*\\d))(?=(.*[!\"#$%&'()*+,-./:;<=>?@\\^_`{|}~])).{8,20}$", message = "{ERROR0004}")
    @NotBlank(message = "{ERROR0001}")
    private String password;
}