/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import dao.RequestDAO;
import dao.UserDAO;
import java.io.IOException;
import java.sql.Date;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dto.RequestDTO;
import model.dto.User_Role;

/**
 *
 * @author Admin
 */
@WebServlet(name="SearchDirectorRequest", urlPatterns={"/search-director-request"})
public class SearchDirectorRequest extends HttpServlet {
   private UserDAO userDAO;
    private RequestDAO requestDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
        requestDAO = new RequestDAO();
    }
    

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        try {
            // Kiểm tra phiên đăng nhập
            HttpSession session = request.getSession();
            User_Role userRole = (User_Role) session.getAttribute("userRole");
            if (userRole == null || userRole.getRoleID() != 3) { // RoleID = 3 là giám đốc
                request.setAttribute("message", "Vui lòng đăng nhập với vai trò giám đốc.");
                request.setAttribute("messageType", "error");
                request.getRequestDispatcher("/Request/Request_List_Director.jsp").forward(request, response);
                return;
            }

            // Lấy danh sách người dùng để hiển thị trong ô chọn
            List<User_Role> userList = userDAO.getAllUsersWithRoles();
            request.setAttribute("userList", userList);

            // Lấy tham số tìm kiếm
            String requestCode = request.getParameter("requestCode");
            if (requestCode != null) {
                requestCode = requestCode.trim();
            }
            String startDateStr = request.getParameter("startDate");
            String endDateStr = request.getParameter("endDate");
            String createdByIdStr = request.getParameter("createdById");

            Date startDate = null;
            Date endDate = null;
            Integer createdById = null;

            // Xử lý tham số ngày
            try {
                if (startDateStr != null && !startDateStr.isEmpty()) {
                    startDate = Date.valueOf(startDateStr);
                }
                if (endDateStr != null && !endDateStr.isEmpty()) {
                    endDate = Date.valueOf(endDateStr);
                    endDate = new Date(endDate.getTime() + 24 * 60 * 60 * 1000); // Bao gồm cả ngày endDate
                }
                if (createdByIdStr != null && !createdByIdStr.isEmpty()) {
                    createdById = Integer.parseInt(createdByIdStr);
                }
            } catch (IllegalArgumentException e) {
                request.setAttribute("message", "Định dạng ngày hoặc ID nhân viên không hợp lệ.");
                request.setAttribute("messageType", "error");
                request.getRequestDispatcher("/Request/Request_List_Director.jsp").forward(request, response);
                return;
            }

            // Tìm kiếm yêu cầu
            List<RequestDTO> requests = requestDAO.searchDirectorRequests(requestCode, startDate, endDate, createdById);
            request.setAttribute("processedRequests", requests);

            // Chuyển tiếp tới trang JSP
            request.getRequestDispatcher("/Request/Request_List_Director.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "Lỗi khi tìm kiếm: " + e.getMessage());
            request.setAttribute("messageType", "error");
            request.getRequestDispatcher("/Request/Request_List_Director.jsp").forward(request, response);
        }
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
