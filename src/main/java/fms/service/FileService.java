package fms.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.tika.parser.txt.CharsetDetector;
import org.apache.tika.parser.txt.CharsetMatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MultipartFile;

import fms.domain.Constants;
import fms.domain.LogDomain;
import fms.domain.MessageDomain;
import fms.domain.UserDomain;
import fms.dto.FileDto;
import fms.dto.UserDto;
import fms.entity.MPost;
import fms.entity.MTeam;
import fms.entity.MUser;
import fms.entity.TFile;
import fms.form.FileForm;
import fms.form.FileInputForm;
import fms.mapper.TFileMapper;
import fms.rep.TFileRepository;
import fms.util.DateUtil;
import fms.util.LogUtil;

/**
 * ファイルサービス
 *
 * @author 髙橋 真澄
 */
@Service
public class FileService {

    /* ファイルマッパー */
    @Autowired
    private TFileMapper tFileMapper;

    /** Dateユーティリティ */
    @Autowired
    private DateUtil dateUtil;

    /** ログエンティティ */
    @Autowired
    private LogUtil logUtil;

    /** ユーザーエンティティ */
    @Autowired
    private MUser mUser;

    /** メッセージプロパティ */
    @Autowired
    private MessageSource messageSource;

    /** ユーザーサービス */
    @Autowired
    private UserService userService;

    /** 役職サービス */
    @Autowired
    private PostService postService;

    /** 所属サービス */
    @Autowired
    private TeamService teamService;

    @Autowired
    private TFileRepository tFileRepository;

    /**
     * ファイル情報検索
     *
     * @author 髙橋 真澄
     *
     * @param fileForm ファイルフォーム
     *
     * @return ファイル情報DTOリスト
     */
    public List<FileDto> getFileList(FileForm fileForm) {

        // formに入っているString型の『yyyy-MM-dd』を比較用に
        // Date型の『yyyy-MM-dd』に変換してそれぞれ変数に格納
        // データベース検索時にBETWEENする用にString型の『yyyyMMdd』をフォームにセット

        // fileForm.getDateFrom()に値が入っているかどうか
        if (!fileForm.getDateFrom().isEmpty()) {

            // form内の値を"yyyy-MM-dd"から"yyyyMMdd"に変換
            fileForm.setDateFrom(dateUtil.noHyphenDate(fileForm.getDateFrom()));
        }

        // fileForm.getDateTo()に値が入っているかどうか
        if (!fileForm.getDateTo().isEmpty()) {

            // form内の値を"yyyy-MM-dd"から"yyyyMMdd"に変換
            fileForm.setDateTo(dateUtil.noHyphenDate(fileForm.getDateTo()));
        }

        // ユーザー検索を行っているか
        if (fileForm.getUserId().isEmpty()) {

            // 行っていない場合

            // ファイル情報取得
            List<FileDto> fileList = tFileMapper.getFileDtoList(
                    fileForm.getTitle(),
                    fileForm.getFileName(),
                    fileForm.getDateFrom(),
                    fileForm.getDateTo(),
                    null,
                    fileForm.getFindWord(),
                    Constants.DELETE_FLG_FALSE);

            // 日付の表記を修正
            for (FileDto file : fileList) {

                // 日付型を新しいフォーマットの文字列に変換
                file.setDate(dateUtil.dateOutputChange(file.getDate()));
            }

            // ファイルリストを返却
            return fileList;
        }

        // ユーザー検索を行っている場合

        // 最終的に全ての検索結果を格納するリスト
        List<FileDto> allFileList = new ArrayList<>();

        // ユーザーID分回す
        for (String userId : fileForm.getUserId()) {

            // ユーザーIDごとに検索し、ファイル情報取得
            List<FileDto> fileList = tFileMapper.getFileDtoList(
                    fileForm.getTitle(),
                    fileForm.getFileName(),
                    fileForm.getDateFrom(),
                    fileForm.getDateTo(), userId,
                    fileForm.getFindWord(),
                    Constants.DELETE_FLG_FALSE);

            // 日付の表記を修正
            for (FileDto file : fileList) {

                // 日付型を新しいフォーマットの文字列に変換
                file.setDate(dateUtil.dateOutputChange(file.getDate()));

                // リストに追加
                allFileList.add(file);
            }
        }

        // 重複するファイルを削除
        allFileList = allFileList.stream()
                .collect(Collectors.toMap(FileDto::getFileId, file -> file,
                        (existing, replacement) -> existing))
                .values()
                .stream()
                .collect(Collectors.toList());

        // ファイルを日付順に並び替え
        allFileList.sort(Comparator.comparing(FileDto::getDate).reversed());

        // ファイルリストを返却
        return allFileList;

    }

