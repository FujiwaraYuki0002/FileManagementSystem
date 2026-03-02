package fms.dto;

import java.util.List;

import lombok.Data;

/**
 * ユーザーDTO
 *
 * @author 大塚
 */
@Data
public class UserDto {

    /** ユーザーID */
    private String userId;
    /** 所属リスト */
    private List<TeamDto> teamDtoList;
    /** 役職ID */
    private Integer postId;
    /** 役職名 */
    private String postName;
    /** ユーザー名（漢字） */
    private String userName;
    /** ユーザー名（カナ） */
    private String userNameKana;
    /** パスワード */
    private String password;
    /** 権限 */
    private Integer role;
    /** 退職フラグ */
    private int retirementFlg;
    /** バージョン */
    private Integer version;
}
