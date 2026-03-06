package fms.entity;

import lombok.Data;

/**
 * ファイル管理エンティティ
 *
 * @author 髙橋 真澄
 */
@Data
public class TFile extends BaseEntity {

    /** ファイル管理番号 */
    private Integer fileId;

    /** ファイル管理連番 */
    private Integer serialNumber;

    /** ファイル */ // 検索時にはfile型が、登録時にはMultipartFile型が入るためObject型を採用
    private Object file;

    /** ファイル名 */
    private String fileName;

    /** 題名 */
    private String title;

    /** 日付 */
    private String date;

    /** 時間自 */
    private String timeTo;

    /** 時間至 */
    private String timeFrom;

    /** 削除フラグ */
    private int deleteFlg;

    /** ユーザーID */
    private String userId;
}
