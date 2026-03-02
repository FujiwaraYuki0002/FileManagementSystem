package fms.domain;

/**
 * メッセージユーティリティ
 *
 * @author 大塚 月愛
 */
public interface MessageDomain {

    // messages_ja.propertiesのリソースキー

    /** 完了メッセージキー */

    /** {0}を登録しました */
    public static final String PROP_KEY_MESSAGE0001 = "MESSAGE0001";

    /** {0}を更新しました */
    public static final String PROP_KEY_MESSAGE0002 = "MESSAGE0002";

    /** {0}を削除しました */
    public static final String PROP_KEY_MESSAGE0003 = "MESSAGE0003";

    /** エラーメッセージキー */

    /** *{0}は半角英数字で入力してください */
    public static final String VALID_KEY_ERROR0001 = "ERROR0001";

    /** *{0}が選択されていません */
    public static final String VALID_KEY_ERROR0002 = "ERROR0002";

    /** *{0}は1文字～20文字で入力してください */
    public static final String VALID_KEY_ERROR0003 = "ERROR0003";

    /** *{0}が不正です */
    public static final String VALID_KEY_ERROR0004 = "ERROR0004";

    /** *{0}は半角英数字を含む8文字～20文字で入力してください */
    public static final String VALID_KEY_ERROR0005 = "ERROR0005";

    /** *{0}は全角文字100文字以内で入力してください */
    public static final String VALID_KEY_ERROR0006 = "ERROR0006";

    /** *{0}は全角カタカナ文字100文字以内で入力してください */
    public static final String VALID_KEY_ERROR0007 = "ERROR0007";

    /** *検索項目を一つ以上入力ください */
    public static final String VALID_KEY_ERROR0008 = "ERROR0008";

    /** *{0}を一つ以上選択してください */
    public static final String VALID_KEY_ERROR0009 = "ERROR0009";

    /** *{0}は既に登録されています */
    public static final String VALID_KEY_ERROR0010 = "ERROR0010";

    /** *既に削除されています */
    public static final String VALID_KEY_ERROR0011 = "ERROR0011";

    /** *{0}が一致しません */
    public static final String VALID_KEY_ERROR0012 = "ERROR0012";

    /** *{0}でロックされています。 */
    public static final String VALID_KEY_ERROR0013 = "ERROR0013";

    /** *検索結果はありません */
    public static final String VALID_KEY_ERROR0014 = "ERROR0014";

    /** *ファイルのサイズが500MBを超えています */
    public static final String VALID_KEY_ERROR0015 = "ERROR0015";

    /** *日付は左から『前』～『後』で入力してください */
    public static final String VALID_KEY_ERROR0016 = "ERROR0016";

    /** *{0}は半角英文字(大文字、小文字)、数字、記号を全て含む10文字～20文字で入力してください */
    public static final String VALID_KEY_ERROR0017 = "ERROR0017";

    /** *ファイル名は100文字以内で入力してください */
    public static final String VALID_KEY_ERROR0018 = "ERROR0018";

    /** *ユーザーで登録されているため削除できません */
    public static final String VALID_KEY_ERROR0019 = "ERROR0019";

    /** *ファイル名が重複しています */
    public static final String VALID_KEY_ERROR0020 = "ERROR0020";

    /** *システムエラーが発生しました。管理者に問い合わせてください */
    public static final String VALID_KEY_ERROR0021 = "ERROR0021";

    /** *セッションタイムアウトです */
    public static final String VALID_KEY_ERROR0022 = "ERROR0022";

}
