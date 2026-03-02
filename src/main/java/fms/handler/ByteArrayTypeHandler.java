package fms.handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fms.entity.MUser;

@Component
@MappedTypes(File.class) // File型を扱うように変更
@MappedJdbcTypes(JdbcType.BINARY) // BINARYタイプを扱う
public class ByteArrayTypeHandler extends BaseTypeHandler<File> {

    /** ユーザーエンティティ */
    @Autowired
    private MUser mUser;

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, File parameter, JdbcType jdbcType)
            throws SQLException {
        // Fileをbyte[]に変換して保存
        try {
            byte[] byteArray = Files.readAllBytes(parameter.toPath()); // Filesクラスでバイト配列を読み込み
            ps.setBytes(i, byteArray); // BINARYとして保存
        } catch (IOException e) {
            throw new SQLException("Failed to read file to byte array", e);
        }
    }

    @Override
    public File getNullableResult(ResultSet rs, String columnName) throws SQLException {
        byte[] byteArray = rs.getBytes(columnName);
        String fileName = rs.getString("file_name"); // SQLのfile_nameカラムを取得
        return byteArrayToFile(byteArray, fileName); // 取得したfile_nameを使用
    }

    @Override
    public File getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        byte[] byteArray = rs.getBytes(columnIndex);
        String fileName = rs.getString("file_name"); // SQLのfile_nameカラムを取得
        return byteArrayToFile(byteArray, fileName); // 取得したfile_nameを使用
    }

    @Override
    public File getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        byte[] byteArray = cs.getBytes(columnIndex);
        String fileName = cs.getString("file_name"); // SQLのfile_nameカラムを取得
        return byteArrayToFile(byteArray, fileName); // 取得したfile_nameを使用
    }

    private File byteArrayToFile(byte[] byteArray, String fileName) throws SQLException {

        // プロジェクトのルートディレクトリを取得
        String projectRootPath = System.getProperty("user.dir");

        // 保存先ディレクトリパス（static/fileディレクトリ）
        File targetDir = new File(projectRootPath, "src/main/resources/static/file/" + mUser.getUserId());

        // 保存先ディレクトリが存在しない場合は作成
        if (!targetDir.exists()) {
            if (!targetDir.mkdirs()) {
                throw new SQLException("Failed to create directories");
            }
        }

        // file_nameがnullまたは空の場合、デフォルト名を設定
        if (fileName == null || fileName.isEmpty()) {
            fileName = "file_" + System.currentTimeMillis(); // デフォルト名を生成
        }

        // 保存するファイルパスを作成
        File targetFile = new File(targetDir, fileName);

        // ファイルに書き込む処理
        try (FileOutputStream fos = new FileOutputStream(targetFile)) {
            fos.write(byteArray); // バイト配列を書き込む
        } catch (IOException e) {
            throw new SQLException("Failed to write byte array to file", e);
        }

        // 保存したファイルを返す
        return targetFile;
    }
}
