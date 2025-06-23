/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import dao.RequestDAO;
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
@WebServlet(name="SearchRequest", urlPatterns={"/search-request"})
public class SearchRequest extends HttpServlet {
   
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
       
        try {
            HttpSession session = request.getSession();
            User_Role userRole = (User_Role) session.getAttribute("userRole");
            if (userRole == null) {
                System.out.println("userRole is null - User not logged in");
                request.setAttribute("message", "Vui lòng đăng nhập để tìm kiếm yêu cầu.");
                request.setAttribute("messageType", "error");
                request.getRequestDispatcher("/Request/Request_List.jsp").forward(request, response);
                return;
            }

            int userId = userRole.getUserID();
            System.out.println("Retrieved userId: " + userId);

            String requestCode = request.getParameter("requestCode");
            if (requestCode != null) {
                requestCode = requestCode.trim();
            }
            String startDateStr = request.getParameter("startDate");
            String endDateStr = request.getParameter("endDate");

            System.out.println("requestCode: " + requestCode);
            System.out.println("startDateStr: " + startDateStr);
            System.out.println("endDateStr: " + endDateStr);

            Date startDate = null;
            Date endDate = null;
            try {
                if (startDateStr != null && !startDateStr.isEmpty()) {
                    startDate = Date.valueOf(startDateStr);
                }
                if (endDateStr != null && !endDateStr.isEmpty()) {
                    endDate = Date.valueOf(endDateStr);
                    endDate = new Date(endDate.getTime() + 24 * 60 * 60 * 1000); // Bao gồm cả ngày endDate
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid date format: startDateStr=" + startDateStr + ", endDateStr=" + endDateStr);
                request.setAttribute("message", "Định dạng ngày không hợp lệ.");
                request.setAttribute("messageType", "error");
                request.getRequestDispatcher("/Request/Request_List.jsp").forward(request, response);
                return;
            }

            RequestDAO dao = new RequestDAO();
            List<RequestDTO> requests = dao.searchRequests(requestCode, startDate, endDate, userId);
            System.out.println("Number of requests found: " + requests.size());

            request.setAttribute("processedRequests", requests);
            request.getRequestDispatcher("/Request/Request_List.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error during search: " + e.getMessage());
            request.setAttribute("message", "Lỗi khi tìm kiếm: " + e.getMessage());
            request.setAttribute("messageType", "error");
            request.getRequestDispatcher("/Request/Request_List.jsp").forward(request, response);
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
