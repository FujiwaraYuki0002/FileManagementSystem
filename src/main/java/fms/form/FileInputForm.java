package fms.form;

import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import fms.annotation.DateFraudCheck;
import fms.annotation.FileNameDuplicationCheck;
import fms.annotation.FileNameLengthCheck;
import fms.annotation.FileSizeCheck;
import fms.annotation.TimeCorrelationCheck;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 登録・更新用ファイルフォーム
 *
 * @author 髙橋 真澄
 *
 */
@Data
@DateFraudCheck
@FileNameLengthCheck
@FileNameDuplicationCheck
@FileSizeCheck
@TimeCorrelationCheck
public class FileInputForm {

    /** ファイルID */
    private Integer fileId;

    /** ユーザーID */
    private List<String> userId;

    /** ユーザー名 */
    private List<String> userName;

    /** 会議実施日 */
    private String meetingDate;

    /** 時間自 */
    @DateTimeFormat(pattern = "HH:mm")
    private String timeTo;

    /** 時間至 */
    @DateTimeFormat(pattern = "HH:mm")
    private String timeFrom;

    /** 題名 */
    @Pattern(regexp = "^[ａ-ｚＡ-Ｚ０-９一-龯ぁ-んァ-ヶ々々ー]{1,100}$", message = "{ERROR0006}")
    private String title;

    /** ファイル */
    private List<MultipartFile> file;

    /** ファイル名 */
    private List<String> fileName;

    /** バージョン */
    private Integer version;

    //更新の排他ロック確認用
    /** 画面ID */
    private String screenId;
}