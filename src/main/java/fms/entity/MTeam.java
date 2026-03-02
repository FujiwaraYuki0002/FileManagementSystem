package fms.entity;

import lombok.Data;

/**
 * 所属エンティティ
 *
 * @author 安藤 優海
 *
 */
@Data
public class MTeam extends BaseEntity {

    /** 所属ID */
    private Integer teamId;

    /** 所属名 */
    private String teamName;

}
