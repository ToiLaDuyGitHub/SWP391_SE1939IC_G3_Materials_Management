package controller;

import dao.CategoryDAO;
import dao.MaterialDAO;
import dao.PurchaseRequestDAO;
import dao.PurchaseRequestMaterialsDAO;
import dao.SubCategoryDAO;
import dao.UserDAO;
import dao.UnitDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import model.Category;
import model.Material;
import model.MaterialQuantity;
import model.PurchaseRequest;
import model.PurchaseRequestMaterials;
import model.SubCategory;
import model.User;
import model.dto.User_Role;
import util.DBUtil;

// Định nghĩa servlet để quản lý nhập kho vật tư với URL "/import-materials"
@WebServlet(name = "ManageImportController", urlPatterns = { "/import-materials" })
public class ManageImportController extends HttpServlet {

    // Xử lý các yêu cầu GET (hiển thị trang, tìm kiếm, xác thực, xem chi tiết)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Đặt mã hóa UTF-8 để hỗ trợ tiếng Việt
        request.setCharacterEncoding("UTF-8");
        // Lấy tham số "action" từ URL để xác định hành động
        String action = request.getParameter("action");
        
        try {
            if (action == null) {
                // Nếu không có action, hiển thị trang nhập kho vật tư
                showImportPage(request, response);
            } else if (action.equals("search")) {
                // Xử lý tìm kiếm vật tư
                searchMaterials(request, response);
            } else if (action.equals("validate")) {
                // Xác thực tên vật tư và danh mục
                validateMaterial(request, response);
            } else if (action.equals("showImportFromDirector")) {
                // Hiển thị trang nhập kho từ các đơn đã duyệt
                showImportFromDictorPage(request, response);
            } else if (action.equals("viewDetail")) {
                // Xem chi tiết đơn yêu cầu nhập kho
                showImportRequestDetail(request, response);
            }
        } catch (Exception e) {
            // In lỗi nếu có vấn đề trong quá trình xử lý
            e.printStackTrace();
        }
    }

    // Xử lý các yêu cầu POST (nhập kho vật tư, nhập từ đơn đã duyệt)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Đặt mã hóa UTF-8 để hỗ trợ tiếng Việt
        request.setCharacterEncoding("UTF-8");
        // Lấy tham số "action" từ yêu cầu
        String action = request.getParameter("action");

        if (action != null && action.equals("import")) {
            // Xử lý nhập kho vật tư từ form nhập tay
            importMaterials(request, response);
        } else if (action != null && action.equals("importFromDirector")) {
            // Xử lý nhập kho từ đơn đã duyệt
            importMaterialsFromDirector(request, response);
        } else {
            // Nếu không có action hoặc action không hợp lệ, chuyển về trang chính
            response.sendRedirect(request.getContextPath() + "/import-materials");
        }
    }

    // Hiển thị trang nhập kho từ các đơn đã duyệt
    private void showImportFromDictorPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        // Khởi tạo DAO để lấy danh sách yêu cầu mua hàng đã duyệt
        PurchaseRequestDAO prDAO = new PurchaseRequestDAO();
        List<PurchaseRequest> approvedRequests = prDAO.getApprovedRequestsWithImportStatus();

        // Tạo Map để lưu trạng thái nhập kho của từng đơn
        Map<Integer, Boolean> importStatusMap = new HashMap<>();
        for (PurchaseRequest pr : approvedRequests) {
            // Kiểm tra xem đơn đã được nhập kho chưa và lưu vào Map
            importStatusMap.put(pr.getPurchaseRequestID(), prDAO.isImported(pr.getPurchaseRequestID()));
        }

        // Khởi tạo UserDAO để sử dụng trong JSP (lấy thông tin người dùng)
        UserDAO userDao = new UserDAO();
        // Lưu dữ liệu vào request để gửi đến JSP
        request.setAttribute("userDao", userDao);
        request.setAttribute("approvedRequests", approvedRequests);
        request.setAttribute("importStatusMap", importStatusMap);
        // Chuyển đến trang JSP hiển thị danh sách đơn đã duyệt
        request.getRequestDispatcher("/import-materials/import-materials-from-director.jsp").forward(request, response);
    }

    // Hiển thị trang nhập kho vật tư
    private void showImportPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Lấy danh sách danh mục để hiển thị trong dropdown
        CategoryDAO categoryDAO = new CategoryDAO();
        List<Category> categories = categoryDAO.getAllCategories();
        // Lưu danh sách danh mục vào request
        request.setAttribute("categories", categories);

        // Chuyển đến trang JSP nhập kho vật tư
        request.getRequestDispatcher("/import-materials/import-materials.jsp").forward(request, response);
    }

    // Xử lý tìm kiếm vật tư theo từ khóa
    private void searchMaterials(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Lấy từ khóa tìm kiếm từ request
            String keyword = request.getParameter("keyword");
            List<Material> materials = new ArrayList<>();

            // Nếu có từ khóa, tìm kiếm vật tư theo tên
            if (keyword != null && !keyword.trim().isEmpty()) {
                MaterialDAO materialDAO = new MaterialDAO();
                materials = materialDAO.suggestMaterialsByName(keyword);
            }

            // Thiết lập response trả về dưới dạng JSON
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            // Tạo chuỗi JSON từ danh sách vật tư
            StringBuilder json = new StringBuilder();
            json.append("[");
            for (int i = 0; i < materials.size(); i++) {
                Material m = materials.get(i);
                if (i > 0) {
                    json.append(",");
                }
                json.append("{")
                        .append("\"id\":").append(m.getMaterialID()).append(",")
                        .append("\"name\":\"").append(m.getMaterialName()).append("\",")
                        .append("\"categoryId\":").append(m.getCategory().getCategoryID()).append(",")
                        .append("\"supplier\":\"").append(m.getSupplierID().getSupplierName()).append("\"")
                        .append("}");
            }
            json.append("]");

            // Gửi JSON về client
            response.getWriter().write(json.toString());
        } catch (Exception e) {
            // Gửi lỗi nếu có vấn đề trong quá trình xử lý
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // Xác thực tên vật tư và danh mục
    private void validateMaterial(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Lấy thông tin tên vật tư và ID danh mục từ request
            String materialName = request.getParameter("materialName");
            int categoryID = Integer.parseInt(request.getParameter("categoryID"));

            // Tìm kiếm vật tư theo tên
            MaterialDAO materialDAO = new MaterialDAO();
            List<Material> materials = materialDAO.suggestMaterialsByName(materialName);

            // Kiểm tra xem vật tư có tồn tại và thuộc danh mục đã chọn không
            boolean isValid = false;
            Material validMaterial = null;
            for (Material material : materials) {
                if (material.getMaterialName().equalsIgnoreCase(materialName)
                        && material.getCategory().getCategoryID() == categoryID) {
                    isValid = true;
                    validMaterial = material;
                    break;
                }
            }

            // Thiết lập response trả về dưới dạng JSON
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            // Tạo chuỗi JSON với kết quả xác thực
            StringBuilder json = new StringBuilder();
            json.append("{")
                    .append("\"isValid\":").append(isValid);

            if (isValid && validMaterial != null) {
                json.append(",\"material\":{")
                        .append("\"supplier\":\"").append(validMaterial.getSupplierID().getSupplierName()).append("\"")
                        .append("}");
            } else {
                json.append(",\"errorMessage\":\"Tên vật tư hoặc danh mục không tồn tại!\"");
            }
            json.append("}");

            // Gửi JSON về client
            response.getWriter().write(json.toString());
        } catch (Exception e) {
            // Gửi lỗi nếu có vấn đề trong quá trình xử lý
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // Xử lý nhập kho vật tư từ form nhập tay
    private void importMaterials(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        try {
            // Lấy số lượng vật tư từ form
            int materialCount = Integer.parseInt(request.getParameter("materialCount"));
            // Lấy thông tin người dùng từ session
            User_Role userRole = (User_Role) session.getAttribute("userRole");
            int userID = userRole.getUserID();

            // Tạo danh sách để lưu thông tin vật tư
            List<Map<String, Integer>> materials = new ArrayList<>();

            // Lấy thông tin từng vật tư từ form
            for (int i = 0; i < materialCount; i++) {
                String materialIdStr = request.getParameter("materialId_" + i);
                String newQuantityStr = request.getParameter("newQuantity_" + materialIdStr);
                String brokenQuantityStr = request.getParameter("brokenQuantity_" + materialIdStr);

                if (materialIdStr != null && newQuantityStr != null && brokenQuantityStr != null) {
                    Map<String, Integer> material = new HashMap<>();
                    material.put("materialId", Integer.parseInt(materialIdStr)); // ID vật tư
                    material.put("usableQuantity", Integer.parseInt(newQuantityStr)); // Số lượng sử dụng được
                    material.put("brokenQuantity", Integer.parseInt(brokenQuantityStr)); // Số lượng hỏng
                    materials.add(material);
                }
            }

            // Kiểm tra nếu không có vật tư nào được chọn
            if (materials.isEmpty()) {
                throw new Exception("Không có vật tư nào được chọn!");
            }

            // Gọi DAO để xử lý nhập kho
            MaterialDAO materialDAO = new MaterialDAO();
            materialDAO.importMaterials(materials, userID);

            // Lưu thông báo thành công vào session
            session.setAttribute("successMessage", "Nhập kho vật tư thành công!");

            // Chuyển hướng về trang nhập kho
            response.sendRedirect(request.getContextPath() + "/import-materials");
            return;

        } catch (Exception e) {
            // In lỗi và lưu thông báo lỗi vào session
            e.printStackTrace();
            session.setAttribute("error", "Lỗi khi nhập kho: " + e.getMessage());
            // Chuyển hướng về trang nhập kho
            response.sendRedirect(request.getContextPath() + "/import-materials");
            return;
        }
    }

    // Xử lý nhập kho từ đơn đã duyệt
    private void importMaterialsFromDirector(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        try {
            // Lấy ID yêu cầu từ form
            String requestIdStr = request.getParameter("requestId");
            if (requestIdStr == null) {
                throw new Exception("Không tìm thấy thông tin đơn yêu cầu!");
            }
            int requestId = Integer.parseInt(requestIdStr);

            // Khởi tạo DAO để lấy thông tin đơn yêu cầu và vật tư
            PurchaseRequestDAO prDAO = new PurchaseRequestDAO();
            PurchaseRequestMaterialsDAO prMaterialsDAO = new PurchaseRequestMaterialsDAO();
            
            // Kiểm tra xem đơn đã được nhập kho chưa
            if (prDAO.isImported(requestId)) {
                throw new Exception("Đơn này đã được nhập kho!");
            }

            // Lấy danh sách vật tư từ đơn yêu cầu
            List<PurchaseRequestMaterials> prmList = prMaterialsDAO.getByPurchaseRequestId(requestId);
            if (prmList.isEmpty()) {
                throw new Exception("Không tìm thấy vật tư trong đơn yêu cầu!");
            }

            // Chuẩn bị danh sách vật tư để nhập kho
            List<Map<String, Integer>> materials = new ArrayList<>();
            for (PurchaseRequestMaterials prm : prmList) {
                Map<String, Integer> material = new HashMap<>();
                material.put("materialId", prm.getMaterialID()); // ID vật tư
                material.put("usableQuantity", prm.getQuantity()); // Số lượng sử dụng được
                material.put("brokenQuantity", 0); // Mặc định số lượng hỏng là 0
                materials.add(material);
            }

            // Lấy thông tin người nhập kho từ session
            User_Role userRole = (User_Role) session.getAttribute("userRole");
            int userID = userRole.getUserID();

            // Gọi DAO để xử lý nhập kho
            MaterialDAO materialDAO = new MaterialDAO();
            materialDAO.importMaterialsForPurchase(materials, userID, requestId);

            // Lưu thông báo thành công vào session
            session.setAttribute("successMessage", "Nhập kho vật tư từ đơn #" + requestId + " thành công!");

            // Chuyển hướng về trang danh sách đơn đã duyệt
            response.sendRedirect(request.getContextPath() + "/import-materials?action=showImportFromDirector");
            return;

        } catch (Exception e) {
            // In lỗi và lưu thông báo lỗi vào session
            e.printStackTrace();
            session.setAttribute("error", "Lỗi khi nhập kho: " + e.getMessage());
            // Chuyển hướng về trang danh sách đơn đã duyệt
            response.sendRedirect(request.getContextPath() + "/import-materials?action=showImportFromDirector");
            return;
        }
    }

    // Hiển thị chi tiết đơn yêu cầu nhập kho
    private void showImportRequestDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Lấy ID yêu cầu từ request
        String purchaseRequestIdStr = request.getParameter("purchaseRequestID");
        if (purchaseRequestIdStr == null) {
            // Nếu không có ID, chuyển về trang danh sách đơn đã duyệt
            response.sendRedirect(request.getContextPath() + "/import-materials?action=showImportFromDirector");
            return;
        }
        int purchaseRequestID = Integer.parseInt(purchaseRequestIdStr);

        // Lấy thông tin đơn yêu cầu mua
        PurchaseRequestDAO prDAO = new PurchaseRequestDAO();
        PurchaseRequest purchaseRequest = prDAO.getById(purchaseRequestID);
        
        // Lấy danh sách vật tư và số lượng từ đơn yêu cầu
        PurchaseRequestMaterialsDAO prMaterialsDAO = new PurchaseRequestMaterialsDAO();
        List<PurchaseRequestMaterials> prmList = prMaterialsDAO.getByPurchaseRequestId(purchaseRequestID);

        // Tạo danh sách Map để lưu thông tin vật tư chi tiết
        List<Map<String, Object>> requestMaterials = new ArrayList<>();
        MaterialDAO materialDAO = new MaterialDAO();
        UnitDAO unitDAO = new UnitDAO();
        UserDAO userDao = new UserDAO();

        for (PurchaseRequestMaterials prm : prmList) {
            Map<String, Object> materialDetailMap = new HashMap<>();
            materialDetailMap.put("materialID", prm.getMaterialID()); // ID vật tư
            materialDetailMap.put("quantity", prm.getQuantity()); // Số lượng vật tư

            // Lấy tên vật tư
            Material material = materialDAO.getMaterialById(prm.getMaterialID());
            if (material != null) {
                materialDetailMap.put("materialName", material.getMaterialName());
            } else {
                materialDetailMap.put("materialName", "N/A");
            }

            // Lấy đơn vị
            String unit = unitDAO.getMinUnitByMaterialId(prm.getMaterialID());
            materialDetailMap.put("unit", unit != null ? unit : "N/A");

            // Thêm vào danh sách
            requestMaterials.add(materialDetailMap);
        }
        
        // Lưu dữ liệu vào request để gửi đến JSP
        request.setAttribute("requestId", purchaseRequestID);
        request.setAttribute("purchaseRequest", purchaseRequest);
        request.setAttribute("requestMaterials", requestMaterials);
        request.setAttribute("userDao", userDao); // Để lấy tên người dùng trong JSP
        
        // Chuyển đến trang JSP hiển thị chi tiết đơn yêu cầu
        request.getRequestDispatcher("/import-materials/import-materials-from-director-detail.jsp").forward(request, response);
    }
}