package fms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import fms.domain.LogDomain;
import fms.domain.MessageDomain;
import fms.domain.UserDomain;
import fms.entity.MUser;
import fms.form.LoginForm;
import fms.mapper.LoginMapper;
import fms.util.LogUtil;

/**
 * ログインサービス
 *
 * @author 髙橋 真澄
 */

@Service
public class LoginService {

    /** ログインマッパー */
    @Autowired
    private LoginMapper loginMapper;

    /** ログユーティリティ */
    @Autowired
    private LogUtil logUtil;

    /**
     * ログイン処理
     *
     * @author 髙橋 真澄
     *
     * @param loginForm ログインフォーム
     *
     * @return ユーザー情報
     */
    public MUser loginCheck(LoginForm loginForm) {

        // ユーザーを検索
        MUser loginUser = loginMapper.getLoginInformation(loginForm.getUserId(), UserDomain.RETIREMENT_FLG_FALSE);

        /**  @author 安藤 優海 */
        if (loginUser == null) {

            // ユーザーIDでユーザー情報を取得できない場合はエラーログ登録
            // 未ログイン状態のためユーザーIDの代わりに"noLoginUser"で登録
            logUtil.addLog(LogDomain.CODE_LOG_SECTION_ERROR, "ログインエラー", MessageDomain.VALID_KEY_ERROR0001,
                    "noLoginUser", Thread.currentThread().getStackTrace()[1].getClassName());

            // loginUserがnullの場合（ユーザー登録がない人 or 退職者）はユーザーの情報をnullで返す
            return null;
        }

        // パスワード照合
        BCryptPasswordEncoder bcpe = new BCryptPasswordEncoder();
        boolean isLogin = bcpe.matches(loginForm.getPassword(), loginUser.getPassword());

        // パスワード照合結果
        if (!isLogin) {

            // パスワード照合出来なかった場合はエラーログ登録
            // 未ログイン状態のためユーザーIDの代わりに"noLoginUser"で登録
            logUtil.addLog(LogDomain.CODE_LOG_SECTION_ERROR, "照合エラー", MessageDomain.VALID_KEY_ERROR0001,
                    "noLoginUser", Thread.currentThread().getStackTrace()[1].getClassName());

            // パスワード照合出来なかった場合はユーザーの情報をnullで返す
            return null;
        }
        // パスワード照合出来た場合はユーザーの情報を返す
        return loginUser;
    }
}