    /**
     * ファイル登録
     *
     * @author 髙橋 真澄
     *
     * @param fileInputForm 登録・更新情報入力ファイルフォーム
     * @throws IOException
     */
    @Transactional
    public void insertTFile(FileInputForm fileInputForm) throws IOException {

        // ユーザーIDの有無を確認
        if (fileInputForm.getUserId().isEmpty() || fileInputForm.getUserId() == null) {
            throw new DataIntegrityViolationException("ユーザーIDがnullです");
        }

        TFile tFile = new TFile();

        // ファイル登録用リスト
        List<TFile> fileList = new ArrayList<>();

        // ファイル管理連番
        int serialNumber = 1;

        // 渡されたファイル配列の番号
        int fileIndex = 0;

        // ファイル管理番号のシーケンスを取得
        int fileId = tFileMapper.getFileSequence();

        // ファイルの数の分、登録情報をリストに追加
        for (String fileName : fileInputForm.getFileName()) {

            // ファイル名が無い場合は次に
            if (fileName.isBlank()) {
                fileIndex++;
                continue;
            }

            // ファイルをMultipartFile型で格納
            MultipartFile multipartFile = fileInputForm.getFile().get(fileIndex++);

            // 拡張子が.txtの場合、UTF-8に変換する処理
            if (fileName.endsWith(".txt")) {
                byte[] utf8Bytes = convertToUtf8(multipartFile);

                // MockMultipartFile で新しい MultipartFile を作成
                multipartFile = new MockMultipartFile(
                        fileName, fileName, "text/plain", utf8Bytes);
            }

            try {

                // 登録処理
                tFileRepository.insertFile(fileId,
                        serialNumber++,
                        multipartFile,
                        fileInputForm.getTitle(),
                        fileName,
                        dateUtil.noHyphenDate(fileInputForm.getMeetingDate()),
                        dateUtil.noColonTime(fileInputForm.getTimeFrom()),
                        dateUtil.noColonTime(fileInputForm.getTimeTo()),
                        dateUtil.getToday(),
                        dateUtil.getToday(),
                        mUser.getUserId());
            } catch (Exception e) {

                e.printStackTrace();
                throw new UncategorizedSQLException(null, null, null);
            }
        }

        // ユーザーIDの有無を確認
        if (fileInputForm.getUserId().isEmpty() || fileInputForm.getUserId() == null) {
            throw new DataIntegrityViolationException("ユーザーIDがnullです");
        }

        fileList = new ArrayList<>();

        // 参加者情報をリストに格納
        for (String userId : fileInputForm.getUserId()) {
            tFile = new TFile();
            tFile.setFileId(fileId);
            tFile.setUserId(userId);
            tFile.setFirstCreateDate(dateUtil.getToday());
            tFile.setLastModifiedDate(dateUtil.getToday());
            tFile.setLastModifiedUser(mUser.getUserId());

            // 複数のTFileエンティティをリストに追加
            fileList.add(tFile);
        }

        // 一括挿入を実行
        tFileMapper.insertTJoinUsers(fileList);
    }

    private static byte[] convertToUtf8(MultipartFile file) throws IOException {
        byte[] fileBytes = file.getBytes();

        // 文字コードを判別
        Charset detectedCharset = detectCharset(fileBytes);

        // もし UTF-8 以外なら変換する
        if (!detectedCharset.equals(StandardCharsets.UTF_8)) {
            return new String(fileBytes, detectedCharset).getBytes(StandardCharsets.UTF_8);
        }
        return fileBytes;
    }

