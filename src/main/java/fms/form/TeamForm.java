package fms.form;

import jakarta.validation.constraints.Size;

import lombok.Data;

/**
 * 所属フォーム
 *
 * @author 安藤 優海
 *
 */
@Data
public class TeamForm {

    /** 所属ID */
    private Integer teamId;

    /** 所属名 */
    @Size(max = 20, message = "{ERROR0003}")
    private String teamName;

    /** バージョン */
    private Integer version;

    //更新の排他ロック確認用
    /** 画面ID */
    private String screenId;
}