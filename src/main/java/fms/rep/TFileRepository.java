package fms.rep;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.Date;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import fms.entity.MUser;
import fms.entity.TFile;

@Repository
public class TFileRepository {

    @Autowired
    private DataSource dataSource;

    /** ユーザーエンティティ */
    @Autowired
    private MUser mUser;

    /**
     * ファイル登録
     *
     * @author 髙橋 真澄
     *
     * @param fileId
     * @param serialNumber
     * @param file
     * @param fileName
     * @param title
     * @param data
     * @param first_create_date
     * @param last_modified_date
     * @param last_modified_user
     *
     * @throws Exception
     */
    public void insertFile(int fileId,
            int serialNumber,
            MultipartFile file,
            String fileName,
            String title,
            String data,
            Date first_create_date,
            Date last_modified_date,
            String last_modified_user)
            throws Exception {
        String sql = "INSERT INTO t_file ("
                + "file_id, "
                + "serial_number, "
                + "file,"
                + "file_name, "
                + "title, "
                + "date,"
                + "delete_flg,"
                + "first_create_date,"
                + "last_modified_date,"
                + "last_modified_user) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                InputStream inputStream = file.getInputStream()) { // ファイルをストリームで取得

            ps.setInt(1, fileId);
            ps.setInt(2, serialNumber);
            // setBinaryStream を使用
            ps.setBinaryStream(3, inputStream, file.getSize());
            ps.setString(4, title);
            ps.setString(5, fileName);
            ps.setString(6, data);
            ps.setInt(7, 0);
            ps.setObject(8, first_create_date.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime());
            ps.setObject(9, last_modified_date.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime());
            ps.setString(10, last_modified_user);

            // データを挿入
            ps.executeUpdate();
        }
    }

    /**
     *
     * ファイル登録
     *
     * @author 髙橋 真澄
     *
     * @param fileId
     * @param serialNumber
     * @param delFlg
     *
     * @return
     */
    public TFile getFileItem(int fileId, int serialNumber, int delFlg) {

        TFile file = new TFile();

        Connection connection = null;

        // 2つのバイト配列を結合して最終的なファイルを作成
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // ダウンロード対象のファイルサイズを取得
        String getFileSizeQuery = "SELECT LENGTH(file) AS file_size FROM t_file WHERE file_id = ? AND serial_number = ? AND delete_flg = ?";

        // 0～100MB部分取得
        String queryPart1 = "SELECT file_id, serial_number, title, date, file_name, SUBSTRING(file FROM 1 FOR 104857600) AS file_chunk FROM t_file WHERE file_id = ? AND serial_number = ? AND delete_flg = ?";

        // 101～200MB部分取得
        String queryPart2 = "SELECT SUBSTRING(file FROM 104857601 FOR 104857600) AS file_chunk FROM t_file WHERE file_id = ? AND serial_number = ? AND delete_flg = ?";

        // 201～300MB部分取得
        String queryPart3 = "SELECT SUBSTRING(file FROM 209715201 FOR 104857600) AS file_chunk FROM t_file WHERE file_id = ? AND serial_number = ? AND delete_flg = ?";

        // 301～400MB部分取得
        String queryPart4 = "SELECT SUBSTRING(file FROM 314572801 FOR 104857600) AS file_chunk FROM t_file WHERE file_id = ? AND serial_number = ? AND delete_flg = ?";

        // 401～500MB部分取得
        String queryPart5 = "SELECT SUBSTRING(file FROM 419430401 FOR 104857600) AS file_chunk FROM t_file WHERE file_id = ? AND serial_number = ? AND delete_flg = ?";

        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false); // 🔴 AutoCommitを無効化

            // 最初にファイルサイズを取得
            long fileSize = 0;
            try (PreparedStatement ps = connection.prepareStatement(getFileSizeQuery)) {
                ps.setInt(1, fileId);
                ps.setInt(2, serialNumber);
                ps.setInt(3, delFlg);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        fileSize = rs.getLong("file_size");
                    }
                }
            }

            // 最初の100MBを取得
            InputStream fileStream1 = null;
            try (PreparedStatement ps = connection.prepareStatement(queryPart1)) {
                ps.setInt(1, fileId);
                ps.setInt(2, serialNumber);
                ps.setInt(3, delFlg);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        fileStream1 = rs.getBinaryStream("file_chunk");
                        file.setFileId(rs.getInt("file_id"));
                        file.setSerialNumber(rs.getInt("serial_number"));
                        file.setTitle(rs.getString("title"));
                        file.setDate(rs.getString("date"));
                        file.setFileName(rs.getString("file_name"));
                    }
                }
            }

            if (fileStream1 == null) {
                return null;
            }

            connection.commit();

            // 残りのデータを取得する
            InputStream fileStream2 = null;
            if (fileSize > 104857600) { // 残りデータがある場合

                try (PreparedStatement ps = connection.prepareStatement(queryPart2)) {
                    ps.setInt(1, fileId);
                    ps.setInt(2, serialNumber);
                    ps.setInt(3, delFlg);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            fileStream2 = rs.getBinaryStream("file_chunk");
                        }
                    }
                }
            }

            connection.commit();

            InputStream fileStream3 = null;
            if (fileSize > 209715200) { // 残りデータがある場合

                try (PreparedStatement ps = connection.prepareStatement(queryPart3)) {
                    ps.setInt(1, fileId);
                    ps.setInt(2, serialNumber);
                    ps.setInt(3, delFlg);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            fileStream3 = rs.getBinaryStream("file_chunk");
                        }
                    }
                }
            }

            connection.commit();

            InputStream fileStream4 = null;
            if (fileSize > 314572800) { // 残りデータがある場合

                try (PreparedStatement ps = connection.prepareStatement(queryPart4)) {
                    ps.setInt(1, fileId);
                    ps.setInt(2, serialNumber);
                    ps.setInt(3, delFlg);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            fileStream4 = rs.getBinaryStream("file_chunk");
                        }
                    }
                }
            }

            connection.commit();

            InputStream fileStream5 = null;
            if (fileSize > 419430400) { // 残りデータがある場合

                try (PreparedStatement ps = connection.prepareStatement(queryPart5)) {
                    ps.setInt(1, fileId);
                    ps.setInt(2, serialNumber);
                    ps.setInt(3, delFlg);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            fileStream5 = rs.getBinaryStream("file_chunk");
                        }
                    }
                }
            }

            connection.commit();

            // 各部分のInputStreamをByteArrayOutputStreamに書き込む
            writeStreamToOutputStream(fileStream1, outputStream);
            writeStreamToOutputStream(fileStream2, outputStream);
            writeStreamToOutputStream(fileStream3, outputStream);
            writeStreamToOutputStream(fileStream4, outputStream);
            writeStreamToOutputStream(fileStream5, outputStream);

            byte[] fullFile = outputStream.toByteArray();

            fileStream1 = null;
            fileStream2 = null;
            fileStream3 = null;
            fileStream4 = null;
            fileStream5 = null;

            outputStream = null;

            // ファイルを一時ファイルとして保存
            String projectRootPath = System.getProperty("user.dir");
            File targetDir = new File(projectRootPath, "src/main/resources/static/file/" + mUser.getUserId());

            if (!targetDir.exists() && !targetDir.mkdirs()) {
                throw new SQLException("Failed to create directories");
            }

            File targetFile = new File(targetDir, file.getFileName());
            try (FileOutputStream fos = new FileOutputStream(targetFile)) {
                fos.write(fullFile);
            }

            file.setFile(targetFile);
            targetFile = null;

        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (connection != null)
                    connection.rollback(); // 🔴 失敗時にロールバック
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        } finally {
            outputStream = null;
            System.gc();
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close(); // 🔴 コネクションを必ずクローズ
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return file;
    }

    // InputStreamをByteArrayOutputStreamに書き込むヘルパーメソッド
    private void writeStreamToOutputStream(InputStream inputStream, ByteArrayOutputStream outputStream)
            throws IOException {
        if (inputStream != null) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
        }
    }

}