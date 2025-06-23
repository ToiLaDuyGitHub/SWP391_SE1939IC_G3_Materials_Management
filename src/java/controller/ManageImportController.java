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

@WebServlet(name = "ManageImportController", urlPatterns = { "/import-materials" })
public class ManageImportController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        try{
            if (action == null) {
            // Hiển thị trang nhập kho vật tư
            showImportPage(request, response);
        } else if (action.equals("search")) {
            // Xử lý tìm kiếm vật tư
            searchMaterials(request, response);
        } else if (action.equals("validate")) {
            // Xác thực tên vật tư và danh mục
            validateMaterial(request, response);
        } else if (action.equals("showImportFromDirector")) {
            showImportFromDictorPage(request, response);
        } else if (action.equals("viewDetail")) {
            showImportRequestDetail(request, response);
        }
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        if (action != null && action.equals("import")) {
            // Xử lý nhập kho vật tư
            importMaterials(request, response);
        } else if (action != null && action.equals("importFromDirector")) {
            // Xử lý nhập kho từ đơn đã duyệt
            importMaterialsFromDirector(request, response);
        } else {
            // Nếu không có action hoặc action không hợp lệ, chuyển về trang chính
            response.sendRedirect(request.getContextPath() + "/import-materials");
        }
    }

    private void showImportFromDictorPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        PurchaseRequestDAO prDAO = new PurchaseRequestDAO();
        List<PurchaseRequest> approvedRequests = prDAO.getApprovedRequestsWithImportStatus();

        // Tạo Map trạng thái nhập kho
        Map<Integer, Boolean> importStatusMap = new HashMap<>();
        for (PurchaseRequest pr : approvedRequests) {
            // Giả sử DAO trả về true/false cho từng đơn, bạn có thể lấy từ một hàm khác
            // hoặc join trong SQL
            // Ở đây ví dụ: nếu đơn đã có trong importhistory thì là true
            importStatusMap.put(pr.getPurchaseRequestID(), prDAO.isImported(pr.getPurchaseRequestID()));
        }

        UserDAO userDao = new UserDAO();
        request.setAttribute("userDao", userDao);
        request.setAttribute("approvedRequests", approvedRequests);
        request.setAttribute("importStatusMap", importStatusMap);
        request.getRequestDispatcher("/import-materials/import-materials-from-director.jsp").forward(request, response);
    }
    

    private void showImportPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Lấy danh sách danh mục để hiển thị trong dropdown
        CategoryDAO categoryDAO = new CategoryDAO();
        List<Category> categories = categoryDAO.getAllCategories();
        request.setAttribute("categories", categories);

        // Forward đến trang JSP
        request.getRequestDispatcher("/import-materials/import-materials.jsp").forward(request, response);
    }

    private void searchMaterials(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String keyword = request.getParameter("keyword");
            List<Material> materials = new ArrayList<>();

            if (keyword != null && !keyword.trim().isEmpty()) {
                MaterialDAO materialDAO = new MaterialDAO();
                materials = materialDAO.suggestMaterialsByName(keyword);
            }

            // Set content type to JSON for AJAX response
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            // Create JSON response
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

            response.getWriter().write(json.toString());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private void validateMaterial(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String materialName = request.getParameter("materialName");
            int categoryID = Integer.parseInt(request.getParameter("categoryID"));

            MaterialDAO materialDAO = new MaterialDAO();
            List<Material> materials = materialDAO.suggestMaterialsByName(materialName);

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

            // Set content type to JSON for AJAX response
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

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

            response.getWriter().write(json.toString());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private void importMaterials(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        try {
            // Lấy số lượng vật tư từ form
            int materialCount = Integer.parseInt(request.getParameter("materialCount"));
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
                    material.put("materialId", Integer.parseInt(materialIdStr));
                    material.put("usableQuantity", Integer.parseInt(newQuantityStr));
                    material.put("brokenQuantity", Integer.parseInt(brokenQuantityStr));
                    materials.add(material);
                }
            }

            // Kiểm tra nếu không có vật tư nào
            if (materials.isEmpty()) {
                throw new Exception("Không có vật tư nào được chọn!");
            }

            // Gọi phương thức từ DAO để xử lý import
            MaterialDAO materialDAO = new MaterialDAO();
            materialDAO.importMaterials(materials, userID);

            // Thêm thông báo thành công vào session
            session.setAttribute("successMessage", "Nhập kho vật tư thành công!");

            // Chuyển hướng về trang nhập kho vật tư
            response.sendRedirect(request.getContextPath() + "/import-materials");
            return;

        } catch (Exception e) {
            e.printStackTrace();
            // Thêm thông báo lỗi vào session
            session.setAttribute("error", "Lỗi khi nhập kho: " + e.getMessage());
            // Chuyển hướng về trang nhập kho vật tư
            response.sendRedirect(request.getContextPath() + "/import-materials");
            return;
        }
    }

    private void importMaterialsFromDirector(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        try {
            // Lấy requestId từ form
            String requestIdStr = request.getParameter("requestId");
            if (requestIdStr == null) {
                throw new Exception("Không tìm thấy thông tin đơn yêu cầu!");
            }
            int requestId = Integer.parseInt(requestIdStr);

            // Lấy thông tin đơn yêu cầu và vật tư
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
                material.put("materialId", prm.getMaterialID());
                material.put("usableQuantity", prm.getQuantity()); // Số lượng vật tư từ đơn yêu cầu
                material.put("brokenQuantity", 0); // Mặc định số lượng hỏng là 0
                materials.add(material);
            }

            // Lấy thông tin người nhập kho
            User_Role userRole = (User_Role) session.getAttribute("userRole");
            int userID = userRole.getUserID();

            // Gọi phương thức từ DAO để xử lý import
            MaterialDAO materialDAO = new MaterialDAO();
            materialDAO.importMaterialsForPurchase(materials, userID, requestId);

            // Thêm thông báo thành công vào session
            session.setAttribute("successMessage", "Nhập kho vật tư từ đơn #" + requestId + " thành công!");

            // Chuyển hướng về trang danh sách đơn đã duyệt
            response.sendRedirect(request.getContextPath() + "/import-materials?action=showImportFromDirector");
            return;

        } catch (Exception e) {
            e.printStackTrace();
            // Thêm thông báo lỗi vào session
            session.setAttribute("error", "Lỗi khi nhập kho: " + e.getMessage());
            // Chuyển hướng về trang danh sách đơn đã duyệt
            response.sendRedirect(request.getContextPath() + "/import-materials?action=showImportFromDirector");
            return;
        }
    }

    private void showImportRequestDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String purchaseRequestIdStr = request.getParameter("purchaseRequestID");
        if (purchaseRequestIdStr == null) {
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

        // Chuẩn bị danh sách map để truyền sang JSP
        List<Map<String, Object>> requestMaterials = new ArrayList<>();
        MaterialDAO materialDAO = new MaterialDAO();
        UnitDAO unitDAO = new UnitDAO();
        UserDAO userDao = new UserDAO();

        for (PurchaseRequestMaterials prm : prmList) {
            Map<String, Object> materialDetailMap = new HashMap<>();
            materialDetailMap.put("materialID", prm.getMaterialID());
            materialDetailMap.put("quantity", prm.getQuantity());

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

            requestMaterials.add(materialDetailMap);
        }
        
        request.setAttribute("requestId", purchaseRequestID);
        request.setAttribute("purchaseRequest", purchaseRequest);
        request.setAttribute("requestMaterials", requestMaterials);
        request.setAttribute("userDao", userDao); // Để lấy tên người dùng trong JSP
        
        request.getRequestDispatcher("/import-materials/import-materials-from-director-detail.jsp").forward(request, response);
    }
}
