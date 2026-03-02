package fms.mapper;

import org.apache.ibatis.annotations.Mapper;

import fms.entity.TLog;

/**
 * ログマッパー
 *
 * @author 安藤 優海
 *
 */
@Mapper
public interface TLogMapper {

    /**
     * ログ登録
     *
     * @author 安藤 優海
     * @param tLog
     */
    void insertTLog(TLog tLog);

}
