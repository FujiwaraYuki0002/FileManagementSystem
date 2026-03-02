package fms.entity;

import java.util.Date;

import lombok.Data;

/**
 * 基底エンティティクラス
 *
 * @author 髙橋 真澄
 *
 */
@Data
public class BaseEntity {

    /** データ登録日時 */
    private Date firstCreateDate;

    /** データ更新日時 */
    private Date lastModifiedDate;

    /** 更新者ユーザーID */
    private String lastModifiedUser;

    /** バージョン */
    private Integer version;

    // 別クラスに継承した際、そのままSystem.out.printでは表示されない
    // 確認したい場合は以下のコメントアウトを解除し、使用する。

    //    @Override
    //    public String toString() {
    //        return "firstCreateDate=" + firstCreateDate +
    //                ", lastModifiedDate=" + lastModifiedDate +
    //                ", lastModifiedUser=" + lastModifiedUser +
    //                ", version=" + version;
    //    }

    // 継承する側のtoString()の例 記述先に合わせて内容を変更
    //    @Override
    //    public String toString() {
    //        return "(変数名)=" + (変数名) +
    //                ", " + super.toString();
    //    }

}
