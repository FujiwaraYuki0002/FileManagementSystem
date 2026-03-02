package fms.entity;

import lombok.Data;

/**
 * 排他ロックエンティティ
 *
 * @author 安藤 優海
 *
 */
@Data
public class TExclusiveControl extends BaseEntity {

    /** PCホスト名 */
    public String hostName;

    /** 画面ID */
    public String screenId;

    /** ファイル管理番号 */
    public Integer fileId;

    /** ユーザーID */
    public String userId;

    /** 所属ID */
    public Integer teamId;

    /** 役職ID */
    public Integer postId;
}
