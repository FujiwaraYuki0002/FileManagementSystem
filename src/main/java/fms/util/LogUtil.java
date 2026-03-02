package fms.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fms.entity.TLog;
import fms.mapper.TLogMapper;

/**
 * ログユーティリティ
 *
 * @author 安藤 優海
 *
 */
@Component
public class LogUtil {

    /** Dateユーティリティ */
    @Autowired
    private DateUtil dateUtil;

    /** ログマッパー */
    @Autowired
    private TLogMapper tLogMapper;

    /**
     * ログ登録
     *
     * @author 安藤 優海
     *
     * @param logSection ログ区分
     * @param operationContent 操作内容
     * @param messageCode メッセージコード
     * @param userId ユーザーID
     */
    public void addLog(int logSection, String operationContent,
            String messageCode, String userId, String className) {

        //ログの設定
        TLog log = new TLog();
        log.setLogSection(logSection);
        log.setOperationContent(operationContent);
        log.setMessageCode(messageCode);
        log.setIncidentClassName(className);
        log.setIncidentDate(dateUtil.getToday());
        log.setFirstCreateDate(dateUtil.getToday());
        log.setLastModifiedDate(dateUtil.getToday());
        log.setLastModifiedUser(userId);

        // ログを登録
        tLogMapper.insertTLog(log);
    }
}
