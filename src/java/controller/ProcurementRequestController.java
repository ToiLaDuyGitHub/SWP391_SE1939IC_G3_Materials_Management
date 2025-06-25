package controller;

import dao.CategoryDAO;
import dao.MaterialDAO;
import dao.PurchaseRequestDAO;
import dao.PurchaseRequestMaterialsDAO;
import dao.SubCategoryDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Category;
import model.PurchaseRequest;
import model.PurchaseRequestMaterials;
import model.SubCategory;
import model.User;
import model.dto.SearchMaterialDTO;

public class ProcurementRequestController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String service = request.getParameter("service");

            // Xử lý tìm kiếm
            if ("searchByKeywords".equals(service)) {
                String keywords = request.getParameter("keywords");
                String categoryFilter = request.getParameter("categoryFilter");
                String subcategoryFilter = request.getParameter("subcategoryFilter");

                MaterialDAO materialDAO = new MaterialDAO();
                List<SearchMaterialDTO> materials = materialDAO.searchMaterialsForPurchase(keywords, categoryFilter, subcategoryFilter);
                request.setAttribute("searchResults", materials);

                if (materials == null || materials.isEmpty()) {
                    request.setAttribute("notFoundMaterial", "Không tìm thấy vật tư phù hợp với từ khóa tìm kiếm.");
                }
            }
            if ("resetForm".equals(service)) {
                request.getSession().removeAttribute("selectedMaterials");
                response.sendRedirect(request.getContextPath() + "/Create-Request/create-procurement.jsp");
                return;
            }
            if ("getSubcategories".equals(service)) {
                String categoryID = request.getParameter("categoryID");
                try {
                    SubCategoryDAO subcategoryDAO = new SubCategoryDAO();
                    List<SubCategory> subcategories = subcategoryDAO.getSubcategoriesByCategoryID(Integer.parseInt(categoryID));

                    // Chuyển đổi sang JSON
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");

                    StringBuilder json = new StringBuilder("[");
                    for (SubCategory sub : subcategories) {
                        json.append("{")
                                .append("\"subcategoryID\":").append(sub.getSubcategoryID()).append(",")
                                .append("\"subcategoryName\":\"").append(sub.getSubcategoryName()).append("\"")
                                .append("},");
                    }
                    if (!subcategories.isEmpty()) {
                        json.deleteCharAt(json.length() - 1);
                    }
                    json.append("]");

                    response.getWriter().write(json.toString());
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Load categories và subcategories
            CategoryDAO categoryDAO = new CategoryDAO();
            SubCategoryDAO subcategoryDAO = new SubCategoryDAO();
            List<Category> categories = categoryDAO.getAllCategories();
            List<SubCategory> subcategories = subcategoryDAO.getAllSubcategories();
            request.setAttribute("categories", categories);
            request.setAttribute("subcategories", subcategories);

            // Load selected materials từ session
            List<SelectedMaterial> selectedMaterials = (List<SelectedMaterial>) request.getSession().getAttribute("selectedMaterials");
            if (selectedMaterials == null) {
                selectedMaterials = new ArrayList<>();
                request.getSession().setAttribute("selectedMaterials", selectedMaterials);
            }
            request.setAttribute("selectedMaterials", selectedMaterials);

            // Forward đến JSP
            request.getRequestDispatcher("/Create-Request/create-procurement.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi hệ thống: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String service = request.getParameter("service");

            if ("addMaterial".equals(service)) {
                handleAddMaterial(request, response);
            } else if ("removeMaterial".equals(service)) {
                handleRemoveMaterial(request, response);
            } else if ("resetForm".equals(service)) {
                handleResetForm(request, response);
            } else if ("createRequest".equals(service)) {
                handleCreateRequest(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/procurement-request");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void handleAddMaterial(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String materialIDStr = request.getParameter("materialID");
        if (materialIDStr == null || materialIDStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/procurement-request?error=InvalidMaterialID");
            return;
        }

        int materialID;
        try {
            materialID = Integer.parseInt(materialIDStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/procurement-request?error=InvalidMaterialID");
            return;
        }

        List<SelectedMaterial> selectedMaterials = (List<SelectedMaterial>) request.getSession().getAttribute("selectedMaterials");
        if (selectedMaterials == null) {
            selectedMaterials = new ArrayList<>();
        }

        boolean exists = selectedMaterials.stream().anyMatch(m -> m.getMaterialID() == materialID);
        if (!exists) {
            MaterialDAO materialDAO = new MaterialDAO();
            try {
                SearchMaterialDTO material = materialDAO.getMaterialByID(materialID);
                if (material != null) {
                    SelectedMaterial selected = new SelectedMaterial(
                            materialID,
                            material.getMaterialName(),
                            material.getUnit(),
                            material.getSupplierName(),
                            1
                    );
                    selectedMaterials.add(selected);
                    request.getSession().setAttribute("selectedMaterials", selectedMaterials);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/procurement-request?error=DatabaseError");
                return;
            }
        }
        if (exists) {
            String redirectURL = request.getContextPath() + "/procurement-request?error=materialExists";
            String keywords = request.getParameter("keywords");
            String categoryFilter = request.getParameter("categoryFilter");
            if (keywords != null || categoryFilter != null) {
                redirectURL += "&";
                if (keywords != null) {
                    redirectURL += "keywords=" + URLEncoder.encode(keywords, "UTF-8");
                }
                if (categoryFilter != null) {
                    redirectURL += "&categoryFilter=" + categoryFilter;
                }
            }

            response.sendRedirect(redirectURL);
            return;
        }
        response.sendRedirect(request.getContextPath() + "/procurement-request");
    }

    private void handleRemoveMaterial(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String materialIDStr = request.getParameter("materialID");
        if (materialIDStr == null || materialIDStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/procurement-request?error=InvalidMaterialID");
            return;
        }

        int materialID;
        try {
            materialID = Integer.parseInt(materialIDStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/procurement-request?error=InvalidMaterialID");
            return;
        }
        List<SelectedMaterial> selectedMaterials = (List<SelectedMaterial>) request.getSession().getAttribute("selectedMaterials");
        if (selectedMaterials != null) {
            selectedMaterials.removeIf(m -> m.getMaterialID() == materialID);
            request.getSession().setAttribute("selectedMaterials", selectedMaterials);
        }

        // Redirect giữ nguyên tham số tìm kiếm
        String keywords = request.getParameter("keywords");
        String categoryFilter = request.getParameter("categoryFilter");
        String redirectURL = request.getContextPath() + "/procurement-request";

        if (keywords != null || categoryFilter != null) {
            redirectURL += "?";
            if (keywords != null) {
                redirectURL += "keywords=" + URLEncoder.encode(keywords, "UTF-8");
            }
            if (categoryFilter != null) {
                redirectURL += "&categoryFilter=" + categoryFilter;
            }
        }

        response.sendRedirect(redirectURL);
    }

    private void handleResetForm(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.getSession().removeAttribute("selectedMaterials");
        response.sendRedirect(request.getContextPath() + "/procurement-request");
    }

    private void handleCreateRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<SelectedMaterial> selectedMaterials = (List<SelectedMaterial>) request.getSession().getAttribute("selectedMaterials");
        if (selectedMaterials == null || selectedMaterials.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/procurement-request?error=noMaterials");
            return;
        }

        List<PurchaseRequestMaterials> materials = new ArrayList<>();
        for (SelectedMaterial sm : selectedMaterials) {
            String quantityParam = request.getParameter("quantity_" + sm.getMaterialID());
            int quantity = 1; // default value

            if (quantityParam != null && !quantityParam.trim().isEmpty()) {
                try {
                    quantity = Integer.parseInt(quantityParam);
                    if (quantity <= 0) {
                        quantity = 1;
                    }
                } catch (NumberFormatException e) {
                }
            }

            PurchaseRequestMaterials prm = new PurchaseRequestMaterials();
            prm.setMaterialID(sm.getMaterialID());
            prm.setQuantity(quantity);
            materials.add(prm);
        }

        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        PurchaseRequest pr = new PurchaseRequest();
        pr.setCreatedByID(user.getUserID());
        pr.setNote(request.getParameter("generalNote"));
        pr.setStatus((byte) 0);

        try {
            PurchaseRequestDAO prDao = new PurchaseRequestDAO();
            int purchaseRequestId = prDao.createPurchaseRequest(pr);

            PurchaseRequestMaterialsDAO prmDao = new PurchaseRequestMaterialsDAO();
            prmDao.createPurchaseRequestMaterials(purchaseRequestId, materials);

            request.getSession().removeAttribute("selectedMaterials");
            response.sendRedirect(request.getContextPath() + "/procurement-request?success=true");

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/procurement-request?error=DatabaseError");
        }
    }

    public static class SelectedMaterial {

        private int materialID;
        private String materialName;
        private String unit;
        private String supplierName;
        private int quantity;

        public SelectedMaterial(int materialID, String materialName, String unit, String supplierName, int quantity) {
            this.materialID = materialID;
            this.materialName = materialName;
            this.unit = unit;
            this.supplierName = supplierName;
            this.quantity = quantity;
        }

        // Getters
        public int getMaterialID() {
            return materialID;
        }

        public String getMaterialIDAsString() {
            return String.valueOf(materialID);
        }

        public String getMaterialName() {
            return materialName;
        }

        public String getUnit() {
            return unit;
        }

        public String getSupplierName() {
            return supplierName;
        }

        public int getQuantity() {
            return quantity;
        }

        // Setters
        public void setMaterialID(int materialID) {
            this.materialID = materialID;
        }

        public void setMaterialName(String materialName) {
            this.materialName = materialName;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public void setSupplierName(String supplierName) {
            this.supplierName = supplierName;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}
