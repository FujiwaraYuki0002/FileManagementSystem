package fms.controller.file;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import fms.domain.LogDomain;
import fms.domain.MessageDomain;
import fms.dto.FileDto;
import fms.entity.MUser;
import fms.form.FileForm;
import fms.service.FileService;
import fms.service.TExclusiveControlService;
import fms.util.DateUtil;
import fms.util.LogUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

/**
 * ファイルコントローラー
 *
 * @author 髙橋 真澄
 */
@Controller
@RequestMapping("/file")
public class FileSearchController {

    /** ファイルサービス */
    @Autowired
    private FileService fileService;

    /** メッセージプロパティ */
    @Autowired
    private MessageSource messageSource;

    /** ログユーティリティ */
    @Autowired
    private LogUtil logUtil;

    /** ユーザーエンティティ */
    @Autowired
    private MUser mUser;

    /** Dateユーティリティ */
    @Autowired
    private DateUtil dateUtil;

    /** 排他ロックサービス */
    @Autowired
    private TExclusiveControlService tExclusiveControlService;

    /**
     * ファイル検索・管理画面 初期表示
     *
     * @author 髙橋 真澄
     *
     * @param fileForm ファイルフォーム
     * @param model モデル
     * @param httpServletRequest URL情報
     *
     * @return ファイル検索・管理画面
     */
    @RequestMapping(path = { "/index", "/search" }, method = { RequestMethod.POST, RequestMethod.GET })
    public String fileSearch(@ModelAttribute FileForm fileForm, Model model, HttpServletRequest httpServletRequest) {

        // ユーザー、役職、所属の情報を取得
        fileService.setParticipantSelectionList(model);

        // URLを比較し、管理画面か検索画面か判定する
        if (httpServletRequest.getRequestURI().equals("/file_management_system/file/index") ||
                httpServletRequest.getRequestURI().equals("/file/index")) {

            // 管理画面を開いたログを登録
            logUtil.addLog(LogDomain.CODE_LOG_SECTION_OPE, "ファイル管理", "FILE_INDEX",
                    mUser.getUserId(), Thread.currentThread().getStackTrace()[1].getClassName());

            // 遷移後の画面を管理画面にする
            model.addAttribute("index", true);
        } else {

            // 検索画面を開いたログを登録
            logUtil.addLog(LogDomain.CODE_LOG_SECTION_OPE, "ファイル検索前", "FILE_SEARCH_BEFORE",
                    mUser.getUserId(), Thread.currentThread().getStackTrace()[1].getClassName());

            // 遷移後の画面を検索画面にする
            model.addAttribute("search", true);
        }

        // ファイル検索・管理画面に遷移
        return "file/search";
    }

