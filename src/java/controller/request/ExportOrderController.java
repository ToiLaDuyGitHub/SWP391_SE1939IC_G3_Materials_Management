/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller.request;

import dao.MaterialDAO;
import dao.RequestDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import model.ExportRequest;
import model.ExportRequestMaterial;
import model.dto.User_Role;
import model.dto.MaterialDTO;

/**
 *
 * @author ADMIN
 */
@WebServlet(name="ExportOrderController", urlPatterns={"/create-export-order"})
public class ExportOrderController extends HttpServlet {
   
    private MaterialDAO materialDAO;
    private RequestDAO requestDao;

    @Override
    public void init() throws ServletException {
        materialDAO = new MaterialDAO();
        requestDao = new RequestDAO();
    }
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ExportOrderController</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ExportOrderController at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
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
        HttpSession session = request.getSession();
        try {
            List<MaterialDTO> materials = (List<MaterialDTO>) session.getAttribute("materials");
            if (materials == null) {
                materials = materialDAO.getMaterialsWithCategoryAndSupplier();
                session.setAttribute("materials", materials);
            }
            if (session.getAttribute("selectedMaterials") == null) {
                session.setAttribute("selectedMaterials", new ArrayList<MaterialDTO>());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        request.getRequestDispatcher("/Ordering-requirements/Create-export-order.jsp").forward(request, response);
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
            // Lấy thông tin người dùng từ session với key "userRole"
            User_Role userRole = (User_Role) session.getAttribute("userRole");
            if (userRole == null) {
                request.setAttribute("errorMessage", "Vui lòng đăng nhập để gửi đơn xuất kho.");
                request.getRequestDispatcher("/Ordering-requirements/Create-export-order.jsp").forward(request, response);
                return;
            }
            int createdByID = userRole.getUserID(); 
            // Kiểm tra tính hợp lệ của createdByID
            if (createdByID <= 0) {
                request.setAttribute("errorMessage", "ID người dùng không hợp lệ: " + createdByID);
                request.getRequestDispatcher("/Ordering-requirements/Create-export-order.jsp").forward(request, response);
                return;
            }
            // Lấy thông tin từ form
            String note = request.getParameter("notes");
            List<MaterialDTO> selectedMaterials = (List<MaterialDTO>) session.getAttribute("selectedMaterials");
            if (selectedMaterials == null || selectedMaterials.isEmpty()) {
                request.setAttribute("errorMessage", "Vui lòng chọn ít nhất một vật tư.");
                request.getRequestDispatcher("/Ordering-requirements/Create-export-order.jsp").forward(request, response);
                return;
            }
            // Tạo danh sách vật tư và số lượng
            List<ExportRequestMaterial> requestMaterials = new ArrayList<>();
            for (MaterialDTO material : selectedMaterials) {
                int materialId = material.getMaterialID();
                String quantityParam = request.getParameter("quantities[" + materialId + "]");
                int quantity = (quantityParam != null && !quantityParam.isEmpty()) ? Integer.parseInt(quantityParam) : 0;
                if (quantity <= 0) {
                    request.setAttribute("errorMessage", "Số lượng vật tư phải lớn hơn 0.");
                    request.getRequestDispatcher("/Ordering-requirements/Create-export-order.jsp").forward(request, response);
                    return;
                }
                ExportRequestMaterial requestMaterial = new ExportRequestMaterial(0, materialId, quantity);
                requestMaterials.add(requestMaterial);
            }
            // Tạo đơn xuất kho
            ExportRequest exportRequest = new ExportRequest(createdByID, note);
            System.out.println("Tạo ExportRequest với Note: " + note + ", CreatedByID: " + createdByID);
            int exportRequestID = requestDao.createExportRequest(exportRequest);
            if (exportRequestID == -1) {
                request.setAttribute("errorMessage", "Lỗi khi tạo đơn xuất kho. Có thể không tìm thấy giám đốc để gán người duyệt hoặc CreatedByID không hợp lệ.");
                request.getRequestDispatcher("/Ordering-requirements/Create-export-order.jsp").forward(request, response);
                return;
            }
            // Gán ExportRequestID cho danh sách vật tư
            for (ExportRequestMaterial material : requestMaterials) {
                material.setExportRequestID(exportRequestID);
            }
            // Lưu vật tư và số lượng
            requestDao.saveExportRequestMaterials(requestMaterials);
            System.out.println("Luu danh sach vat tu thanh cong");
            // Xóa selectedMaterials sau khi gửi đơn thành công
            session.removeAttribute("selectedMaterials");
            System.out.println("Xóa selectedMaterials khỏi session");
            // Thông báo thành công
            request.setAttribute("successMessage", "Don xuat kho da duoc gui thanh cong!");
            request.getRequestDispatcher("/Ordering-requirements/Create-export-order.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Loi khi xu ly don xuat kho: " + e.getMessage());
            request.getRequestDispatcher("/Ordering-requirements/Create-export-order.jsp").forward(request, response);
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