    // 文字コードを判別するメソッド
    private static Charset detectCharset(byte[] bytes) {
        CharsetDetector detector = new CharsetDetector();
        detector.setText(bytes);
        CharsetMatch match = detector.detect();

        if (match != null) {
            return Charset.forName(match.getName());
        }
        return StandardCharsets.UTF_8; // デフォルトでUTF-8

    }

    /**
     * ディレクトリ削除
     *
     * @author 髙橋 真澄
     *
     * @param directory 削除対象のディレクトリ
     */
    public void deleteDirectory(File directory) throws IOException {

        // ディレクトリが存在しない場合
        if (!directory.exists()) {

            // 何もしない
            return;
        }

        // ディレクトリの中身を取得
        File[] files = directory.listFiles();

        // 中身が存在しない場合
        if (files == null) {

            // 空のディレクトリを削除
            Files.delete(directory.toPath());
            return;
        }

        // 中身が存在するなら削除
        for (File file : files) {

            // 対象がディレクトリであるかどうか
            if (file.isDirectory()) {

                // ディレクトリなら再帰的に削除
                deleteDirectory(file);
            } else {

                // ファイルを削除
                Files.delete(file.toPath());
            }
        }

        // 最後にディレクトリ自体を削除
        Files.delete(directory.toPath());
    }

    /**
     * ファイル情報ダウンロード(単体)
     *
     * @author 髙橋 真澄
     *
     * @param fileForm ファイルフォーム
     *
     * @return ダウンロード処理のレスポンス
     */
    public ResponseEntity<Resource> getFileItem(FileForm fileForm) {

        TFile file = new TFile();

        file = tFileRepository.getFileItem(fileForm.getFileId(),
                fileForm.getSerialNumber(),
                Constants.DELETE_FLG_FALSE);

        if (file == null || !((File) file.getFile()).exists()) {
            // ファイルが見つからない場合
            return ResponseEntity.notFound().build();
        }

        // ファイルをResourceに変換
        Path path = ((File) file.getFile()).toPath();
        Resource resource = new FileSystemResource(path);

        // ファイル名を取得
        String fileName = ((File) file.getFile()).getName();

        // ファイル名をUTF-8エンコード
        String encodedFileName = null;

        try {
            // ファイル名をUTF-8エンコード
            encodedFileName = URLEncoder.encode(fileName, "UTF-8")
                    .replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            // エンコード失敗時のエラーハンドリング
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }

        // HTTPヘッダーを設定
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename*=UTF-8''" + encodedFileName);

        // レスポンスを返す
        ResponseEntity<Resource> response = ResponseEntity.ok()
                .headers(headers)
                .contentLength(((File) file.getFile()).length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);

        return response;
    }

    /**
     * ファイル情報ダウンロード(zip)
     *
     * @author 髙橋 真澄
     *
     * @param fileForm ファイルフォーム
     *
     * @return ダウンロード処理のレスポンス
     */
    public ResponseEntity<Resource> getFilesAsZip(FileForm fileForm)
            throws IOException {
        // ZIP出力ストリームを用意
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(
                byteArrayOutputStream);

        // 一時ファイルを格納するリスト
        List<File> tempFiles = new ArrayList<>();

        // zipファイル名
        String title = null;

        // zipファイル名に使用する日付
        String date = null;

        try {
            // ファイルリスト内の各ファイルをZIPに追加
            for (int i = 1; i < 4; i++) {
                // ファイルを取得
                TFile file = tFileRepository.getFileItem(fileForm.getFileId(), i,
                        Constants.DELETE_FLG_FALSE);

                // ファイルが存在しない場合は次へ
                if (file == null) {
                    continue;
                }

                // zipのファイル名、日付をセット
                title = file.getTitle();
                date = file.getDate();

                // ZIP内で使用するファイル名
                String zipEntryName = file.getFileName(); // getFileName()を使用

                InputStream fileInputStream = null;

                // file.getFile()の型による処理
                if (file.getFile() instanceof InputStream) {
                    // すでにInputStreamの場合
                    fileInputStream = (InputStream) file.getFile();
                } else if (file.getFile() instanceof File) {
                    // file.getFile()がFile型の場合、FileInputStreamを使用
                    File fileObj = (File) file.getFile();
                    fileInputStream = new FileInputStream(fileObj);
                    // 一時ファイルをリストに追加
                    tempFiles.add(fileObj);
                } else {
                    // file.getFile()が予期しない型の場合の処理
                    throw new IllegalArgumentException("Unsupported file type: "
                            + file.getFile().getClass().getName());
                }

                // ZIPエントリを追加
                zipOutputStream.putNextEntry(new ZipEntry(zipEntryName));

                // ファイル内容をZIPに書き込む
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fileInputStream.read(buffer)) >= 0) {
                    zipOutputStream.write(buffer, 0, length);
                }

                // それぞれストリームを閉じる
                zipOutputStream.closeEntry();
                fileInputStream.close();
            }

