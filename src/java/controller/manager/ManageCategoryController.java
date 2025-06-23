package controller.manager;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.Category;
import model.SubCategory;
import dao.CategoryDAO;
import dao.SubCategoryDAO;

// Định nghĩa URL mà servlet này sẽ xử lý
@WebServlet("/manage-category")
public class ManageCategoryController extends HttpServlet {

    // Khai báo đối tượng DAO để làm việc với danh mục và danh mục con
    private CategoryDAO categoryDAO;
    private SubCategoryDAO subcategoryDAO;

    // Hàm khởi tạo servlet, được gọi khi servlet bắt đầu chạy
    @Override
    public void init() throws ServletException {
        // Khởi tạo các đối tượng DAO để sử dụng
        categoryDAO = new CategoryDAO();
        subcategoryDAO = new SubCategoryDAO();
    }

    // Xử lý các yêu cầu GET (hiển thị danh sách, form thêm, chi tiết, tìm kiếm)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Lấy tham số "action" từ URL để biết người dùng muốn làm gì
        String action = request.getParameter("action");
        
        // Lấy session để lưu trữ hoặc lấy thông báo
        HttpSession session = request.getSession();
        String successMessage = (String) session.getAttribute("successMessage");
        String errorMessage = (String) session.getAttribute("errorMessage");
        
        // Nếu có thông báo thành công, lưu vào request và xóa khỏi session
        if (successMessage != null) {
            request.setAttribute("successMessage", successMessage);
            session.removeAttribute("successMessage");
        }
        
