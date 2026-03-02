package fms.domain;

import org.springframework.stereotype.Component;

/**
 * ログドメイン
 *
 * @author 安藤 優海
 */
@Component
public class LogDomain {

    /** ログ区分:操作 */
    public static final int CODE_LOG_SECTION_OPE = 1;

    /** ログ区分:エラー */
    public static final int CODE_LOG_SECTION_ERROR = 2;

}
