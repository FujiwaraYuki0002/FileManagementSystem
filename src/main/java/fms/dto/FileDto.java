package fms.dto;

import java.util.List;

import fms.entity.MUser;
import fms.entity.TFile;
import lombok.Data;

/**
 * オペレーターDTO
 *
 * @author 東京ITスクール
 */
@Data
public class FileDto {

    /** ファイル管理連番 */
    private String fileId;
    /** ファイル管理連番 */
    private Integer serialNumber;
    /** ユーザーリスト */
    private List<MUser> mUserList;
    /** 題名 */
    private String title;
    /** 日付 */
    private String date;
    /** ファイルリスト */
    private List<TFile> tFileList;
    /** バージョン */
    private Integer version;
}
