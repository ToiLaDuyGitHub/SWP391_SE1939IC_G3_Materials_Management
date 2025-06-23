/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import dao.RequestDAO;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dto.RequestDTO;
import model.User;
import model.dto.User_Role;


/**
 *
 * @author Admin
 */
@WebServlet(name="RequestsForStaff", urlPatterns={"/request-for-staff"})
public class RequestsForStaff extends HttpServlet {
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    

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
            // Lấy phiên và kiểm tra người dùng đã xác thực
            HttpSession session = request.getSession(false);
            User_Role userRole = (session != null) ? (User_Role) session.getAttribute("userRole") : null;

            if (userRole == null || !userRole.isIsActive()) {
                // Nếu người dùng chưa đăng nhập hoặc tài khoản không hoạt động, chuyển hướng đến trang đăng nhập
                request.setAttribute("errorMessage", "Vui lòng đăng nhập để xem danh sách yêu cầu!");
                request.getRequestDispatcher("/auth/login.jsp").forward(request, response);
                return;
            }

            // Lấy userId từ userRole (giả sử User_Role có phương thức getUserId)
            int userId = userRole.getUserID(); // Cần đảm bảo User_Role có phương thức này

            // Lấy các yêu cầu do người dùng tạo bằng RequestDAO
            RequestDAO requestDAO = new RequestDAO();
            List<RequestDTO> userRequests = requestDAO.getRequestsByCreator(userId);

            // Đặt danh sách yêu cầu làm thuộc tính
            request.setAttribute("processedRequests", userRequests);

            // Chuyển hướng đến Request_List.jsp để hiển thị
            request.getRequestDispatcher("/Request/Request_List.jsp").forward(request, response);

        } catch (Exception e) {
            // Xử lý lỗi
            e.printStackTrace();
            request.setAttribute("error", "Đã xảy ra lỗi khi lấy danh sách yêu cầu: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
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
