package fms.controller.file;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import fms.domain.LogDomain;
import fms.domain.MessageDomain;
import fms.dto.FileDto;
import fms.entity.MUser;
import fms.form.FileForm;
import fms.form.FileInputForm;
import fms.service.FileService;
import fms.service.TExclusiveControlService;
import fms.util.DateUtil;
import fms.util.LogUtil;

/**
 * ファイル登録・更新コントローラー
 *
 * @author 髙橋 真澄
 */
@Controller
@RequestMapping("/file")
public class FileRegistController {

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
     * ファイル更新画面 初期表示
     *
     * @author 髙橋 真澄
     *
     * @param fileForm ファイルフォーム
     * @param bindingResult リザルト
     * @param httpSession セッション
     * @param model モデル
     * @param httpServletRequest サーバーリクエスト
     *
     * @return ファイル登録画面
     */
    @RequestMapping(path = { "/insert",
            "/update" }, method = RequestMethod.POST)
    public String fileInsert(@ModelAttribute FileInputForm fileInputForm,
            BindingResult bindingResult,
            HttpSession httpSession, Model model,
            HttpServletRequest httpServletRequest,
            RedirectAttributes redirectAttributes) {

        // ユーザー、役職、所属の情報を取得
        fileService.setParticipantSelectionList(model);

        // 更新対象以外のファイル名を取得し、スコープに格納
        List<String> fileNameList = fileService
                .getFileNameList(fileInputForm.getFileId());

        model.addAttribute("fileNameList", fileNameList);

        if (httpServletRequest.getRequestURI()
                .equals("/file_management_system/file/update") ||
                httpServletRequest.getRequestURI().equals("/file/update")) {

            // フォームに画面IDをセット
            fileInputForm.setScreenId("file-update");

            // 更新対象の情報を取得
            FileDto file = fileService.getFile(fileInputForm, bindingResult);

            // 更新対象のファイルが見つからなかった場合
            if (file == null) {

                // 遷移後の画面を管理画面にする
                model.addAttribute("index", true);

                // 再検索用のフォームを作成
                FileForm fileForm = new FileForm();

                // エラー情報をリクエストスコープに格納
                redirectAttributes.addFlashAttribute("bindingResult",
                        bindingResult);

                // セッションにfileFormが存在する(事前に検索を行っていた)場合、それを取得
                if (httpSession.getAttribute("fileForm") != null) {

                    // セッション上から検索条件を取得
                    fileForm = (FileForm) httpSession.getAttribute("fileForm");

                    // 検索条件の日付フォーマットを修正
                    dateUtil.formDateSet(fileForm);

                } else {
                    // 行っていない場合はそのまま管理画面の初期表示

                    // 管理画面に遷移
                    return "redirect:/file/index";
                }

                httpSession.removeAttribute("fileForm");

                redirectAttributes.addFlashAttribute("fileForm", fileForm);

                // 管理画面に遷移
                return "redirect:/file/indexSearchExecute";
            }

            // 排他ロックがかかっているか確認
            boolean exclusiveControl = tExclusiveControlService
                    .checkUpdateExclusiveControl(fileInputForm, bindingResult);

            // 排他ロックがかかっている場合(true)、エラーアラートを表示
            // 排他ロックがかかっていない場合(false)
            model.addAttribute("exclusiveControl", exclusiveControl);

            List<String> userIdList = new ArrayList<>();
            List<String> userNameList = new ArrayList<>();

            for (MUser user : file.getMUserList()) {
                userIdList.add(user.getUserId());
                userNameList.add(user.getUserName());
            }

            fileInputForm.setUserId(userIdList);
            fileInputForm.setUserName(userNameList);
            // 題名と会議実施日をフォームに格納
            fileInputForm.setTitle(file.getTitle());
            fileInputForm.setMeetingDate(file.getDate());

            // ファイル名等はそのままエンティティでスコープに格納
            model.addAttribute("file", file);

            // 画面IDとファイルIDをセッションに保存
            httpSession.setAttribute("screenId", fileInputForm.getScreenId());
            httpSession.setAttribute("fileId", fileInputForm.getFileId());

            // 遷移後の画面を設定
            model.addAttribute("update", true);
        } else {
            // 遷移後の画面を設定
            model.addAttribute("insert", true);
            httpSession.removeAttribute("fileForm");
        }

        // 更新画面に遷移
        return "file/fileRegist";
    }

