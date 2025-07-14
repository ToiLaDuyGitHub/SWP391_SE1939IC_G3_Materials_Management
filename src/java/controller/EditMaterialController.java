/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.MaterialDAO;
import model.Material;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.nio.file.Paths;

/**
 *
 * @author Admin
 */
@WebServlet(name = "EditMaterialController", urlPatterns = {"/edit-material"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10, // 10MB
        maxRequestSize = 1024 * 1024 * 50)   // 50MB
public class EditMaterialController extends HttpServlet {
    
    private static final String UPLOAD_DIR = "uploads";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
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
        request.getRequestDispatcher("/EditMaterial.jsp").forward(request, response);
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
        MaterialDAO materialDAO = new MaterialDAO();
        try {
            // Lấy các tham số từ form
            int materialID = Integer.parseInt(request.getParameter("materialID"));
            String materialName = request.getParameter("materialName");
            int subcategoryID = Integer.parseInt(request.getParameter("subcategoryID"));
            String detail = request.getParameter("detail");
            String oldImageUrl = request.getParameter("imageUrl");

            // Xử lý upload hình ảnh mới
            String imageUrl = oldImageUrl; // Giữ URL hình ảnh cũ nếu không có hình mới
            Part filePart = request.getPart("image");
            if (filePart != null && filePart.getSize() > 0) {
                String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdir();
                }
                String filePath = uploadPath + File.separator + fileName;
                filePart.write(filePath);
                imageUrl = UPLOAD_DIR + "/" + fileName;
            }

            // Gọi phương thức updateMaterial
            materialDAO.updateMaterial(materialID, materialName, subcategoryID, imageUrl, detail);

            request.getSession().setAttribute("successMessage", "Cập nhật vật tư thành công!");
            response.sendRedirect(request.getContextPath() + "/manage-material");
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi khi cập nhật vật tư: " + e.getMessage());
            request.getRequestDispatcher("/manage-material").forward(request, response);
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Vui lòng nhập đúng định dạng số cho các trường số!");
            request.getRequestDispatcher("/manage-material").forward(request, response);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
