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

    /** *ERROR0001:{0}は半角英数字で入力してください */
    public static final String VALID_KEY_ERROR0001 = "ERROR0001";

    /** *ERROR0002:{0}が選択されていません */
    public static final String VALID_KEY_ERROR0002 = "ERROR0002";

    /** *ERROR0003:{0}は1文字～19文字で入力してください */
    public static final String VALID_KEY_ERROR0003 = "ERROR0003";

    /** *ERROR0004:{0}が不正です */
    public static final String VALID_KEY_ERROR0004 = "ERROR0004";

    /** *ERROR0005:{0}は半角英数字を含む8文字～19文字で入力してください */
    public static final String VALID_KEY_ERROR0005 = "ERROR0005";

    /** *ERROR0006:{0}は全角文字100文字以内で入力してください */
    public static final String VALID_KEY_ERROR0006 = "ERROR0006";

    /** *ERROR0007:{0}は全角カタカナ文字100文字以内で入力してください */
    public static final String VALID_KEY_ERROR0007 = "ERROR0007";

    /** *ERROR0008:検索項目を一つ以上入力ください */
    public static final String VALID_KEY_ERROR0008 = "ERROR0008";

    /** *ERROR0009:{0}を一つ以上選択してください */
    public static final String VALID_KEY_ERROR0009 = "ERROR0009";

    /** *ERROR0010:{0}は既に登録されています */
    public static final String VALID_KEY_ERROR0010 = "ERROR0010";

    /** *ERROR0011:既に削除されています */
    public static final String VALID_KEY_ERROR0011 = "ERROR0011";

    /** *ERROR0012:{0}が一致しません */
    public static final String VALID_KEY_ERROR0012 = "ERROR0012";

    /** *ERROR0013:{0}でロックされています。 */
    public static final String VALID_KEY_ERROR0013 = "ERROR0013";

    /** *ERROR0014:検索結果はありません */
    public static final String VALID_KEY_ERROR0014 = "ERROR0014";

    /** *ERROR0015:ファイルのサイズが500MBを超えています */
    public static final String VALID_KEY_ERROR0015 = "ERROR0015";

    /** *ERROR0016:{0}は半角英文字(大文字、小文字)、数字、記号を全て含む10文字～19文字で入力してください */
    public static final String VALID_KEY_ERROR0016 = "ERROR0016";

    /** *ERROR0017:ファイル名は100文字以内で入力してください */
    public static final String VALID_KEY_ERROR0017 = "ERROR0017";

    /** *ERROR0018:ユーザーで登録されているため削除できません */
    public static final String VALID_KEY_ERROR0018 = "ERROR0018";

    /** *ERROR0019:ファイル名が重複しています */
    public static final String VALID_KEY_ERROR0019 = "ERROR0019";

    /** *ERROR0020:システムエラーが発生しました。管理者に問い合わせてください */
    public static final String VALID_KEY_ERROR0020 = "ERROR0020";

    /** *ERROR0021:セッションタイムアウトです */
    public static final String VALID_KEY_ERROR0021 = "ERROR0021";

    /** *ERROR0022:入力したデータが無効です。データの整合性に違反しているため、処理を完了できません。 */
    public static final String VALID_KEY_ERROR0022 = "ERROR0022";

    /** *ERROR0023:この操作を実行する権限がありません。管理者に問い合わせてください。 */
    public static final String VALID_KEY_ERROR0023 = "ERROR0023";

    /** *ERROR0024:処理中にデータベースのロックが競合しました。再試行しても問題が解決しない場合は管理者に問い合わせてください。 */
    public static final String VALID_KEY_ERROR0024 = "ERROR0024";

    /** *ERROR0024:予期しないエラーが発生しました。再試行しても問題が解決しない場合は管理者に問い合わせてください。 */
    public static final String VALID_KEY_ERROR0025 = "ERROR0025";

    /** *ERROR0026:ネットワークエラーが発生しました。再試行しても問題が解決しない場合は管理者に問い合わせてください。 */
    public static final String VALID_KEY_ERROR0026 = "ERROR0026";

    /** *ERROR0027:予期しないエラーが発生しました。再試行しても問題が解決しない場合は管理者に問い合わせてください。 */
    public static final String VALID_KEY_ERROR0027 = "ERROR0027";

    /** *ERROR0028:システムのメモリが不足しています。アプリケーションを再起動してください。 */
    public static final String VALID_KEY_ERROR0028 = "ERROR0028";

    /** *ERROR0029:スタック領域が不足しています。管理者に問い合わせてください。 */
    public static final String VALID_KEY_ERROR0029 = "ERROR0029";

    /** *ERROR0030:この操作は現在実行できません。管理者に問い合わせてください。 */
    public static final String VALID_KEY_ERROR0030 = "ERROR0030";

    /** *ERROR0031:操作がタイムアウトしました。再度試してください。 */
    public static final String VALID_KEY_ERROR0031 = "ERROR0031";

    /** *ERROR0032:このページにアクセスする権限がありません。管理者に問い合わせてください。 */
    public static final String VALID_KEY_ERROR0032 = "ERROR0032";

    /** *ERROR0033:入力された値が無効です。適切な値を入力してください。 */
    public static final String VALID_KEY_ERROR0033 = "ERROR0033";

    /** *ERROR0034:この機能は現在利用できません。管理者に問い合わせてください。 */
    public static final String VALID_KEY_ERROR0034 = "ERROR0034";

    /** *ERROR0035:システムエラーが発生しました。管理者に問い合わせてください。 */
    public static final String VALID_KEY_ERROR0035 = "ERROR0035";

    /** *ERROR0036:ただいま内部処理中です。時間をおいて再試行してください。 */
    public static final String VALID_KEY_ERROR0036 = "ERROR0036";

}
