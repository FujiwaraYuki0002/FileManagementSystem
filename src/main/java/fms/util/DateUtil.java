package fms.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fms.domain.LogDomain;
import fms.entity.MUser;
import fms.entity.TLog;
import fms.form.FileForm;
import fms.mapper.TLogMapper;

/**
 * Dateユーティリティ
 *
 * @author 安藤 優海
 *
 */
@Component
public class DateUtil {

    /** ユーザーエンティティ */
    @Autowired
    private MUser mUser;

    /** ログマッパー */
    @Autowired
    private TLogMapper tLogMapper;

    /**
     * 現在時刻の取得
     *
     * @author 安藤 優海
     * @return 現在時刻
     */
    public Date getToday() {

        Date nowDate = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String nowDateStr = df.format(nowDate);
        Date today = null;
        try {
            today = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss").parse(nowDateStr);

        } catch (ParseException e) {

            // エラーログを登録
            TLog log = new TLog();
            Date todayError = new Date();

            log.setLogSection(LogDomain.CODE_LOG_SECTION_ERROR);
            log.setOperationContent("時間取得エラー");
            log.setMessageCode("ParseException");
            log.setIncidentClassName(Thread.currentThread().getStackTrace()[1].getClassName());
            log.setIncidentDate(todayError);
            log.setFirstCreateDate(todayError);
            log.setLastModifiedDate(todayError);
            log.setLastModifiedUser(mUser.getUserId());

            tLogMapper.insertTLog(log);
        }
        return today;
    }

    /**
     * 文字列yyyy-MM-ddを文字列yyyyMMddに変換
     *
     * @author 髙橋 真澄
     *
     * @param dateString 変換したい文字列
     *
     * @return 変換後の日付
     */
    public String noHyphenDate(String dateString) {

        // フォームの日付型変更用フォーマット
        SimpleDateFormat formDateChangeFormat = new SimpleDateFormat("yyyy-MM-dd");

        // データベースから受け取った日付の型変更用フォーマット
        SimpleDateFormat dateChangeFormat = new SimpleDateFormat("yyyyMMdd");

        // もしすでに "yyyyMMdd" 形式ならそのまま返す
        if (dateString.matches("\\d{8}")) {
            return dateString;
        }

        try {
            return dateChangeFormat.format(
                    formDateChangeFormat.parse(dateString));
        } catch (ParseException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }
        return dateString;
    }

    /**
     * 文字列yyyyMMddを文字列yyyy-MM-ddに変換
     *
     * @author 髙橋 真澄
     *
     * @param dateString 変換したい文字列
     *
     * @return 変換後の日付
     */
    public String hyphenDate(String dateString) {

        // フォームの日付型変更用フォーマット
        SimpleDateFormat formDateChangeFormat = new SimpleDateFormat("yyyyMMdd");

        // 変更したい形式
        SimpleDateFormat dateChangeFormat = new SimpleDateFormat("yyyy-MM-dd");

        // もしすでに "yyyy-MM-dd" 形式ならそのまま返す
        if (dateString.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return dateString;
        }

        try {
            return dateChangeFormat.format(
                    formDateChangeFormat.parse(dateString));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateString;
    }

    /**
     * 文字列yyyyMMddを文字列yyyy/MM/ddに変換
     *
     * @author 髙橋 真澄
     *
     * @param dateString 変換したい文字列
     *
     * @return 変換後の日付
     */
    public String dateOutputChange(String dateString) {

        // データベースから受け取った日付の型変更用フォーマット
        SimpleDateFormat dateChangeFormat = new SimpleDateFormat("yyyyMMdd");

        // 表示用フォーマット
        SimpleDateFormat dateOutputFormat = new SimpleDateFormat("yyyy/MM/dd");

        // もしすでに "yyyy/MM/dd" 形式ならそのまま返す
        if (dateString.matches("\\d{4}/\\d{2}/\\d{2}")) {
            return dateString;
        }

        try {
            return dateOutputFormat.format(dateChangeFormat.parse(dateString));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateString;
    }

    /**
     *  文字列hh:mmを文字列hhmmに変換
     *
     *  @author 藤田 誠也
     *
     *  @param dateString 変換したい文字列
     *
     *  @return 変換後の時間
     */
    public String noColonTime(String stringTime) {
        return stringTime.replace(":", "");
    }

    /**
     *  文字列hhmmを文字列hh:mmに変換
     *
     *  @author 藤田 誠也
     *
     *  @param dateString 変換したい文字列
     *
     *  @return 変換後の時間
     */
    public String ColonTime(String stringTime) {
        StringBuilder sb = new StringBuilder(stringTime);
        return sb.insert(2, ":").toString();
    }

    /**
     * フォームの日時表記の修正
     *
     * @author 髙橋 真澄
     *
     * @param fileForm
     */
    public void formDateSet(FileForm fileForm) {

        // 日付が入力されているか
        if (!fileForm.getDateFrom().isEmpty()) {

            // 不正ならばform内の値を"yyyy-MM-dd"から"yyyyMMdd"に変換
            fileForm.setDateFrom(noHyphenDate(fileForm.getDateFrom()));

        }

        // 日付が入力されているか
        if (!fileForm.getDateTo().isEmpty()) {

            // 不正ならばform内の値を"yyyy-MM-dd"から"yyyyMMdd"に変換
            fileForm.setDateTo(noHyphenDate(fileForm.getDateTo()));
        }
    }
}
