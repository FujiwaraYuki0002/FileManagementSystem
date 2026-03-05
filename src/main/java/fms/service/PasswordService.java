package fms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import fms.domain.LogDomain;
import fms.domain.MessageDomain;
import fms.domain.UserDomain;
import fms.entity.MUser;
import fms.form.PasswordForm;
import fms.mapper.LoginMapper;
import fms.mapper.MUserMapper;
import fms.util.DateUtil;
import fms.util.LogUtil;

/**
 * パスワードサービス
 *
 * @author 安藤 優海
 */

@Service
public class PasswordService {

    /** ログインマッパー */
    @Autowired
    private LoginMapper loginMapper;

    /** ユーザーマッパー */
    @Autowired
    private MUserMapper mUserMapper;

    /** ログユーティリティ */
    @Autowired
    private LogUtil logUtil;

    /** Dateユーティリティ */
    @Autowired
    private DateUtil dateUtil;

    /** ユーザーエンティティ */
    @Autowired
    private MUser mUser;

    /**
     * ID・パスワード照合処理
     *
     * @author 安藤 優海
     *
     * @param passwordForm パスワードフォーム
     *
     * @return 照合結果
     */
    public boolean idAndPasswordCheck(PasswordForm passwordForm) {

        // ユーザーを検索
        MUser loginUser = loginMapper.getLoginInformation(passwordForm.getUserId(),
                UserDomain.RETIREMENT_FLG_FALSE);

        BCryptPasswordEncoder bcpe = new BCryptPasswordEncoder();

        // パスワード照合
        boolean isLogin = bcpe.matches(passwordForm.getPassword(), loginUser.getPassword());

        // パスワード照合結果
        if (!isLogin) {

            // パスワード照合出来なかった場合はエラーログ登録
            logUtil.addLog(LogDomain.CODE_LOG_SECTION_ERROR, "照合エラー", MessageDomain.VALID_KEY_ERROR0004,
                    passwordForm.getUserId(), Thread.currentThread().getStackTrace()[1].getClassName());
        }

        return isLogin;
    }

    /**
     * パスワード更新
     *
     * @author 安藤 優海
     *
     * @param passwordForm　パスワードフォーム
     */
    public void passwordUpdate(PasswordForm passwordForm) {

        // パスワードをハッシュ化
        BCryptPasswordEncoder bcpe = new BCryptPasswordEncoder();
        String encodeedPassword = bcpe.encode(passwordForm.getNewPassword());

        // 現在のユーザーのバージョンを取得
        Integer uerVersion = mUserMapper.getVersion(passwordForm.getUserId());

        // ユーザーマスタ（パスワード）更新
        MUser updateUser = new MUser();

        updateUser.setUserId(passwordForm.getUserId());
        updateUser.setPassword(encodeedPassword);
        updateUser.setLastModifiedDate(dateUtil.getToday());
        updateUser.setLastModifiedUser(mUser.getUserId());
        updateUser.setVersion(uerVersion);

        mUserMapper.updateMUserPassword(updateUser);
    }
}