    /**
     * ファイル検索・管理画面 検索処理
     *
     * @author 髙橋 真澄
     *
     * @param fileForm 検索用ファイルフォーム
     * @param bindingResult バインディングリザルト
     * @param model モデル
     * @param httpServletRequest URL情報
     * @param httpSession セッション
     *
     * @return ファイル検索・管理画面
     */
    @RequestMapping(path = { "/indexSearchExecute", "/searchExecute" }, method = { RequestMethod.POST,
            RequestMethod.GET })
    public String fileSearchExecute(@Valid FileForm fileForm, BindingResult bindingResult,
            Model model, HttpServletRequest httpServletRequest, HttpSession httpSession) {

        // 検索ログを追加
        logUtil.addLog(LogDomain.CODE_LOG_SECTION_OPE, "ファイル検索",
                "FILE_SEARCH", mUser.getUserId(), Thread.currentThread().getStackTrace()[1].getClassName());

        // ユーザー、役職、所属の情報を取得
        fileService.setParticipantSelectionList(model);

        // URLを照合して遷移後の画面を切り替える
        if (httpServletRequest.getRequestURI().equals("/file_management_system/file/indexSearchExecute") ||
                httpServletRequest.getRequestURI().equals("/file/indexSearchExecute")) {

            // 遷移後の画面を管理画面にする
            model.addAttribute("index", true);

            // 『検索』ボタンが押されたかどうか
            if (httpServletRequest.getParameter("searchButton") == null) {
                // 『検索』ボタンが押されていないならば選択中のファイルIDを格納

                // ☆更新の戻るボタンからの遷移
                fileForm.setFileId((Integer) httpSession.getAttribute("fileId"));
            }

            // 画面遷移時に検索結果を再表示するために検索情報をセッションに格納
            httpSession.setAttribute("fileForm", fileForm);

        } else {

            // 遷移後の画面を検索画面にする
            model.addAttribute("search", true);
        }

        // エラーのチェック
        if (bindingResult.hasErrors()) {

            // 検索条件の日付フォーマットを修正
            dateUtil.formDateSet(fileForm);

            // ファイル検索・管理画面に遷移
            return "file/search";
        }

        // 検索条件でファイルを検索
        List<FileDto> fileList = fileService.getFileList(fileForm);

        // 検索結果が0件だった場合 かつ 削除処理後ではない場合
        if (fileList.isEmpty() && httpServletRequest.getParameter("searchButton") != null) {

            // エラーを追加
            bindingResult.addError(new FieldError(bindingResult.getObjectName(), "version",
                    messageSource.getMessage(MessageDomain.VALID_KEY_ERROR0014, null,
                            Locale.JAPAN)));

            // エラー情報をログに登録
            logUtil.addLog(LogDomain.CODE_LOG_SECTION_ERROR, "検索結果0件エラー",
                    MessageDomain.VALID_KEY_ERROR0014, mUser.getUserId(), Thread.currentThread().getStackTrace()[1]
                            .getClassName());

            // ファイル検索・管理画面に遷移
            return "file/search";
        }

        for (FileDto file : fileList) {

            // 参加者の表示順を名前,役職で並び替える
            file.getMUserList().sort(Comparator.comparing(MUser::getUserName).reversed());
            file.getMUserList().sort(Comparator.comparing(MUser::getPostId).reversed());
        }

        // 検索結果と入力情報をスコープに格納
        model.addAttribute("fileList", fileList);

        // ファイル検索・管理画面に遷移
        return "file/search";
    }

    /**
     * 一時ファイルのディレクトリ削除
     *
     * @author 髙橋 真澄
     *
     * @return 完了レスポンス
     */
    @RequestMapping(path = { "/saveFileDelete" }, method = RequestMethod.POST)
    public ResponseEntity<Void> saveFileDelete() {

        // 削除対象のディレクトリ
        String projectRootPath = System.getProperty("user.dir");
        File targetDir = new File(projectRootPath, "src/main/resources/static/file/" + mUser.getUserId());

        try {

            // 削除処理
            fileService.deleteDirectory(targetDir);
        } catch (IOException e) {

            e.printStackTrace();
        }

        // レスポンス
        return ResponseEntity.ok().build();
    }

    /**
     * ファイル検索画面 ダウンロード処理
     *
     * @author 髙橋 真澄
     *
     * @param fileForm ファイルフォーム
     * @param model モデル
     *
     * @return ダウンロード情報
     *
     * @throws IOException
     */
    @RequestMapping(path = { "/fileDownload" }, method = RequestMethod.POST)
    public ResponseEntity<Resource> fileDownload(@ModelAttribute FileForm fileForm, Model model) throws IOException {

        // 管理連番が送られている(単体ダウンロード)かどうか
        if (fileForm.getSerialNumber() == null) {

            // 管理連番が送られていない(単体ダウンロードでない)場合

            // 検索ログを追加
            logUtil.addLog(LogDomain.CODE_LOG_SECTION_OPE, "zipファイルダウンロード",
                    "ZIP_FILE_DOWNLOAD", mUser.getUserId(), "FileId : " + fileForm.getFileId());

            // zipファイルをダウンロード
            return fileService.getFilesAsZip(fileForm);
        }

        // 管理連番が送られている(単体ダウンロードの)場合

        // 検索ログを追加
        logUtil.addLog(LogDomain.CODE_LOG_SECTION_OPE, "ファイルダウンロード",
                "FILE_DOWNLOAD", mUser.getUserId(),
                "FileId : " + fileForm.getFileId() + ", SerialNumber : " + fileForm.getSerialNumber());

        // ファイルをダウンロード
        return fileService.getFileItem(fileForm);
    }

