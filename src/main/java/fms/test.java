package fms;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class test {

    public static void main(String[] args) {
        // PostgreSQLデータベースへの接続情報
        String url = "jdbc:postgresql://192.168.2.230:5432/file_management_system";
        String user = "postgres";
        String password = "Justin2024";

        try {
            // JDBCドライバのロード
            Class.forName("org.postgresql.Driver");

            // データベースへの接続
            Connection connection = DriverManager.getConnection(url, user, password);

            // クエリの実行例（ここではSELECT文を実行しています）
            String query = "SELECT * FROM m_team";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    // 結果の処理例（ここではコンソールに出力しています）
                    System.out.println("team_id: " + resultSet.getInt("team_id"));
                    // 必要に応じて他の列も同様に処理
                }
            }

            // 接続を閉じる
            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}