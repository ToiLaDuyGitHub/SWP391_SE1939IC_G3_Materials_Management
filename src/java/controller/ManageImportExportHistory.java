package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.ImportHistory;
import model.ExportHistory;
import model.User;
import model.Material;
import dao.ImportHistoryDAO;
import dao.ExportHistoryDAO;
import dao.UserDAO;
import dao.MaterialDAO;
import dao.UnitDAO;

// Định nghĩa servlet với tên và URL pattern
@WebServlet(name = "ManageImportAndExportHistory", urlPatterns = {"/manage-history"})
public class ManageImportExportHistory extends HttpServlet {
    
    // Xử lý yêu cầu GET
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Lấy tham số action từ request
        String action = request.getParameter("action");
        
        // Nếu không có action, hiển thị danh sách lịch sử nhập/xuất
        if (action == null) {
            // Khởi tạo các DAO để truy xuất dữ liệu
            ImportHistoryDAO importDAO = new ImportHistoryDAO();
            ExportHistoryDAO exportDAO = new ExportHistoryDAO();
            UserDAO userDAO = new UserDAO();
            MaterialDAO materialDAO = new MaterialDAO();
            
            // Lấy danh sách lịch sử nhập/xuất từ database
            List<ImportHistory> importList = importDAO.getImportHistory();
            List<ExportHistory> exportList = exportDAO.getExportHistory();
            
            // Tạo các danh sách Map để lưu thông tin chi tiết
            List<Map<String, Object>> importHistoryMap = new ArrayList<>();
            List<Map<String, Object>> exportHistoryMap = new ArrayList<>();
            
            // Xử lý lịch sử nhập kho
            for (ImportHistory ih : importList) {
                // Tạo Map để lưu thông tin từng bản ghi lịch sử
                Map<String, Object> map = new HashMap<>();
                // Lấy thông tin người nhập kho
                User user = userDAO.getUserById(ih.getImportedByID());
                // Lấy danh sách ID nguyên liệu từ lịch sử nhập
                List<Integer> materialIds = importDAO.getAllMaterialIdsFromHistory(ih.getImportHistoryID());
                // Tạo danh sách tên nguyên liệu
                List<String> materialNames = new ArrayList<>();
                
                // Lấy tên nguyên liệu từ ID
                for (Integer materialId : materialIds) {
                    Material material = materialDAO.getMaterialById(materialId);
                    if (material != null) {
                        materialNames.add(material.getMaterialName());
                    }
                }
                
                // Thêm thông tin vào Map
                map.put("historyId", ih.getImportHistoryID());
                map.put("date", ih.getImportDate());
                map.put("userName", user.getFirstName() + " " + user.getLastName());
                map.put("materialNames", materialNames);
                importHistoryMap.add(map);
            }
            
            // Xử lý lịch sử xuất kho (tương tự nhập kho)
            for (ExportHistory eh : exportList) {
                Map<String, Object> map = new HashMap<>();
                User user = userDAO.getUserById(eh.getExportedByID());
                List<Integer> materialIds = exportDAO.getAllMaterialIdsFromHistory(eh.getExportHistoryID());
                List<String> materialNames = new ArrayList<>();
                
                for (Integer materialId : materialIds) {
                    Material material = materialDAO.getMaterialById(materialId);
                    if (material != null) {
                        materialNames.add(material.getMaterialName());
                    }
                }
                
                map.put("historyId", eh.getExportHistoryID());
                map.put("date", eh.getExportDate());
                map.put("userName", user.getFirstName() + " " + user.getLastName());
                map.put("materialNames", materialNames);
                exportHistoryMap.add(map);
            }
            
            // Gửi dữ liệu đến JSP để hiển thị
            request.setAttribute("importHistoryList", importHistoryMap);
            request.setAttribute("exportHistoryList", exportHistoryMap);
            request.getRequestDispatcher("history/manage-history.jsp").forward(request, response);
            
        // Nếu action là viewDetail, hiển thị chi tiết lịch sử
        } else if (action.equals("viewDetail")) {
            // Lấy loại lịch sử (import/export) và ID
            String type = request.getParameter("type");
            int historyId = Integer.parseInt(request.getParameter("id"));
            
            // Xử lý chi tiết lịch sử nhập kho
            if (type.equals("import")) {
                ImportHistoryDAO importDAO = new ImportHistoryDAO();
                // Lấy thông tin lịch sử nhập theo ID
                ImportHistory history = importDAO.getImportHistoryById(historyId);
                
                // Lấy thông tin người nhập kho
                UserDAO userDAO = new UserDAO();
                User user = userDAO.getUserById(history.getImportedByID());
                
                // Lấy thông tin nguyên liệu
                List<Integer> materialIds = importDAO.getAllMaterialIdsFromHistory(historyId);
                List<Map<String, Object>> materialsList = new ArrayList<>();
                MaterialDAO materialDAO = new MaterialDAO();
                UnitDAO unitDAO = new UnitDAO();
                
                // Tạo danh sách thông tin chi tiết nguyên liệu
                for (Integer materialId : materialIds) {
                    Map<String, Object> materialMap = new HashMap<>();
                    Material material = materialDAO.getMaterialById(materialId);
                    if (material != null) {
                        materialMap.put("materialId", material.getMaterialID());
                        materialMap.put("materialName", material.getMaterialName());
                        materialMap.put("unit", unitDAO.getMinUnitByMaterialId(materialId));
                        // Lấy số lượng nguyên liệu từ lịch sử
                        materialMap.put("quantity", importDAO.getMaterialQuantity(historyId, materialId));
                    }
                    materialsList.add(materialMap);
                }
                
                // Gửi dữ liệu đến JSP
                request.setAttribute("history", history);
                request.setAttribute("user", user);
                request.setAttribute("materialsList", materialsList);
                request.setAttribute("type", "import");
            
            // Xử lý chi tiết lịch sử xuất kho (tương tự nhập kho)
            } else {
                ExportHistoryDAO exportDAO = new ExportHistoryDAO();
                ExportHistory history = exportDAO.getExportHistoryById(historyId);
                
                UserDAO userDAO = new UserDAO();
                User user = userDAO.getUserById(history.getExportedByID());
                
                List<Integer> materialIds = exportDAO.getAllMaterialIdsFromHistory(historyId);
                List<Map<String, Object>> materialsList = new ArrayList<>();
                MaterialDAO materialDAO = new MaterialDAO();
                UnitDAO unitDAO = new UnitDAO();
                
                for (Integer materialId : materialIds) {
                    Map<String, Object> materialMap = new HashMap<>();
                    Material material = materialDAO.getMaterialById(materialId);
                    if (material != null) {
                        materialMap.put("materialId", material.getMaterialID());
                        materialMap.put("materialName", material.getMaterialName());
                        materialMap.put("unit", unitDAO.getMinUnitByMaterialId(materialId));
                        materialMap.put("quantity", exportDAO.getMaterialQuantity(historyId, materialId));
                    }
                    materialsList.add(materialMap);
                }
                
                request.setAttribute("history", history);
                request.setAttribute("user", user);
                request.setAttribute("materialsList", materialsList);
                request.setAttribute("type", "export");
            }
            
            // Chuyển đến trang chi tiết
            request.getRequestDispatcher("history/history-detail.jsp").forward(request, response);
        }
    }
  
    // Xử lý yêu cầu POST
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Chuyển hướng về trang quản lý lịch sử
        response.sendRedirect("manage-history");
    }
}