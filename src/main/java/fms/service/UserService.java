package fms.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import fms.domain.LogDomain;
import fms.domain.MessageDomain;
import fms.domain.UserDomain;
import fms.dto.TeamDto;
import fms.dto.UserDto;
import fms.entity.MUser;
import fms.entity.MUserTeam;
import fms.form.UserForm;
import fms.mapper.MUserMapper;
import fms.mapper.MUserTeamMapper;
import fms.util.DateUtil;
import fms.util.LogUtil;
import fms.util.MessageUtil;

/**
 * ユーザーサービス
 *
 * @author 大塚 月愛
 */
@Service
public class UserService {

    /** ユーザーマッパー */
    @Autowired
    private MUserMapper mUserMapper;

    /** 所属管理マッパー */
    @Autowired
    private MUserTeamMapper mUserTeamMapper;

    /** ユーザーエンティティ */
    @Autowired
    private MUser mUser;

    /** Dateユーティリティ */
    @Autowired
    private DateUtil dateUtil;

    /** ログユーティリティ */
    @Autowired
    private LogUtil logUtil;

    /** メッセージユーティリティ */
    @Autowired
    private MessageUtil messageUtil;

    /**
     * ユーザー情報DTOリストの取得
     *
     * @author 大塚 月愛
     *
     * @return ユーザー情報リスト
     */
    public List<UserDto> getUserDtoList(int retirementFlg, String userId) {

        return mUserMapper.getUserDtoList(retirementFlg, userId);
    }

    /**
     *入力チェック及び重複チェック 更新の場合
     *
     * @author 大塚 月愛
     *
     * @param userForm
     * @param bindingResult
     */
    public void updateCheckMUser(UserForm userForm,
            BindingResult bindingResult) {

        // パスワードと確認用パスワードが一致しているかチェック
        if (userForm.getNewPassword() != null && !userForm.getNewPassword().equals((userForm.getCheckPassword()))) {

            bindingResult.addError(new FieldError(bindingResult.getObjectName(), "checkPassword",
                    messageUtil.getMessage(MessageDomain.VALID_KEY_ERROR0012, new String[] { "確認用パスワード" })));

            // エラーログ登録
            logUtil.addLog(LogDomain.CODE_LOG_SECTION_ERROR, "照合エラー", MessageDomain.VALID_KEY_ERROR0012,
                    mUser.getUserId(), Thread.currentThread().getStackTrace()[1].getClassName());
        }
    }

    /**
     *入力チェック及び重複チェック 登録の場合
     *
     * @author 大塚 月愛
     *
     * @param userForm ユーザーフォーム
     * @param bindingResult エラーチェック
     */
    public void insertCheckMUser(UserForm userForm, BindingResult bindingResult) {

        // userIdの重複チェック
        if (mUserMapper.checkUserId(userForm.getUserId()) > 0) {

            bindingResult.addError(new FieldError(bindingResult.getObjectName(), "userId",
                    messageUtil.getMessage(MessageDomain.VALID_KEY_ERROR0010, new String[] { userForm.getUserId() })));

            // エラーログ登録
            logUtil.addLog(LogDomain.CODE_LOG_SECTION_ERROR, "重複チェックエラー", MessageDomain.VALID_KEY_ERROR0010,
                    mUser.getUserId(), Thread.currentThread().getStackTrace()[1].getClassName());
        }

        // パスワードと確認用パスワードが一致しているかチェック
        if (userForm.getNewPassword() != null && !userForm.getNewPassword().equals((userForm.getCheckPassword()))) {

            bindingResult.addError(new FieldError(bindingResult.getObjectName(), "checkPassword",
                    messageUtil.getMessage(MessageDomain.VALID_KEY_ERROR0012, new String[] { "確認用パスワード" })));

            // エラーログ登録
            logUtil.addLog(LogDomain.CODE_LOG_SECTION_ERROR, "照合エラー", MessageDomain.VALID_KEY_ERROR0012,
                    mUser.getUserId(), Thread.currentThread().getStackTrace()[1].getClassName());
        }
    }

    /**
     * チェックボックスの初期値を比較して取得
     *
     * @author 大塚 月愛
     *
     * @param retirementFlg 退職フラグ
     * @param userId ユーザーID
     * @return selectedTeamIds チームID
     */
    public List<Integer> selectedTeamIds(int retirementFlg, String userId) {

        List<UserDto> userDtoList = mUserMapper.getUserDtoList(retirementFlg,
                userId);
        List<Integer> selectedTeamIds = new ArrayList<>();

        userDtoList.stream()
                // ユーザーごとのチームリストをフラットにする
                .flatMap(userDto -> userDto.getTeamDtoList().stream())
                // チームIDを抽出
                .map(TeamDto::getTeamId)
                // selectedTeamIds に追加
                .forEach(selectedTeamIds::add);

        return selectedTeamIds;
    }