    /**
     * ファイル管理画面 ファイル論理削除
     *
     * @author 髙橋 真澄
     *
     * @param fileForm ファイルフォーム
     * @param bindingResult バインディングリザルト
     * @param model モデル
     * @param redirectAttributes リダイレクト
     * @param httpSession セッション
     * @param page 現在のページ
     *
     * @return ファイル管理画面
     */
    @RequestMapping(path = "/delete", method = RequestMethod.POST)
    public String fileDelete(FileForm fileForm, BindingResult bindingResult, Model model,
            RedirectAttributes redirectAttributes, HttpSession httpSession, @Param("page") Integer page) {

        // 操作ログ登録
        logUtil.addLog(LogDomain.CODE_LOG_SECTION_OPE, "ファイル削除", MessageDomain.PROP_KEY_MESSAGE0003, mUser.getUserId(),
                Thread.currentThread().getStackTrace()[1].getClassName());

        // 排他ロック確認
        tExclusiveControlService.checkDeleteExclusiveControl("file-update", fileForm.getFileId(), bindingResult);

        // ページをリクエストスコープに格納
        redirectAttributes.addFlashAttribute("page", page);

        // 対象のファイルIDをセッション上に格納(削除失敗時に選択状態を保持するため)
        httpSession.setAttribute("fileId", fileForm.getFileId());

        // 遷移後の画面を管理画面にする
        model.addAttribute("index", true);

        // 排他ロックがかかっている場合、エラーアラートを表示
        if (bindingResult.hasErrors()) {

            // エラー情報をリクエストスコープに格納
            redirectAttributes.addFlashAttribute("bindingResult", bindingResult);

            // セッションにfileFormが存在する(事前に検索を行っていた)場合、それを取得
            if (httpSession.getAttribute("fileForm") != null) {

                // セッション上から検索条件を取得
                fileForm = (FileForm) httpSession.getAttribute("fileForm");

                // 検索条件の日付フォーマットを修正
                dateUtil.formDateSet(fileForm);

            } else {

                // 管理画面に遷移
                return "redirect:/file/index";
            }

            // セッション上のフォームを削除し、リダイレクトスコープにフォームを格納
            httpSession.removeAttribute("fileForm");
            redirectAttributes.addFlashAttribute("fileForm", fileForm);

            // 管理画面に遷移
            return "redirect:/file/indexSearchExecute";
        }

        // 削除処理後かどうかの判定に使用(削除後に検索結果0件エラーを表示しない用)
        redirectAttributes.addFlashAttribute("delete", "delete");

        // 削除処理
        fileService.updateDeleteTFile(fileForm, bindingResult);

        // 削除済みの場合、エラーアラートを表示
        if (bindingResult.hasErrors()) {

            redirectAttributes.addFlashAttribute("bindingResult", bindingResult);

            // セッションにfileFormが存在する(事前に検索を行っていた)場合、それを取得
            if (httpSession.getAttribute("fileForm") != null) {

                fileForm = (FileForm) httpSession.getAttribute("fileForm");
            } else {

                // 管理画面に遷移
                return "redirect:/file/index";
            }

            // セッション上のフォームを削除し、リダイレクトスコープにフォームを格納
            httpSession.removeAttribute("fileForm");
            redirectAttributes.addFlashAttribute("fileForm", fileForm);

            // 管理画面に遷移
            return "redirect:/file/indexSearchExecute";
        }

        // エラーではない場合、削除処理。完了メッセージを表示する
        String message = messageSource.getMessage(MessageDomain.PROP_KEY_MESSAGE0003, new String[] { "ファイル" },
                Locale.JAPAN);
        redirectAttributes.addFlashAttribute("completeMessage", message);

        // セッションにfileFormが存在する(事前に検索を行っていた)場合、それを取得
        if (httpSession.getAttribute("fileForm") != null) {
            fileForm = (FileForm) httpSession.getAttribute("fileForm");

            // 検索条件の日付フォーマットを修正
            if (fileForm.getDateFrom() != null && !fileForm.getDateFrom().isEmpty()) {

                // form内の値を"yyyy-MM-dd"から"yyyyMMdd"に変換
                fileForm.setDateFrom(dateUtil.hyphenDate(fileForm.getDateFrom()));
            }

            // 日付が入力されているか
            if (fileForm.getDateTo() != null && !fileForm.getDateTo().isEmpty()) {

                // form内の値を"yyyy-MM-dd"から"yyyyMMdd"に変換
                fileForm.setDateTo(dateUtil.hyphenDate(fileForm.getDateTo()));
            }

        } else {

            // 管理画面に遷移
            return "redirect:/file/index";
        }

        // 検索情報を削除 & スコープに格納
        httpSession.removeAttribute("fileForm");
        redirectAttributes.addFlashAttribute("fileForm", fileForm);

        // 管理画面に遷移
        return "redirect:/file/indexSearchExecute";
    }
}
