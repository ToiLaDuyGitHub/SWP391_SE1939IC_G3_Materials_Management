/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import dao.RequestDAO;
import java.io.IOException;
import java.sql.SQLException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author Admin
 */
@WebServlet(name="DeleteRequest", urlPatterns={"/delete-request"})
public class DeleteRequest extends HttpServlet {
       private RequestDAO requestDAO;

    @Override
    public void init() throws ServletException {
        requestDAO = new RequestDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        HttpSession session = request.getSession();
        
        try {
            // Lấy tham số từ form
            int requestId = Integer.parseInt(request.getParameter("requestId"));
            String requestType = request.getParameter("requestType");

            // Xóa yêu cầu
            requestDAO.deleteRequest(requestId, requestType);

            // Lưu thông báo thành công vào session
            session.setAttribute("message", "Xóa yêu cầu thành công!");
            session.setAttribute("messageType", "success");

        } catch (SQLException e) {
            // Lưu thông báo lỗi vào session
            session.setAttribute("message", "Lỗi khi xóa yêu cầu: " + e.getMessage());
            session.setAttribute("messageType", "error");
            e.printStackTrace();
        } catch (NumberFormatException e) {
            session.setAttribute("message", "ID yêu cầu không hợp lệ!");
            session.setAttribute("messageType", "error");
            e.printStackTrace();
        }

        // Chuyển hướng về trang danh sách yêu cầu
        response.sendRedirect(request.getContextPath() + "/search-request");
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