            // txtファイルを追加
            String txtFileName = "参加者一覧.txt"; // 追加するtxtファイル名

            // ZIPエントリを作成
            zipOutputStream.putNextEntry(new ZipEntry(txtFileName));

            zipOutputStream
                    .write(("[参加者一覧]" + "\n").getBytes(StandardCharsets.UTF_8));

            List<MUser> mUserList = tFileMapper.getTJoinUser(
                    fileForm.getFileId(), Constants.DELETE_FLG_FALSE);

            // 参加者の表示順を名前,役職で並び替える
            mUserList.sort(Comparator.comparing(MUser::getUserName).reversed());
            mUserList.sort(Comparator.comparing(MUser::getPostId).reversed());

            // リスト内の文字列を改行してファイルに書き込む
            for (MUser user : mUserList) {
                zipOutputStream.write(("・" + user.getUserName() + "\n")
                        .getBytes(StandardCharsets.UTF_8)); // 各行ごとに改行を追加
            }

            // txtファイルのエントリを閉じる
            zipOutputStream.closeEntry();

            // ZIPストリームを閉じる
            zipOutputStream.close();

            // ZIPファイル名を設定
            String zipFileName = title + "_" + date + ".zip";

            // Unicodeをエンコード
            String encodedZipFileName = URLEncoder.encode(zipFileName, "UTF-8")
                    .replaceAll("\\+", "%20");

            // ZIPデータをResourceとしてラップ
            Resource resource = new ByteArrayResource(
                    byteArrayOutputStream.toByteArray());

