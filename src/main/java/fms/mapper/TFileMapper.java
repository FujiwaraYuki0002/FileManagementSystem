package fms.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import fms.dto.FileDto;
import fms.entity.MUser;
import fms.entity.TFile;

/**
 * ファイルマッパー
 *
 * @author 髙橋 真澄
 */
@Mapper
public interface TFileMapper {

    /**
     * ファイル情報リスト取得
     *
     * @author 髙橋 真澄
     *
     * @param title 題名
     * @param fileName ファイル名
     * @param dateFrom 日付自
     * @param dateTo 日付至
     * @param userId ユーザーID
     * @param findWord ファイル内テキスト
     * @param deleteFlg 削除フラグ
     *
     * @return ファイル情報DTOリスト
     */
    List<FileDto> getFileDtoList(
            @Param("title") String title,
            @Param("fileName") String fileName,
            @Param("dateFrom") String dateFrom,
            @Param("dateTo") String dateTo,
            @Param("userId") String userId,
            @Param("findWord") String findWord,
            @Param("deleteFlg") int deleteFlg);

    /**
     * ファイルダウンロード対象の取得
     *
     * @author 髙橋 真澄
     *
     * @param fileId
     * @param serialNumber
     *
     * @return ファイル情報削除可否フラグ
     */
    TFile getFileItem(@Param("fileId") Integer fileId, @Param("serialNumber") Integer serialNumber,
            @Param("deleteFlg") int deleteFlg);

    /**
     * ファイル情報論理削除
     *
     * @author 髙橋 真澄
     *
     * @param tFile ファイルエンティティ
     *
     * @return ファイル情報削除可否フラグ
     */
    boolean updateDeleteTFile(TFile tFile);

    /**
     * ファイルシーケンス取得
     *
     * @return ファイル情報登録可否フラグ
     */
    int getFileSequence();

    /**
     * ファイル情報登録
     *
     * @param tFile ファイルエンティティ
     *
     * @return ファイル情報登録可否フラグ
     */
    boolean insertTFiles(@Param("fileList") List<TFile> fileList);

    /**
     * ファイル情報更新
     *
     * @param tFile ファイルエンティティ
     *
     * @return ファイル情報更新可否フラグ
     */
    boolean updateTFiles(List<TFile> tFiles);

    /**
     * 参加者情報削除
     *
     * @author 髙橋 真澄
     *
     * @param fileId
     *
     * @return 削除可否
     */
    boolean deleteTJoinUser(@Param("fileId") Integer fileId);

    /**
     * 参加者情報登録
     *
     * @author 髙橋 真澄
     *
     * @param fileId
     *
     * @return 登録可否
     */
    boolean insertTJoinUsers(@Param("fileList") List<TFile> fileList);

    /**
     * 参加者情報取得
     *
     * @author 髙橋 真澄
     *
     * @param fileId
     *
     * @return 参加者情報
     */
    List<MUser> getTJoinUser(@Param("fileId") Integer fileId, @Param("deleteFlg") int deleteFlg);

    /**
     * 更新対象のファイル情報取得
     *
     * @author 髙橋 真澄
     *
     * @param fileId ファイルID
     *
     * @return ファイル情報DTO
     */
    FileDto getFileDto(@Param("fileId") Integer fileId, @Param("deleteFlg") int deleteFlg);

    /**
     * ファイル名取得
     *
     * @author 髙橋 真澄
     *
     * @param deleteFlg 削除フラグ
     * @param fileId ファイルID
     *
     * @return ファイル名リスト
     */
    List<String> getFileNameList(@Param("deleteFlg") int deleteFlg, @Param("fileId") Integer fileId);
}
