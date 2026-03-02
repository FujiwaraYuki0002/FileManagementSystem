package fms.service;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import fms.domain.LogDomain;
import fms.domain.MessageDomain;
import fms.entity.MTeam;
import fms.entity.MUser;
import fms.form.TeamForm;
import fms.mapper.MTeamMapper;
import fms.util.DateUtil;
import fms.util.LogUtil;

/**
 * 所属サービス
 *
 * @author 安藤 優海
 */

@Service
public class TeamService {

    /** 所属マッパー */
    @Autowired
    private MTeamMapper mTeamMapper;

    /** Dateユーティリティ */
    @Autowired
    private DateUtil dateUtil;

    /** メッセージプロパティ */
    @Autowired
    private MessageSource messageSource;

    /** ユーザーエンティティ */
    @Autowired
    private MUser mUser;

    /** ログエンティティ */
    @Autowired
    private LogUtil logUtil;

    /**
     * 所属マスタリストの情報取得
     *
     * @author 安藤 優海
     *
     * @return 所属リスト
     */
    public List<MTeam> getMTeamList() {

        List<MTeam> teamList = mTeamMapper.getMTeam();

        return teamList;
    }

    /**
     * 所属マスタ削除
     *
     * @author 安藤 優海
     *
     * @param teamId 所属ID
     * @param bindingResult エラーチェック
     */
    public void isMTeamDelete(Integer teamId, BindingResult bindingResult) {

        boolean isTeamDelete = true;

        try {

            // 削除処理
            isTeamDelete = mTeamMapper.deleteMTeam(teamId);

        } catch (DataIntegrityViolationException e) {

            // リザルトに外部参照キーエラーを登録
            bindingResult.addError(new FieldError(bindingResult.getObjectName(), "teamName",
                    messageSource.getMessage(MessageDomain.VALID_KEY_ERROR0019, null, Locale.JAPAN)));

            // エラーログ登録
            logUtil.addLog(LogDomain.CODE_LOG_SECTION_ERROR, "外部参照キーエラー", MessageDomain.VALID_KEY_ERROR0019,
                    mUser.getUserId(), Thread.currentThread().getStackTrace()[1].getClassName());

        }

        // 削除済みだった（エラー）場合
        if (!isTeamDelete) {

            //リザルトに削除済みエラーを登録
            bindingResult.addError(new FieldError(bindingResult.getObjectName(), "teamName",
                    messageSource.getMessage(MessageDomain.VALID_KEY_ERROR0011, null, Locale.JAPAN)));

            //エラーログ登録
            logUtil.addLog(LogDomain.CODE_LOG_SECTION_ERROR, "削除済みエラー", MessageDomain.VALID_KEY_ERROR0011,
                    mUser.getUserId(), Thread.currentThread().getStackTrace()[1].getClassName());
        }
    }

    /**
     * 重複チェック
     *
     * @author 安藤 優海
     *
     * @param teamForm 所属フォーム
     * @param bindingResult エラーチェック
     */
    public void checkMTeam(TeamForm teamForm, BindingResult bindingResult) {

        Integer team = mTeamMapper.checkTeamName(teamForm.getTeamName());

        // 重複チェック
        if (team > 0) {

            // リザルトに重複チェックエラーを登録
            bindingResult.addError(new FieldError(bindingResult.getObjectName(), "teamName",
                    messageSource.getMessage(MessageDomain.VALID_KEY_ERROR0010, new String[] { teamForm.getTeamName() },
                            Locale.JAPAN)));

            // エラーログ登録
            logUtil.addLog(LogDomain.CODE_LOG_SECTION_ERROR, "重複チェックエラー", MessageDomain.VALID_KEY_ERROR0010,
                    mUser.getUserId(), Thread.currentThread().getStackTrace()[1].getClassName());
        }
    }

    /**
     * 所属マスタ登録
     *
     * @author 安藤 優海
     *
     * @param teamForm 所属フォーム
     */
    public void mTeamInsert(TeamForm teamForm) {

        // 所属マスタ登録
        MTeam mTeam = new MTeam();
        mTeam.setTeamName(teamForm.getTeamName());
        mTeam.setFirstCreateDate(dateUtil.getToday());
        mTeam.setLastModifiedDate(dateUtil.getToday());
        mTeam.setLastModifiedUser(mUser.getUserId());

        // 登録処理
        mTeamMapper.insertMTeam(mTeam);

    }

    /**
     * 所属更新情報をフォームにセット
     *
     * @author 安藤 優海
     *
     * @param teamForm 所属フォーム
     */
    public void setMTeamForm(TeamForm teamForm, BindingResult bindingResult) {

        MTeam mTeam = mTeamMapper.mTeamInfo(teamForm.getTeamId());

        if (mTeam == null) {

            //リザルトに削除済みエラーを登録
            bindingResult.addError(new FieldError(bindingResult.getObjectName(), "teamName",
                    messageSource.getMessage(MessageDomain.VALID_KEY_ERROR0011, null, Locale.JAPAN)));

            //エラーログ登録
            logUtil.addLog(LogDomain.CODE_LOG_SECTION_ERROR, "削除済みエラー", MessageDomain.VALID_KEY_ERROR0011,
                    mUser.getUserId(), Thread.currentThread().getStackTrace()[1].getClassName());

            return;
        }

        teamForm.setTeamName(mTeam.getTeamName());
        teamForm.setVersion(mTeam.getVersion());
        teamForm.setScreenId("team-update");
    }

    /**
     * 所属マスタ更新
     *
     * @author 安藤 優海
     *
     * @param teamForm 所属フォーム
     */
    public void mTeamUpdate(TeamForm teamForm) {

        // 所属マスタ更新
        MTeam mTeam = new MTeam();

        mTeam.setTeamId(teamForm.getTeamId());
        mTeam.setTeamName(teamForm.getTeamName());
        mTeam.setLastModifiedDate(dateUtil.getToday());
        mTeam.setLastModifiedUser(mUser.getUserId());
        mTeam.setVersion(teamForm.getVersion());

        mTeamMapper.updateMTeam(mTeam);

    }
}
