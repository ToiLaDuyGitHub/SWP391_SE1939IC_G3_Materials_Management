/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import dao.MaterialDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Admin
 */
@WebServlet(name="updateInventory", urlPatterns={"/update-inventory"})
public class updateInventory extends HttpServlet {
    private MaterialDAO materialDAO;

    @Override
    public void init() throws ServletException {
        materialDAO = new MaterialDAO();
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
       try {
            // Lấy các tham số từ form
            int materialID = Integer.parseInt(request.getParameter("materialID"));
            int usableQuantity = Integer.parseInt(request.getParameter("usableQuantity"));
            int brokenQuantity = Integer.parseInt(request.getParameter("brokenQuantity"));

            // Kiểm tra số lượng
            if (usableQuantity < 0 || brokenQuantity < 0) {
                request.setAttribute("errorMessage", "Số lượng không được âm");
                request.getRequestDispatcher("/inventory").forward(request, response);
                return;
            }

            // Cập nhật số lượng trong cơ sở dữ liệu
            materialDAO.updateMaterialQuantities(materialID, usableQuantity, brokenQuantity);

            // Đặt thông báo thành công
            request.getSession().setAttribute("successMessage", "Cập nhật số lượng vật tư thành công");

            // Chuyển hướng đến trang danh sách vật tư
            response.sendRedirect(request.getContextPath() + "/inventory");

        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Dữ liệu số lượng không hợp lệ");
            request.getRequestDispatcher("/inventory").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Lỗi khi cập nhật số lượng: " + e.getMessage());
            request.getRequestDispatcher("/inventory").forward(request, response);
        }
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
