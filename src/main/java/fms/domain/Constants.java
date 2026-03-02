package fms.domain;

import org.springframework.stereotype.Component;

/**
* コンスタンツ
*
* @author 安藤 優海
*/
@Component
public class Constants {

    /** 削除フラグオフ */
    public static final int DELETE_FLG_FALSE = 0;
    /** 削除フラグオン */
    public static final int DELETE_FLG_TRUE = 1;

    /** 初回バージョン */
    public static final int VERSION_START = 1;
}
