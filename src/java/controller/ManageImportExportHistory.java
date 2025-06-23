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

@WebServlet(name = "ManageImportAndExportHistory", urlPatterns = {"/manage-history"})
public class ManageImportExportHistory extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if (action == null) {
            // Get import and export history lists
            ImportHistoryDAO importDAO = new ImportHistoryDAO();
            ExportHistoryDAO exportDAO = new ExportHistoryDAO();
            UserDAO userDAO = new UserDAO();
            MaterialDAO materialDAO = new MaterialDAO();
            
            List<ImportHistory> importList = importDAO.getImportHistory();
            List<ExportHistory> exportList = exportDAO.getExportHistory();
            
            // Create maps to store detailed information
            List<Map<String, Object>> importHistoryMap = new ArrayList<>();
            List<Map<String, Object>> exportHistoryMap = new ArrayList<>();
            
            // Process import history
            for (ImportHistory ih : importList) {
                Map<String, Object> map = new HashMap<>();
                User user = userDAO.getUserById(ih.getImportedByID());
                List<Integer> materialIds = importDAO.getAllMaterialIdsFromHistory(ih.getImportHistoryID());
                List<String> materialNames = new ArrayList<>();
                
                for (Integer materialId : materialIds) {
                    Material material = materialDAO.getMaterialById(materialId);
                    if (material != null) {
                        materialNames.add(material.getMaterialName());
                    }
                }
                
                map.put("historyId", ih.getImportHistoryID());
                map.put("date", ih.getImportDate());
                map.put("userName", user.getFirstName() + " " + user.getLastName());
                map.put("materialNames", materialNames);
                importHistoryMap.add(map);
            }
            
            // Process export history
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
            
            request.setAttribute("importHistoryList", importHistoryMap);
            request.setAttribute("exportHistoryList", exportHistoryMap);
            request.getRequestDispatcher("history/manage-history.jsp").forward(request, response);
            
        } else if (action.equals("viewDetail")) {
            String type = request.getParameter("type");
            int historyId = Integer.parseInt(request.getParameter("id"));
            
            if (type.equals("import")) {
                ImportHistoryDAO importDAO = new ImportHistoryDAO();
                ImportHistory history = importDAO.getImportHistoryById(historyId);
                
                // Get user information
                UserDAO userDAO = new UserDAO();
                User user = userDAO.getUserById(history.getImportedByID());
                
                // Get materials information
                List<Integer> materialIds = importDAO.getAllMaterialIdsFromHistory(historyId);
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
                        // Get quantity from import history materials
                        materialMap.put("quantity", importDAO.getMaterialQuantity(historyId, materialId));
                    }
                    materialsList.add(materialMap);
                }
                
                request.setAttribute("history", history);
                request.setAttribute("user", user);
                request.setAttribute("materialsList", materialsList);
                request.setAttribute("type", "import");
            } else {
                ExportHistoryDAO exportDAO = new ExportHistoryDAO();
                ExportHistory history = exportDAO.getExportHistoryById(historyId);
                
                // Get user information
                UserDAO userDAO = new UserDAO();
                User user = userDAO.getUserById(history.getExportedByID());
                
                // Get materials information
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
                        // Get quantity from export history materials
                        materialMap.put("quantity", exportDAO.getMaterialQuantity(historyId, materialId));
                    }
                    materialsList.add(materialMap);
                }
                
                request.setAttribute("history", history);
                request.setAttribute("user", user);
                request.setAttribute("materialsList", materialsList);
                request.setAttribute("type", "export");
            }
            
            request.getRequestDispatcher("history/history-detail.jsp").forward(request, response);
        }
    }
  
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("manage-history");
    }
}