    /**
     * ファイル登録画面 「登録」ボタン押下
     *
     * @author 髙橋 真澄
     *
     * @param fileForm ファイルフォーム
     * @param bindingResult リザルト
     * @param httpSession セッション
     * @param model モデル
     *
     * @return 正常:ファイル管理画面 異常:ファイル登録画面
     */
    @RequestMapping(path = "/insert", params = "insertButton", method = RequestMethod.POST)
    public String fileInsertExecute(
            @Valid @ModelAttribute FileInputForm fileInputForm,
            BindingResult bindingResult,
            HttpSession httpSession, Model model) {

        // 操作ログ登録
        logUtil.addLog(LogDomain.CODE_LOG_SECTION_OPE, "ファイル登録",
                MessageDomain.PROP_KEY_MESSAGE0001, mUser.getUserId(),
                Thread.currentThread().getStackTrace()[1].getClassName());

        // ユーザー、役職、所属の情報を取得
        fileService.setParticipantSelectionList(model);

        // 登録済みのファイル名を取得
        List<String> fileNameList = fileService
                .getFileNameList(fileInputForm.getFileId());
        model.addAttribute("fileNameList", fileNameList);

        // 入力エラーの有無
        if (!bindingResult.hasErrors()) {// エラーが無かったら

            // ファイルを登録
            try {
                fileService.insertTFile(fileInputForm);

                fileInputForm.setFile(null);
            } catch (IOException e) {
                // TODO 自動生成された catch ブロック
                e.printStackTrace();
            }

            // 完了メッセージを用意
            String message = messageSource.getMessage(
                    MessageDomain.PROP_KEY_MESSAGE0001,
                    new String[] { "ファイル" }, Locale.JAPAN);

            model.addAttribute("completeMessage", message);
        }
        // エラーだった場合はそのまま登録画面に遷移

        // 入力された日付の表記を変更して再格納
        fileInputForm.setMeetingDate(
                dateUtil.noHyphenDate(fileInputForm.getMeetingDate()));

        // 遷移後の画面を設定
        model.addAttribute("insert", true);

        // 登録画面に遷移
        return "file/fileRegist";
    }

    /**
     * ファイル登録・更新画面 「戻る」ボタン押下
     *
     * @author 髙橋 真澄
     *
     * @param httpSession セッション
     * @param redirectAttributes リダイレクト
     * @param HttpServletRequest サーバーリクエスト
     *
     * @return ファイル管理画面
     */
    @RequestMapping(path = { "/updateBack",
            "/insertBack" }, method = RequestMethod.POST)
    public String updateBack(HttpSession httpSession,
            RedirectAttributes redirectAttributes,
            HttpServletRequest httpServletRequest) {

        // 排他ロック削除
        tExclusiveControlService.isTExclusiveControlDelete();

        // 検索条件保持用フォームを作成
        FileForm fileForm = new FileForm();

        // 画面IDとファイルIDをセッションから削除
        httpSession.removeAttribute("screenId");

        // 登録画面からの遷移かどうか
        if (httpServletRequest.getRequestURI()
                .equals("/file_management_system/file/insertBack") ||
                httpServletRequest.getRequestURI().equals("/file/insertBack")) {

            // その場合はファイルIDを削除
            httpSession.removeAttribute("fileId");
        }

        // セッションにfileFormが存在する(事前に検索を行っていた)場合、それを取得
        if (httpSession.getAttribute("fileForm") != null) {
            fileForm = (FileForm) httpSession.getAttribute("fileForm");

            // 日付が入力されているか
            if (fileForm.getDateFrom() != null
                    && !fileForm.getDateFrom().isEmpty()) {

                // form内の値を"yyyy-MM-dd"から"yyyyMMdd"に変換
                fileForm.setDateFrom(
                        dateUtil.hyphenDate(fileForm.getDateFrom()));
            }

            // 日付が入力されているか
            if (fileForm.getDateTo() != null
                    && !fileForm.getDateTo().isEmpty()) {

                // form内の値を"yyyy-MM-dd"から"yyyyMMdd"に変換
                fileForm.setDateTo(dateUtil.hyphenDate(fileForm.getDateTo()));
            }
        } else {

            // 管理画面に遷移
            return "redirect:/file/index";
        }

        // セッション上から検索フォームを削除
        httpSession.removeAttribute("fileForm");

        // 検索条件をリクエストスコープに格納
        redirectAttributes.addFlashAttribute("fileForm", fileForm);

        // 管理画面に遷移
        return "redirect:/file/indexSearchExecute";
    }

