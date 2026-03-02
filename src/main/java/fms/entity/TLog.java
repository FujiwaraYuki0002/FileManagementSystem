package fms.entity;

import java.util.Date;

import lombok.Data;

/**
 * ログエンティティ
 *
 * @author 安藤 優海
 *
 */
@Data
public class TLog extends BaseEntity {

    /** ログID */
    public Integer logId;

    /** ログ区分 */
    public int logSection;

    /** 操作内容 */
    public String operationContent;

    /** メッセージコード */
    public String messageCode;

    /** 発生クラス名 */
    public String incidentClassName;

    /** 発生日時 */
    public Date incidentDate;

}
