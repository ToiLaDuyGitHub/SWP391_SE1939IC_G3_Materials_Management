/*
 * Click nfsb://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nfsb://.netbeans/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.RequestDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dto.User_Role;
import java.io.IOException;
import java.sql.SQLException;

/**
 *
 * @author Admin
 */
@WebServlet(name = "UpdateRequestStatus", urlPatterns = {"/update-request-status"})
public class UpdateRequestStatus extends HttpServlet {

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/request-for-director");
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            session = request.getSession(true); // Tạo session mới nếu chưa có
            session.setAttribute("message", "Phiên làm việc đã hết hạn. Vui lòng đăng nhập lại.");
            session.setAttribute("messageType", "error");
            response.sendRedirect(request.getContextPath() + "/auth/login.jsp");
            return;
        }

        Object userRoleObj = session.getAttribute("userRole");
        if (userRoleObj == null || !(userRoleObj instanceof User_Role)) {
            session.setAttribute("message", "Thông tin vai trò không hợp lệ. Vui lòng đăng nhập lại.");
            session.setAttribute("messageType", "error");
            response.sendRedirect(request.getContextPath() + "/auth/login.jsp");
            return;
        }

        User_Role userRole = (User_Role) userRoleObj;
        if (userRole.getRoleID() != 3) {
            session.setAttribute("message", "Bạn không có quyền thực hiện chức năng này.");
            session.setAttribute("messageType", "error");
            response.sendRedirect(request.getContextPath() + "/request-for-director");
            return;
        }

        // Lấy tham số từ request
        String requestCode = request.getParameter("requestCode");
        String action = request.getParameter("action");
        String requestType = request.getParameter("requestType");

        if (requestCode == null || action == null || requestType == null) {
            session.setAttribute("message", "Thông tin yêu cầu không hợp lệ.");
            session.setAttribute("messageType", "error");
            response.sendRedirect(request.getContextPath() + "/request-for-director");
            return;
        }

        RequestDAO requestDAO = null;
        try {
            requestDAO = new RequestDAO();

            // Lấy requestId từ requestCode
            int requestId = requestDAO.getRequestIdFromCode(requestCode, requestType);
            if (requestId == -1) {
                session.setAttribute("message", "Không tìm thấy yêu cầu với mã: " + requestCode);
                session.setAttribute("messageType", "error");
                response.sendRedirect(request.getContextPath() + "/request-for-director");
                return;
            }

            // Xác định trạng thái mới dựa trên hành động
            int status = action.equals("approve") ? 1 : 2; // 1: Duyệt, 2: Từ chối

            // Cập nhật trạng thái yêu cầu
            requestDAO.updateRequestStatus(requestId, requestType, status);
            session.setAttribute("message", "Cập nhật trạng thái yêu cầu thành công.");
            session.setAttribute("messageType", "success");

        } catch (SQLException e) {
            e.printStackTrace();
            session.setAttribute("message", "Lỗi cơ sở dữ liệu: " + e.getMessage());
            session.setAttribute("messageType", "error");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("message", "Lỗi không xác định: " + e.getMessage());
            session.setAttribute("messageType", "error");
        }

        // Luôn chuyển hướng về /request-for-director
        response.sendRedirect(request.getContextPath() + "/request-for-director");
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Servlet for updating request status";
    }
}