    /**
     * ユーザーマスタ更新
     *
     * @author 大塚 月愛
     *
     * @param userForm ユーザーフォーム
     */
    @Transactional
    public void mUserUpdate(UserForm userForm) {

        // ユーザーマスタ更新
        MUser mUser = new MUser();
        // バージョン取得
        Integer version = mUserMapper.getVersion(userForm.getUserId());

        // ユーザーエンティティに値をセット
        mUser.setUserId(userForm.getUserId());
        mUser.setPostId(userForm.getPostId());
        mUser.setUserName(userForm.getUserName());
        mUser.setUserNameKana(userForm.getUserNameKana());
        mUser.setRole(userForm.getRole());
        mUser.setRetirementFlg(userForm.getRetirementFlg());
        mUser.setLastModifiedDate(dateUtil.getToday());
        mUser.setLastModifiedUser(mUser.getUserId());
        mUser.setVersion(version);

        if (userForm.getNewPassword().isEmpty()) {
            // パスワードが入力されていない場合、変更なし
            String currentPassword = mUserMapper.getUserDto(userForm.getUserId()).getPassword();
            mUser.setPassword(currentPassword);

        } else {
            //パスワードをハッシュ化
            BCryptPasswordEncoder bcpe = new BCryptPasswordEncoder();
            String encodeedPassword = bcpe.encode(userForm.getNewPassword());

            mUser.setPassword(encodeedPassword);
        }

        mUserMapper.updateMUser(mUser);
        // フォームに入力した所属情報
        Integer[] teamIds = userForm.getTeamId();
        // DBに登録されてる所属情報
        List<Integer> mUserTeamList = mUserTeamMapper
                .getMUserTeam(userForm.getUserId());

        // userに登録されている所属情報を全件削除
        mUserTeamMapper.deleteMUserTeam(userForm.getUserId(), mUserTeamList);

        // 新しく登録するMUserTeamオブジェクトのリスト
        List<MUserTeam> userTeamToInsert = new ArrayList<>();

        // 選択された所属情報を所属管理DBに登録
        for (Integer teamId : teamIds) { // 拡張for文を使用

            // 所属管理エンティティに値をセット
            MUserTeam mUserTeam = new MUserTeam();
            mUserTeam.setUserId(userForm.getUserId());
            mUserTeam.setTeamId(teamId); // teamIdをセット
            mUserTeam.setFirstCreateDate(dateUtil.getToday());
            mUserTeam.setLastModifiedDate(dateUtil.getToday());
            mUserTeam.setLastModifiedUser(mUser.getUserId());

            // リストに追加
            userTeamToInsert.add(mUserTeam);

        }

        if (!userTeamToInsert.isEmpty()) {

            mUserTeamMapper.insertMUserTeams(userTeamToInsert);

        }
    }

    /**
     * ユーザーマスタ登録
     *
     * @author 大塚 月愛
     *
     * @param userForm ユーザーフォーム
     */
    @Transactional
    public void mUserInsert(UserForm userForm) {

        // ユーザーマスタ登録
        MUser mUser = new MUser();
        // 所属IDをリストで取得
        Integer[] teamIds = userForm.getTeamId();

        //パスワードをハッシュ化
        BCryptPasswordEncoder bcpe = new BCryptPasswordEncoder();
        String encodeedPassword = bcpe.encode(userForm.getNewPassword());

        // ユーザーエンティティに値をセット
        mUser.setUserId(userForm.getUserId());
        mUser.setPostId(userForm.getPostId());
        mUser.setUserName(userForm.getUserName());
        mUser.setUserNameKana(userForm.getUserNameKana());
        mUser.setPassword(encodeedPassword);
        mUser.setRole(userForm.getRole());
        mUser.setRetirementFlg(UserDomain.RETIREMENT_FLG_FALSE);
        mUser.setFirstCreateDate(dateUtil.getToday());
        mUser.setLastModifiedDate(dateUtil.getToday());
        mUser.setLastModifiedUser(mUser.getUserId());

        mUserMapper.insertMUser(mUser);

        // 新しく登録するMUserTeamオブジェクトのリスト
        List<MUserTeam> userTeamToInsert = new ArrayList<>();

        for (Integer teamId : teamIds) {
            // 所属管理登録
            MUserTeam mUserTeam = new MUserTeam();

            // 所属管理エンティティに値をセット
            mUserTeam.setUserId(userForm.getUserId());
            mUserTeam.setTeamId(teamId);
            mUserTeam.setFirstCreateDate(dateUtil.getToday());
            mUserTeam.setLastModifiedDate(dateUtil.getToday());
            mUserTeam.setLastModifiedUser(mUser.getUserId());

            // リストに追加
            userTeamToInsert.add(mUserTeam);
        }

        if (!userTeamToInsert.isEmpty()) {

            mUserTeamMapper.insertMUserTeams(userTeamToInsert);
        }
    }

    /**
     * ユーザー更新情報をフォームにセット
     *
     * @author 大塚 月愛
     *
     * @param userForm ユーザーフォーム
     */
    public void setMUserForm(UserForm userForm) {

        List<UserDto> userDtoList = mUserMapper.getUserDtoList(UserDomain.RETIREMENT_FLG_NONE, userForm.getUserId());

        UserDto userDto = userDtoList.get(0);

        userForm.setScreenId("user-update");

        BeanUtils.copyProperties(userDto, userForm);

    }
}