    /**
     * ファイル更新画面 「更新」ボタン押下
     *
     * @author 髙橋 真澄
     *
     * @param httpSession セッション
     * @param fileForm ファイルフォーム
     * @param bindingResult 入力チェック
     * @param model モデル
     * @param redirectAttributes リダイレクト
     *
     * @return 正常:ファイル管理画面 異常:ファイル更新画面
     */
    @RequestMapping(path = "/update", params = "updateButton", method = RequestMethod.POST)
    public String fileUpdateExecute(HttpSession httpSession,
            @Valid @ModelAttribute FileInputForm fileInputForm,
            BindingResult bindingResult, Model model,
            RedirectAttributes redirectAttributes) {

        // 操作ログ登録
        logUtil.addLog(LogDomain.CODE_LOG_SECTION_OPE, "ファイル更新",
                MessageDomain.PROP_KEY_MESSAGE0002, mUser.getUserId(),
                Thread.currentThread().getStackTrace()[1].getClassName());

        // 更新対象の情報を取得
        FileDto file = fileService.getFile(fileInputForm, bindingResult);

        // 更新対象のファイルが見つからなかった場合
        if (file == null) {

            // 排他ロック削除
            tExclusiveControlService.isTExclusiveControlDelete();

            // 遷移後の画面を管理画面にする
            model.addAttribute("index", true);

            // 再検索用のフォームを作成
            FileForm fileForm = new FileForm();

            // エラー情報をリクエストスコープに格納
            redirectAttributes.addFlashAttribute("bindingResult",
                    bindingResult);

            // セッションにfileFormが存在する(事前に検索を行っていた)場合、それを取得
            if (httpSession.getAttribute("fileForm") != null) {

                // セッション上から検索条件を取得
                fileForm = (FileForm) httpSession.getAttribute("fileForm");

                // 検索条件の日付フォーマットを修正
                dateUtil.formDateSet(fileForm);

            } else {
                // 行っていない場合はそのまま管理画面の初期表示

                // 管理画面に遷移
                return "redirect:/file/index";
            }

            httpSession.removeAttribute("fileForm");

            redirectAttributes.addFlashAttribute("fileForm", fileForm);

            // 管理画面に遷移
            return "redirect:/file/indexSearchExecute";
        }

        // 入力チェックエラーの確認
        if (!bindingResult.hasErrors()) {

            // エラーが無かったら更新処理
            fileService.updateTFile(fileInputForm, bindingResult);

            // 完了メッセージを用意
            String message = messageSource.getMessage(
                    MessageDomain.PROP_KEY_MESSAGE0002,
                    new String[] { "ファイル" }, Locale.JAPAN);

            model.addAttribute("completeMessage", message);
        }

        // ユーザー、役職、所属の情報を取得
        fileService.setParticipantSelectionList(model);

        // 登録済みのファイル名を取得
        List<String> fileNameList = fileService
                .getFileNameList(fileInputForm.getFileId());

        // 入力値に対応した初期値準備

        // SpringのBeanUtilsを使用してコピー
        BeanUtils.copyProperties(fileInputForm, file);

        // コピーできないファイル名と会議実施日を上書き
        IntStream.range(0, file.getTFileList().size())
                .forEach(index -> file.getTFileList().get(index)
                        .setFileName(fileInputForm.getFileName().get(index)));

        // 入力された日付の表記を変更して再格納
        fileInputForm.setMeetingDate(
                dateUtil.noHyphenDate(fileInputForm.getMeetingDate()));

        model.addAttribute("file", file);
        model.addAttribute("fileNameList", fileNameList);
        model.addAttribute("exclusiveControl", false);

        // 遷移後の画面を設定
        model.addAttribute("update", true);

        // 更新画面に遷移
        return "file/fileRegist";

    }

}
