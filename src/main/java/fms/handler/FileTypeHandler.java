package fms.handler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.springframework.web.multipart.MultipartFile;

/*
 * ファイルの型変換
 */
public class FileTypeHandler extends BaseTypeHandler<MultipartFile> {

    /**
     * PreparedStatementにMultipartFileのデータを設定する。
     * ファイルの種類によって処理を分け、.txtファイルはUTF-8に変換、
     * それ以外はバイナリデータとしてそのまま保存します。
     *
     * @param ps PreparedStatement
     * @param i パラメータのインデックス
     * @param parameter MultipartFile
     * @param jdbcType JDBC型
     *
     * @throws SQLException SQL例外
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, MultipartFile parameter, JdbcType jdbcType)
            throws SQLException {
        try {
            byte[] fileBytes = getFileBytes(parameter); // ファイルデータを取得

            ps.setBytes(i, fileBytes); // バイト配列をPreparedStatementにセット
        } catch (IOException e) {
            throw new SQLException("Error processing MultipartFile", e);
        }
    }

    /**
     * ResultSetからファイルデータを取得し、MultipartFileに変換する。
     * NULLの場合はnullを返す。
     *
     * @param rs ResultSet
     * @param columnName カラム名
     *
     * @return MultipartFile
     *
     * @throws SQLException SQL例外
     */
    @Override
    public MultipartFile getNullableResult(ResultSet rs, String columnName) throws SQLException {
        byte[] bytes = rs.getBytes(columnName);
        return createMultipartFile(bytes);
    }

    /**
     * ResultSetからファイルデータを取得し、MultipartFileに変換する。
     * NULLの場合はnullを返す。
     *
     * @param rs ResultSet
     * @param columnIndex カラムインデックス
     *
     * @return MultipartFile
     *
     * @throws SQLException SQL例外
     */
    @Override
    public MultipartFile getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        byte[] bytes = rs.getBytes(columnIndex);
        return createMultipartFile(bytes);
    }

    /**
     * CallableStatementからファイルデータを取得し、MultipartFileに変換する。
     * NULLの場合はnullを返す。
     *
     * @param cs CallableStatement
     * @param columnIndex カラムインデックス
     *
     * @return MultipartFile
     *
     * @throws SQLException SQL例外
     */
    @Override
    public MultipartFile getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        byte[] bytes = cs.getBytes(columnIndex);
        return createMultipartFile(bytes);
    }

    /**
     * MultipartFileをバイト配列に変換する。
     * .txtファイルのみUTF-8に変換し、その他はそのままバイナリデータを取得。
     *
     * @param parameter MultipartFile
     *
     * @return バイト配列
     *
     * @throws IOException 入出力例外
     */
    private byte[] getFileBytes(MultipartFile parameter) throws IOException {
        String fileName = parameter.getOriginalFilename().toLowerCase();

        // ファイルの拡張子が.txtかどうか
        if (fileName.endsWith(".txt")) {

            // .txtファイルはUTF-8に変換
            return convertMultipartFileToUTF8Bytes(parameter);
        }

        // それ以外のファイルはそのままバイナリデータとして取得
        return parameter.getBytes();
    }

    /**
     * バイナリデータからMultipartFileを生成する。
     * NULLの場合はnullを返す。
     *
     * @param bytes ファイルのバイトデータ
     *
     * @return MultipartFile
     */
    private MultipartFile createMultipartFile(byte[] bytes) {
        return (bytes != null) ? new InMemoryMultipartFile(bytes) : null;
    }

    /**
     * UTF-8に変換するメソッド。
     * .txtファイルの内容をUTF-8で読み込み、UTF-8で書き出す。
     *
     * @param file MultipartFile
     *
     * @return UTF-8に変換されたバイト配列
     *
     * @throws IOException 入出力例外
     */
    private byte[] convertMultipartFileToUTF8Bytes(MultipartFile file) throws IOException {
        InputStream inputStream = null;
        BufferedReader reader = null;
        BufferedWriter writer = null;
        ByteArrayOutputStream byteArrayOutputStream = null;

        try {
            inputStream = file.getInputStream();
            byteArrayOutputStream = new ByteArrayOutputStream();

            // ファイルの内容をUTF-8で読み込む
            reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            // UTF-8で書き出し
            writer = new BufferedWriter(new OutputStreamWriter(byteArrayOutputStream, StandardCharsets.UTF_8));

            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
            writer.flush();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new IOException("Error processing file", e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (writer != null) {
                    writer.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                if (byteArrayOutputStream != null) {
                    byteArrayOutputStream.close();
                }
            } catch (IOException ex) {
                // リソースのクローズに失敗した場合の処理（ログを出す等）
            }
        }
    }

    /**
     * InMemoryMultipartFileクラスは、MultipartFileの実装です。
     * バイト配列からファイルを保持し、必要なメソッドを提供します。
     */
    private static class InMemoryMultipartFile implements MultipartFile {
        private final byte[] content;

        public InMemoryMultipartFile(byte[] content) {
            this.content = content;
        }

        @Override
        public String getName() {
            return "file";
        }

        @Override
        public String getOriginalFilename() {
            return "file";
        }

        @Override
        public String getContentType() {
            return "application/octet-stream";
        }

        @Override
        public boolean isEmpty() {
            return content.length == 0;
        }

        @Override
        public long getSize() {
            return content.length;
        }

        @Override
        public byte[] getBytes() {
            return content;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(content);
        }

        @Override
        public void transferTo(File dest) throws IOException, IllegalStateException {
            try (OutputStream os = new FileOutputStream(dest)) {
                os.write(content);
            }
        }
    }
}
