package fms.form;

import java.util.List;

import jakarta.validation.constraints.Size;

import fms.annotation.DayCheck;
import fms.annotation.NoInputCheck;
import lombok.Data;

/**
 * ファイルフォーム
 *
 * @author 髙橋 真澄
 *
 */
@Data
@DayCheck
@NoInputCheck
public class FileForm {

    /** ファイルID */
    private Integer fileId;

    /** ファイル管理連番 */
    private Integer serialNumber;

    /** ユーザーID */
    private List<String> userId;

    /** ユーザー名 */
    private List<String> userName;

    /** 日付自 */
    private String dateFrom;

    /** 日付至 */
    private String dateTo;

    /** 題名 */
    @Size(max = 100, message = "{ERROR0004}")
    private String title;

    /** ファイル名 */
    @Size(max = 100, message = "{ERROR0004}")
    private String fileName;

    /** ファイル内テキスト文字 */
    @Size(max = 100, message = "{ERROR0004}")
    private String findWord;

    /** バージョン */
    private Integer version;

    //更新の排他ロック確認用
    /** 画面ID */
    private String screenId;

    // 検索条件未入力時のエラーメッセージ用
    private String searchConditions;
}