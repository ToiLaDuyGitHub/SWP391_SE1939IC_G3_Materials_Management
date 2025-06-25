package controller.request;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
import dao.MaterialDAO;
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
import model.dto.MaterialDTO;

/**
 *
 * @author ADMIN
 */
@WebServlet(urlPatterns = {"/add-material-modal"})
public class AddMaterialModalController extends HttpServlet {

    private MaterialDAO materialDAO;

    @Override
    public void init() throws ServletException {
        materialDAO = new MaterialDAO();
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
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
            out.println("<title>Servlet AddMaterialModalController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AddMaterialModalController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

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
        HttpSession session = request.getSession();
        List<MaterialDTO> materials = (List<MaterialDTO>) session.getAttribute("materials");
        // Tải materials nếu chưa có
        if (materials == null) {
            materials = materialDAO.getMaterialsWithCategoryAndSupplier();
            session.setAttribute("materials", materials);
        }
        request.getRequestDispatcher("/Ordering-requirements/Create-export-order.jsp").forward(request, response);
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
        HttpSession session = request.getSession();
        String[] selectedIds = request.getParameterValues("materialIds");
        List<MaterialDTO> materials = (List<MaterialDTO>) session.getAttribute("materials");
        List<MaterialDTO> selectedMaterials = (List<MaterialDTO>) session.getAttribute("selectedMaterials");

        // Tải materials nếu chưa có
        if (materials == null) {
            materials = materialDAO.getMaterialsWithCategoryAndSupplier();
            session.setAttribute("materials", materials);
        }

        // Khởi tạo selectedMaterials nếu chưa có
        if (selectedMaterials == null) {
            selectedMaterials = new ArrayList<>();
            session.setAttribute("selectedMaterials", selectedMaterials);
        }

        // Thêm vật tư được chọn
        if (selectedIds != null) {
            for (String id : selectedIds) {
                try {
                    int materialId = Integer.parseInt(id);
                    for (MaterialDTO material : materials) {
                        if (material.getMaterialID() == materialId && !selectedMaterials.contains(material)) {
                            selectedMaterials.add(material);
                            break;
                        }
                    }
                } catch (NumberFormatException e) {
                }
            }
        }

        // Cập nhật session
        session.setAttribute("selectedMaterials", selectedMaterials);

        // Chuyển hướng về trang Tạo đơn xuất kho
        response.sendRedirect(request.getContextPath() + "/create-export-order");
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
