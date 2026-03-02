//package fms.servlets;
//
//import java.io.IOException;
//
//import jakarta.servlet.RequestDispatcher;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//@WebServlet("/login")
//public class LoginServlet extends HttpServlet {
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        // リクエストエンコーディングを UTF-8 に設定
//        request.setCharacterEncoding("UTF-8");
//
//        // 文字エンコーディングを UTF-8 に設定
//        response.setContentType("text/html; charset=UTF-8");
//        response.setCharacterEncoding("UTF-8");
//
//        // login.html へのパスを指定
//        String loginPage = "/WEB-INF/classes/templates/login/login.html";
//        RequestDispatcher dispatcher = request.getRequestDispatcher(loginPage);
//        dispatcher.forward(request, response);
//    }
//}
