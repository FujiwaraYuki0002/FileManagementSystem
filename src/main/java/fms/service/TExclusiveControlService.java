package fms.service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import fms.domain.LogDomain;
import fms.domain.MessageDomain;
import fms.entity.MUser;
import fms.entity.TExclusiveControl;
import fms.form.FileInputForm;
import fms.form.PasswordForm;
import fms.form.PostForm;
import fms.form.TeamForm;
import fms.form.UserForm;
import fms.mapper.TExclusiveControlMapper;
import fms.util.DateUtil;
import fms.util.LogUtil;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 排他ロックサービス
 *
 * @author 安藤 優海
 */

@Service
public class TExclusiveControlService {

	/** 排他ロックマッパー */
	@Autowired
	private TExclusiveControlMapper tExclusiveControlMapper;

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

	/** サーブレットリクエスト */
	@Autowired
	private HttpServletRequest request;

	/**
	 * PCホスト名取得
	 *
	 * @author 安藤 優海
	
	 * @return PCホスト名
	 */
	public String getHostName() {
		String ip = request.getHeader("X-Forwarded-For");

		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}

		try {
			// まず Java の InetAddress で取得を試みる
			InetAddress inetAddress = InetAddress.getByName(ip);
			String hostName = inetAddress.getCanonicalHostName();
			return hostName;

		} catch (IOException e) {
			return "Unknown Host";
		}

	}

	/**
	 * 更新の排他ロックの確認・登録
	 *
	 * @author 安藤 優海
	 *
	 * @param object
	 * @param bindingResult
	 *
	 * @return ロックの有無
	 */
	public boolean checkUpdateExclusiveControl(Object object, BindingResult bindingResult) {

		TExclusiveControl tEcontrol = null; //ロックがあるかどうか
		String screenId = null;
		Integer fileId = null;
		String userId = null;
		Integer teamId = null;
		Integer postId = null;

		// ファイルの場合
		if (object instanceof FileInputForm) {
			FileInputForm form = (FileInputForm) object;
			screenId = form.getScreenId();
			fileId = form.getFileId();
			tEcontrol = tExclusiveControlMapper.getTExclusiveControl(screenId, fileId, null, null, null);
		}

		// ユーザーの場合
		if (object instanceof UserForm) {
			UserForm form = (UserForm) object;
			screenId = form.getScreenId();
			userId = form.getUserId();

			//排他ロック問い合わせ
			tEcontrol = tExclusiveControlMapper.getTExclusiveControl(screenId, null, userId, null, null);
		}

		// パスワード変更の場合
		if (object instanceof PasswordForm) {
			PasswordForm form = (PasswordForm) object;
			screenId = form.getScreenId();
			userId = form.getUserId();

			//排他ロック問い合わせ
			tEcontrol = tExclusiveControlMapper.getTExclusiveControl(screenId, null, userId, null, null);
		}

		// 所属の場合
		if (object instanceof TeamForm) {
			TeamForm form = (TeamForm) object;
			screenId = form.getScreenId();
			teamId = form.getTeamId();

			//排他ロック問い合わせ
			tEcontrol = tExclusiveControlMapper.getTExclusiveControl(screenId, null, null, teamId, null);
		}

		// 役職の場合
		if (object instanceof PostForm) {
			PostForm form = (PostForm) object;
			screenId = form.getScreenId();
			postId = form.getPostId();

			//排他ロック問い合わせ
			tEcontrol = tExclusiveControlMapper.getTExclusiveControl(screenId, null, null, null, postId);
		}

		// ホスト名を取得する
		TExclusiveControl tExclusive = new TExclusiveControl();

		// ※本番
		//		tExclusive.setHostName(getHostName());

		// ※開発環境用　↑とコメントアウトで切り替えて使用してください
		try {
			tExclusive.setHostName(InetAddress.getLocalHost().getHostName());

			// ホスト名が取得できなかった時
		} catch (Exception e) {

			// エラーログ登録
			logUtil.addLog(LogDomain.CODE_LOG_SECTION_ERROR, "ホスト名取得不可エラー", "UnknownHostException", mUser.getUserId(),
					Thread.currentThread().getStackTrace()[1].getClassName());
		}

		// 排他ロックなしの場合
		if (tEcontrol == null)

		{

			tExclusive.setScreenId(screenId);
			if (fileId != null) {
				tExclusive.setFileId(fileId);
			}
			if (userId != null) {
				tExclusive.setUserId(userId);
			}
			if (teamId != null) {
				tExclusive.setTeamId(teamId);
			}
			if (postId != null) {
				tExclusive.setPostId(postId);
			}

			// 排他ロックを登録
			tExclusive.setFirstCreateDate(dateUtil.getToday());
			tExclusive.setLastModifiedDate(dateUtil.getToday());
			tExclusive.setLastModifiedUser(mUser.getUserId());
			tExclusiveControlMapper.insertTExclusiveControl(tExclusive);

			// ロックなしを返す
			return false;
		}
		// 排他ロックあり、ロックが本人の場合
		if (tEcontrol.getHostName().equals(tExclusive.getHostName())) {

			// ロックなしを返す
			return false;
		}

		// 排他ロックがかかっている場合（エラー）

		// リザルトに排他ロックエラーを登録
		bindingResult.addError(new FieldError(bindingResult.getObjectName(), "control", messageSource.getMessage(
				MessageDomain.VALID_KEY_ERROR0013, new String[] { tEcontrol.getHostName() }, Locale.JAPAN)));

		//エラーログ登録
		logUtil.addLog(LogDomain.CODE_LOG_SECTION_ERROR, "排他ロックエラー", MessageDomain.VALID_KEY_ERROR0013,
				mUser.getUserId(), Thread.currentThread().getStackTrace()[1].getClassName());

		// ロックありを返す
		return true;
	}

	/**
	 * 排他ロック削除
	 *
	 * @author 安藤 優海
	 *
	 * @return 排他ロック削除可能かどうか
	 */

	public boolean isTExclusiveControlDelete() {
		boolean isPostControlDelete = false;

		// ※本番
		//        isPostControlDelete = tExclusiveControlMapper.deleteTExclusiveControl(getHostName());

		// ※開発環境用　↑とコメントアウトで切り替えて使用してください
		try {
			isPostControlDelete = tExclusiveControlMapper
					.deleteTExclusiveControl(InetAddress.getLocalHost().getHostName());
			// ホスト名が取得できなかった時
		} catch (UnknownHostException e) {

			// エラーログ登録
			logUtil.addLog(LogDomain.CODE_LOG_SECTION_ERROR, "ホスト名取得不可エラー", "UnknownHostException", mUser.getUserId(),
					Thread.currentThread().getStackTrace()[1].getClassName());
		}

		return isPostControlDelete;

	}

	/**
	 * 削除の排他ロックの確認
	 *
	 * @author 安藤 優海
	 *
	 * @param screenId
	 * @param id
	 * @param bindingResult
	 */

	//排他ロックがかかっているかを確認する
	public void checkDeleteExclusiveControl(String screenId, Integer id, BindingResult bindingResult) {

		TExclusiveControl tEcontrol = null;
		Integer fileId = null;
		Integer teamId = null;
		Integer postId = null;

		//ファイルの場合
		if (screenId.substring(0, 4).equals("file")) {
			fileId = id;
			tEcontrol = tExclusiveControlMapper.getTExclusiveControl(screenId, fileId, null, null, null);
		}

		//所属の場合
		if (screenId.substring(0, 4).equals("team")) {
			teamId = id;
			tEcontrol = tExclusiveControlMapper.getTExclusiveControl(screenId, null, null, teamId, null);
		}

		//役職の場合
		if (screenId.substring(0, 4).equals("post")) {
			postId = id;
			tEcontrol = tExclusiveControlMapper.getTExclusiveControl(screenId, null, null, null, postId);
		}

		//排他ロックがかかっている場合はエラーを登録
		if (tEcontrol != null) {

			// リザルトに排他ロックエラーを登録
			bindingResult.addError(
					new FieldError(bindingResult.getObjectName(), "control", messageSource.getMessage(
							MessageDomain.VALID_KEY_ERROR0013, new String[] { tEcontrol.getHostName() },
							Locale.JAPAN)));

			//エラーログ登録
			logUtil.addLog(LogDomain.CODE_LOG_SECTION_ERROR, "排他ロックエラー", MessageDomain.VALID_KEY_ERROR0013,
					mUser.getUserId(), Thread.currentThread().getStackTrace()[1].getClassName());
		}
	}
}
