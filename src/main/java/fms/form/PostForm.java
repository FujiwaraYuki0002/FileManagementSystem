package fms.form;

import jakarta.validation.constraints.Size;

import lombok.Data;

/**
 * 役職フォーム
 *
 * @author 安藤 優海
 *
 */
@Data
public class PostForm {

    /** 役職ID */
    private Integer postId;

    /** 役職名 */
    @Size(max = 20, message = "{ERROR0003}")
    private String postName;

    /** バージョン */
    private Integer version;

    //更新の排他ロック確認用
    /** 画面ID */
    private String screenId;
}