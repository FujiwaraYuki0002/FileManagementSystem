//package fms.controller;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//import jakarta.servlet.RequestDispatcher;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//
//import fms.domain.LogDomain;
//import fms.dto.UserDto;
//import fms.entity.MUser;
//import fms.service.LoginService;
//import fms.util.LogUtil;
//
//@WebServlet("/login")
//public class LoginServlet extends HttpServlet {
//
//    private LoginService loginService;
//    private LogUtil logUtil;
//
//    @Override
//    public void init() throws ServletException {
//        // サービスやユーティリティの初期化（仮のインスタンス）
//        loginService = new LoginService(); // 実際のサービスに置き換え
//        logUtil = new LogUtil(); // 実際のログユーティリティに置き換え
//    }
//
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        // セッション情報を削除
//        HttpSession session = request.getSession(false);
//        if (session != null) {
//            session.invalidate(); // 既存セッションを無効化
//        }
//
//        // ログイン画面（login.html）を表示
//        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/classes/templates/login/login.html");
//        dispatcher.forward(request, response);
//    }
//
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        // フォームデータの取得
//        String userId = request.getParameter("userId");
//        String password = request.getParameter("password");
//
//        List<String> errorMessageList = new ArrayList<>();
//
//        // 入力チェック（仮のチェック、空白ならエラー）
//        if (userId == null || userId.isEmpty() || password == null || password.isEmpty()) {
//            errorMessageList.add("ユーザーIDまたはパスワードを入力してください。");
//        }
//
//        // エラーがなければ、ログインチェック
//        if (errorMessageList.isEmpty()) {
//            // ログインサービスを使ってユーザー情報を照合
//            MUser loginUser = loginService.loginCheck(userId, password); // 仮のログインチェック
//
//            if (loginUser != null) {
//                // ログイン成功
//                logUtil.addLog(LogDomain.CODE_LOG_SECTION_OPE, "ログイン", "LOGIN",
//                        loginUser.getUserId(), Thread.currentThread().getStackTrace()[1].getClassName());
//
//                // 新規セッションの作成
//                HttpSession session = request.getSession(true);
//                UserDto userDto = new UserDto();
//                userDto.setUserId(loginUser.getUserId());
//                userDto.setUserName(loginUser.getUserName());
//                userDto.setRole(loginUser.getRole());
//
//                session.setAttribute("loginUser", userDto); // セッションにユーザー情報を格納
//
//                // ファイル検索画面に遷移（仮）
//                response.sendRedirect("file/search"); // 実際には適切なページにリダイレクト
//                return;
//            } else {
//                // ログイン失敗
//                errorMessageList.add("ユーザーIDまたはパスワードが間違っています。");
//            }
//        }
//
//        // 入力エラーがあれば、エラーメッセージを表示
//        request.setAttribute("errorMessageList", errorMessageList); // エラーメッセージをリクエストスコープに設定
//        RequestDispatcher dispatcher = request.getRequestDispatcher("/login.html"); // ログイン画面にフォワード
//        dispatcher.forward(request, response);
//    }
//}