            // レスポンスを返す
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + encodedZipFileName
                                    + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, "application/zip")
                    .body(resource);
        } finally {
            // 一時ファイルの削除
            for (File tempFile : tempFiles) {
                if (tempFile.exists()) {
                    Files.delete(tempFile.toPath()); // ファイル削除
                }
            }
        }
    }

    /**
     * 更新対象のファイル情報取得
     *
     * @author 髙橋 真澄
     *
     * @param fileId ファイルID
     *
     * @return ファイル情報DTO
     */
    public FileDto getFile(FileInputForm fileInputForm,
            BindingResult bindingResult) {

        FileDto fileDto = tFileMapper.getFileDto(fileInputForm.getFileId(),
                Constants.DELETE_FLG_FALSE);

        if (fileDto == null) {

            // リザルトに削除済みエラーを登録
            bindingResult.addError(new FieldError(bindingResult.getObjectName(),
                    "fileDelete",
                    messageSource.getMessage("ERROR0011", null, Locale.JAPAN)));

            // エラーログ登録
            logUtil.addLog(LogDomain.CODE_LOG_SECTION_ERROR, "削除済みエラー",
                    MessageDomain.VALID_KEY_ERROR0011,
                    mUser.getUserId(),
                    Thread.currentThread().getStackTrace()[1].getClassName());
        }

        return fileDto;

    }

    /**
     * ファイル名全取得
     *
     * @author 髙橋 真澄
     *
     * @param fileId ファイルID
     *
     * @return ファイル名リスト
     */
    public List<String> getFileNameList(Integer fileId) {

        return tFileMapper.getFileNameList(Constants.DELETE_FLG_FALSE, fileId);
    }

    /**
     * ファイル更新
     *
     * @author 髙橋 真澄
     *
     * @param fileInputForm ファイルフォーム
     */
    @Transactional
    public void updateTFile(FileInputForm fileInputForm,
            BindingResult bindingResult) {

        if (fileInputForm.getUserId().isEmpty()
                || fileInputForm.getUserId() == null) {
            throw new DataIntegrityViolationException("ユーザーIDがnullです");
        }

        // 更新用のTFileリスト
        List<TFile> tFiles = new ArrayList<>();
        TFile tFile = new TFile();

        // ファイル情報の更新処理
        int serialNumber = 1;
        for (String fileName : fileInputForm.getFileName()) {
            tFile = new TFile();
            tFile.setFileId(fileInputForm.getFileId());
            tFile.setSerialNumber(serialNumber++);
            tFile.setTitle(fileInputForm.getTitle());
            tFile.setFileName(fileName);
            tFile.setDate(
                    dateUtil.noHyphenDate(fileInputForm.getMeetingDate()));
            tFile.setTimeTo(dateUtil.noColonTime(fileInputForm.getTimeTo()));
            tFile.setTimeFrom(dateUtil.noColonTime(fileInputForm.getTimeFrom()));
            tFile.setLastModifiedDate(dateUtil.getToday());
            tFile.setLastModifiedUser(mUser.getUserId());
            tFile.setVersion(fileInputForm.getVersion());

            // TFileリストに追加
            tFiles.add(tFile);
        }

        // ファイル情報の一括更新を実行
        tFileMapper.updateTFiles(tFiles);

        if (fileInputForm.getUserId().isEmpty()
                || fileInputForm.getUserId() == null) {
            throw new DataIntegrityViolationException("ユーザーIDがnullです");
        }

        // 参加者情報を一度削除
        tFileMapper.deleteTJoinUser(fileInputForm.getFileId());

        // リストの初期化
        tFiles = new ArrayList<>();

        // 参加者情報の用意
        for (String userId : fileInputForm.getUserId()) {

            tFile = new TFile();
            tFile.setFileId(fileInputForm.getFileId());
            tFile.setUserId(userId);
            tFile.setFirstCreateDate(dateUtil.getToday());
            tFile.setLastModifiedDate(dateUtil.getToday());
            tFile.setLastModifiedUser(mUser.getUserId());

            // 複数のTFileエンティティをリストに追加
            tFiles.add(tFile);
        }

        // 一括挿入を実行
        tFileMapper.insertTJoinUsers(tFiles);
    }

    /**
     * ファイル削除
     *
     * @author 髙橋 真澄
     *
     * @param fileForm
     * @param bindingResult バインディングリザルト
     */
    @Transactional
    public void updateDeleteTFile(FileForm fileForm,
            BindingResult bindingResult) {

        // 論理削除用の情報をエンティティに格納
        TFile tFile = new TFile();
        tFile.setFileId(fileForm.getFileId());
        tFile.setDeleteFlg(Constants.DELETE_FLG_TRUE);
        tFile.setLastModifiedUser(mUser.getUserId());
        tFile.setLastModifiedDate(dateUtil.getToday());
        tFile.setVersion(fileForm.getVersion());

        // ファイル削除処理
        boolean isFileDelete = tFileMapper.updateDeleteTFile(tFile);

        // 削除済みだった（エラー）場合
        if (!isFileDelete) {

            // リザルトに削除済みエラーを登録
            bindingResult.addError(new FieldError(bindingResult.getObjectName(),
                    "fileDelete",
                    messageSource.getMessage("ERROR0011", null, Locale.JAPAN)));

            // エラーログ登録
            logUtil.addLog(LogDomain.CODE_LOG_SECTION_ERROR, "削除済みエラー",
                    MessageDomain.VALID_KEY_ERROR0011,
                    mUser.getUserId(),
                    Thread.currentThread().getStackTrace()[1].getClassName());
        }
    }

    /**
     * 『参加者を選択』ダイアログの情報用意
     *
     * @author 髙橋 真澄
     *
     * @param model モデル
     *
     */
    public void setParticipantSelectionList(Model model) {

        // ユーザー、役職、所属の情報を取得
        List<UserDto> userList = userService
                .getUserDtoList(UserDomain.RETIREMENT_FLG_FALSE, null);
        List<MPost> postList = postService.getMPostList();
        List<MTeam> teamList = teamService.getMTeamList();

        // 必要情報をスコープに格納
        model.addAttribute("userList", userList);
        model.addAttribute("postList", postList);
        model.addAttribute("teamList", teamList);
    }
}