        // Nếu có thông báo lỗi, lưu vào request và xóa khỏi session
        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
            session.removeAttribute("errorMessage");
        }
        
        // Nếu không có hành động hoặc hành động là "list", hiển thị danh sách danh mục
        if (action == null || action.equals("list")) {
            // Lấy tất cả danh mục và danh mục con từ cơ sở dữ liệu
            List<Category> categories = categoryDAO.getAllCategories();
            List<Object[]> subcategoriesWithCategoryInfo = subcategoryDAO.getSubcategoriesWithCategoryInfo();
            
            // Lưu dữ liệu vào request để gửi đến trang JSP
            request.setAttribute("categories", categories);
            request.setAttribute("subcategoriesWithInfo", subcategoriesWithCategoryInfo);
            request.setAttribute("totalCategories", categoryDAO.getTotalCategories());
            request.setAttribute("totalSubcategories", subcategoryDAO.getTotalSubcategories());
            
            // Chuyển đến trang JSP hiển thị danh sách danh mục
            request.getRequestDispatcher("/danhmucvattu/danh-muc-list.jsp").forward(request, response);
            
        } else if (action.equals("addForm")) {
            // Hiển thị form để thêm danh mục mới
            List<Category> categories = categoryDAO.getAllCategories();
            request.setAttribute("categories", categories);
            // Chuyển đến trang JSP chứa form thêm danh mục
            request.getRequestDispatcher("/danhmucvattu/add-category.jsp").forward(request, response);
            
        } else if (action.equals("getCategoryById")) {
            // Lấy thông tin chi tiết của một danh mục theo ID
            String categoryIdStr = request.getParameter("categoryId");
            if (categoryIdStr != null) {
                try {
                    // Chuyển ID từ chuỗi sang số nguyên
                    int categoryId = Integer.parseInt(categoryIdStr);
                    // Lấy danh mục và danh mục con liên quan
                    Category category = categoryDAO.getCategoryByID(categoryId);
                    List<SubCategory> subcategories = subcategoryDAO.getSubcategoriesByCategoryID(categoryId);
                    
                    // Lưu dữ liệu vào request để gửi đến JSP
                    request.setAttribute("selectedCategory", category);
                    request.setAttribute("subcategories", subcategories);
                    // Chuyển đến trang JSP hiển thị chi tiết danh mục
                    request.getRequestDispatcher("/danhmucvattu/category-detail.jsp").forward(request, response);
                } catch (NumberFormatException e) {
                    // Nếu ID không hợp lệ, chuyển về trang danh sách
                    response.sendRedirect(request.getContextPath() + "/manage-category");
                }
            }
            
        } else if (action.equals("search")) {
            // Xử lý tìm kiếm danh mục theo tên
            String searchTerm = request.getParameter("searchTerm");
            List<Category> searchResults;
            List<Object[]> subcategoriesWithCategoryInfo = subcategoryDAO.getSubcategoriesWithCategoryInfo();
            
            // Nếu có từ khóa tìm kiếm, tìm theo tên, nếu không lấy tất cả danh mục
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                searchResults = categoryDAO.searchCategoriesByName(searchTerm.trim());
            } else {
                searchResults = categoryDAO.getAllCategories();
            }
            
            // Lưu kết quả tìm kiếm và các thông tin khác vào request
            request.setAttribute("categories", searchResults);
            request.setAttribute("subcategoriesWithInfo", subcategoriesWithCategoryInfo);
            request.setAttribute("totalCategories", categoryDAO.getTotalCategories());
            request.setAttribute("totalSubcategories", subcategoryDAO.getTotalSubcategories());
            request.setAttribute("searchTerm", searchTerm);
            // Chuyển đến trang JSP hiển thị danh sách danh mục
            request.getRequestDispatcher("/danhmucvattu/danh-muc-list.jsp").forward(request, response);
        }
    }

    // Xử lý các yêu cầu POST (thêm, sửa, xóa danh mục hoặc danh mục con)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Đặt mã hóa UTF-8 để hỗ trợ tiếng Việt
        request.setCharacterEncoding("UTF-8");
        // Lấy tham số "action" từ yêu cầu
        String action = request.getParameter("action");
        
        // Nếu không có hành động, chuyển về trang danh sách
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/manage-category");
            return;
        }
        
        // Xử lý các hành động khác nhau
        switch (action) {
            case "addCategory":
                handleAddCategory(request, response); // Thêm danh mục
                break;
            case "updateCategory":
                handleUpdateCategory(request, response); // Cập nhật danh mục
                break;
            case "deleteCategory":
                handleDeleteCategory(request, response); // Xóa danh mục
                break;
            case "addSubcategory":
                handleAddSubcategory(request, response); // Thêm danh mục con
                break;
            case "updateSubcategory":
                handleUpdateSubcategory(request, response); // Cập nhật danh mục con
                break;
            case "deleteSubcategory":
                handleDeleteSubcategory(request, response); // Xóa danh mục con
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/manage-category"); // Mặc định chuyển về danh sách
                break;
        }
    }
    
    // Xử lý thêm danh mục mới
    private void handleAddCategory(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        String categoryName = request.getParameter("categoryName");
        
        // Kiểm tra tên danh mục có hợp lệ không
        if (categoryName != null && !categoryName.trim().isEmpty()) {
            // Kiểm tra xem tên danh mục đã tồn tại chưa
            if (categoryDAO.categoryNameExists(categoryName.trim())) {
                session.setAttribute("errorMessage", "Tên danh mục đã tồn tại!");
            } else {
                // Tạo danh mục mới và thêm vào cơ sở dữ liệu
                Category newCategory = new Category(0, categoryName.trim());
                boolean success = categoryDAO.insertCategory(newCategory);
                
                // Thông báo kết quả
                if (success) {
                    session.setAttribute("successMessage", "Thêm danh mục thành công!");
                } else {
                    session.setAttribute("errorMessage", "Có lỗi xảy ra khi thêm danh mục!");
                }
            }
        } else {
            session.setAttribute("errorMessage", "Tên danh mục không được để trống!");
        }
        
        // Chuyển về trang danh sách
        response.sendRedirect(request.getContextPath() + "/manage-category");
    }
    
    // Xử lý cập nhật danh mục
    private void handleUpdateCategory(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        String categoryIdStr = request.getParameter("categoryId");
        String categoryName = request.getParameter("categoryName");
        
        // Kiểm tra thông tin đầu vào
        if (categoryIdStr != null && categoryName != null && !categoryName.trim().isEmpty()) {
            try {
                // Chuyển ID từ chuỗi sang số nguyên
                int categoryId = Integer.parseInt(categoryIdStr);
                // Tạo đối tượng danh mục và cập nhật
                Category category = new Category(categoryId, categoryName.trim());
                boolean success = categoryDAO.updateCategory(category);
                
                // Thông báo kết quả
                if (success) {
                    session.setAttribute("successMessage", "Cập nhật danh mục thành công!");
                } else {
                    session.setAttribute("errorMessage", "Có lỗi xảy ra khi cập nhật danh mục!");
                }
            } catch (NumberFormatException e) {
                session.setAttribute("errorMessage", "ID danh mục không hợp lệ!");
            }
        } else {
            session.setAttribute("errorMessage", "Thông tin danh mục không hợp lệ!");
        }
        
        // Chuyển về trang danh sách
        response.sendRedirect(request.getContextPath() + "/manage-category");
    }
    
    // Xử lý xóa danh mục
    private void handleDeleteCategory(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        String categoryIdStr = request.getParameter("categoryId");
        
        // Kiểm tra ID danh mục
        if (categoryIdStr != null) {
            try {
                // Chuyển ID từ chuỗi sang số nguyên
                int categoryId = Integer.parseInt(categoryIdStr);
                
                // Kiểm tra xem danh mục có danh mục con không
                int subcategoryCount = subcategoryDAO.getSubcategoryCountByCategoryID(categoryId);
                if (subcategoryCount > 0) {
                    session.setAttribute("errorMessage", "Không thể xóa danh mục có chứa danh mục con!");
                } else {
                    // Xóa danh mục
                    boolean success = categoryDAO.deleteCategory(categoryId);
                    
                    // Thông báo kết quả
                    if (success) {
                        session.setAttribute("successMessage", "Xóa danh mục thành công!");
                    } else {
                        session.setAttribute("errorMessage", "Có lỗi xảy ra khi xóa danh mục!");
                    }
                }
            } catch (NumberFormatException e) {
                session.setAttribute("errorMessage", "ID danh mục không hợp lệ!");
            }
        }
        
        // Chuyển về trang danh sách
        response.sendRedirect(request.getContextPath() + "/manage-category");
    }
    
    // Xử lý thêm danh mục con
    private void handleAddSubcategory(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        String categoryIdStr = request.getParameter("categoryId");
        String subcategoryName = request.getParameter("subcategoryName");
        
        // Kiểm tra thông tin đầu vào
        if (categoryIdStr != null && subcategoryName != null && !subcategoryName.trim().isEmpty()) {
            try {
                // Chuyển ID danh mục cha từ chuỗi sang số nguyên
                int categoryId = Integer.parseInt(categoryIdStr);
                
                // Kiểm tra tên danh mục con đã tồn tại trong danh mục cha chưa
                if (subcategoryDAO.subcategoryNameExistsInCategory(categoryId, subcategoryName.trim())) {
                    session.setAttribute("errorMessage", "Tên danh mục con đã tồn tại trong danh mục này!");
                } else {
                    // Tạo danh mục con mới và thêm vào cơ sở dữ liệu
                    SubCategory newSubcategory = new SubCategory(categoryId, subcategoryName.trim());
                    boolean success = subcategoryDAO.insertSubcategory(newSubcategory);
                    
                    // Thông báo kết quả
                    if (success) {
                        session.setAttribute("successMessage", "Thêm danh mục con thành công!");
                    } else {
                        session.setAttribute("errorMessage", "Có lỗi xảy ra khi thêm danh mục con!");
                    }
                }
            } catch (NumberFormatException e) {
                session.setAttribute("errorMessage", "ID danh mục không hợp lệ!");
            }
        } else {
            session.setAttribute("errorMessage", "Thông tin danh mục con không hợp lệ!");
        }
        
        // Chuyển về trang danh sách
        response.sendRedirect(request.getContextPath() + "/manage-category");
    }
    
    // Xử lý cập nhật danh mục con
    private void handleUpdateSubcategory(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        String subcategoryIdStr = request.getParameter("subcategoryId");
        String categoryIdStr = request.getParameter("categoryId");
        String subcategoryName = request.getParameter("subcategoryName");
        
        // Kiểm tra thông tin đầu vào
        if (subcategoryIdStr != null && categoryIdStr != null && subcategoryName != null && !subcategoryName.trim().isEmpty()) {
            try {
                // Chuyển ID từ chuỗi sang số nguyên
                int subcategoryId = Integer.parseInt(subcategoryIdStr);
                int categoryId = Integer.parseInt(categoryIdStr);
                
                // Tạo đối tượng danh mục con và cập nhật
                SubCategory subcategory = new SubCategory(subcategoryId, categoryId, subcategoryName.trim());
                boolean success = subcategoryDAO.updateSubcategory(subcategory);
                
                // Thông báo kết quả
                if (success) {
                    session.setAttribute("successMessage", "Cập nhật danh mục con thành công!");
                } else {
                    session.setAttribute("errorMessage", "Có lỗi xảy ra khi cập nhật danh mục con!");
                }
            } catch (NumberFormatException e) {
                session.setAttribute("errorMessage", "ID không hợp lệ!");
            }
        } else {
            session.setAttribute("errorMessage", "Thông tin danh mục con không hợp lệ!");
        }
        
        // Chuyển về trang danh sách
        response.sendRedirect(request.getContextPath() + "/manage-category");
    }
    
    // Xử lý xóa danh mục con
    private void handleDeleteSubcategory(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        String subcategoryIdStr = request.getParameter("subcategoryId");
        
        // Kiểm tra ID danh mục con
        if (subcategoryIdStr != null) {
            try {
                // Chuyển ID từ chuỗi sang số nguyên
                int subcategoryId = Integer.parseInt(subcategoryIdStr);
                // Xóa danh mục con
                boolean success = subcategoryDAO.deleteSubcategory(subcategoryId);
                
                // Thông báo kết quả
                if (success) {
                    session.setAttribute("successMessage", "Xóa danh mục con thành công!");
                } else {
                    session.setAttribute("errorMessage", "Có lỗi xảy ra khi xóa danh mục con!");
                }
            } catch (NumberFormatException e) {
                session.setAttribute("errorMessage", "ID danh mục con không hợp lệ!");
            }
        }
        
        // Chuyển về trang danh sách
        response.sendRedirect(request.getContextPath() + "/manage-category");
    }
}