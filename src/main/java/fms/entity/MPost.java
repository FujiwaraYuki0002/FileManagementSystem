package fms.entity;

import lombok.Data;

/**
 * 役職エンティティ
 *
 * @author 安藤 優海
 *
 */
@Data
public class MPost extends BaseEntity {

    /** 役職ID */
    private Integer postId;

    /** 役職名 */
    private String postName;
}
