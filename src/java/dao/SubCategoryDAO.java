/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import model.SubCategory;
import model.Category;
import util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ToiLaDuyGitHub
 */
public class SubCategoryDAO extends DBUtil {

    // Helper method để tạo SubCategory object từ ResultSet
    private SubCategory getFromResultSet(ResultSet rs) throws SQLException {
        int subcategoryID = rs.getInt("SubcategoryID");
        int categoryID = rs.getInt("CategoryID");
        String subcategoryName = rs.getString("SubcategoryName");
        return new SubCategory(subcategoryID, categoryID, subcategoryName);
    }

    // Lấy tất cả subcategories
    public List<SubCategory> getAllSubcategories() {
        List<SubCategory> subcategories = new ArrayList<>();
        String sql = "SELECT SubcategoryID, CategoryID, SubcategoryName FROM subcategories";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                subcategories.add(getFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subcategories;
    }

    // Lấy subcategory theo ID
    public SubCategory getSubcategoryByID(int subcategoryID) {
        String sql = "SELECT SubcategoryID, CategoryID, SubcategoryName FROM subcategories WHERE SubcategoryID = ?";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, subcategoryID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return getFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Lấy tất cả subcategories theo CategoryID
    public List<SubCategory> getSubcategoriesByCategoryID(int categoryID) {
        List<SubCategory> subcategories = new ArrayList<>();
        String sql = "SELECT SubcategoryID, CategoryID, SubcategoryName FROM subcategories WHERE CategoryID = ?";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoryID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                subcategories.add(getFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subcategories;
    }

    // Thêm subcategory mới
    public boolean insertSubcategory(SubCategory subcategory) {
        String sql = "INSERT INTO subcategories (CategoryID, SubcategoryName) VALUES (?, ?)";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, subcategory.getCategoryID());
            stmt.setString(2, subcategory.getSubcategoryName());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Cập nhật subcategory
    public boolean updateSubcategory(SubCategory subcategory) {
        String sql = "UPDATE subcategories SET CategoryID = ?, SubcategoryName = ? WHERE SubcategoryID = ?";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, subcategory.getCategoryID());
            stmt.setString(2, subcategory.getSubcategoryName());
            stmt.setInt(3, subcategory.getSubcategoryID());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Xóa subcategory
    public boolean deleteSubcategory(int subcategoryID) {
        String sql = "DELETE FROM subcategories WHERE SubcategoryID = ?";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, subcategoryID);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Xóa tất cả subcategories của một category
    public boolean deleteSubcategoriesByCategoryID(int categoryID) {
        String sql = "DELETE FROM subcategories WHERE CategoryID = ?";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoryID);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Kiểm tra subcategory có tồn tại không
    public boolean subcategoryExists(int subcategoryID) {
        String sql = "SELECT COUNT(*) FROM subcategories WHERE SubcategoryID = ?";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, subcategoryID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Kiểm tra tên subcategory đã tồn tại trong cùng category chưa
    public boolean subcategoryNameExistsInCategory(int categoryID, String subcategoryName) {
        String sql = "SELECT COUNT(*) FROM subcategories WHERE CategoryID = ? AND SubcategoryName = ?";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoryID);
            stmt.setString(2, subcategoryName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Lấy tổng số subcategories
    public int getTotalSubcategories() {
        String sql = "SELECT COUNT(*) FROM subcategories";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Lấy số lượng subcategories theo CategoryID
    public int getSubcategoryCountByCategoryID(int categoryID) {
        String sql = "SELECT COUNT(*) FROM subcategories WHERE CategoryID = ?";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoryID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Tìm kiếm subcategories theo tên
    public List<SubCategory> searchSubcategoriesByName(String searchTerm) {
        List<SubCategory> subcategories = new ArrayList<>();
        String sql = "SELECT SubcategoryID, CategoryID, SubcategoryName FROM subcategories WHERE SubcategoryName LIKE ?";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + searchTerm + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                subcategories.add(getFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subcategories;
    }

    // Lấy subcategories với thông tin category (JOIN)
    public List<Object[]> getSubcategoriesWithCategoryInfo() {
        List<Object[]> results = new ArrayList<>();
        String sql = "SELECT s.SubcategoryID, s.CategoryID, s.SubcategoryName, c.CategoryName "
                + "FROM subcategories s "
                + "INNER JOIN categories c ON s.CategoryID = c.CategoryID "
                + "ORDER BY c.CategoryName, s.SubcategoryName";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                SubCategory subcategory = new SubCategory(
                        rs.getInt("SubcategoryID"),
                        rs.getInt("CategoryID"),
                        rs.getString("SubcategoryName")
                );
                Category category = new Category(
                        rs.getInt("CategoryID"),
                        rs.getString("CategoryName")
                );
                results.add(new Object[]{subcategory, category});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    // Lấy subcategories với phân trang
    public List<SubCategory> getSubcategoriesWithPagination(int offset, int limit) {
        List<SubCategory> subcategories = new ArrayList<>();
        String sql = "SELECT SubcategoryID, CategoryID, SubcategoryName FROM subcategories LIMIT ? OFFSET ?";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                subcategories.add(getFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subcategories;
    }

    // === Hàm main để test SubCategoryDAO ===
    public static void main(String[] args) {
        SubCategoryDAO subcategoryDAO = new SubCategoryDAO();
        CategoryDAO categoryDAO = new CategoryDAO();
        System.out.println("=== BẮT ĐẦU TEST SUBCATEGORY DAO ===\n");

        // Test 1: Lấy tổng số subcategories ban đầu
        System.out.println("1. Kiểm tra tổng số subcategories ban đầu:");
        int totalSubcategories = subcategoryDAO.getTotalSubcategories();
        System.out.println("Tổng số subcategories: " + totalSubcategories);
        System.out.println();

        // Test 2: Lấy tất cả categories để có CategoryID cho test
        System.out.println("2. Lấy danh sách categories có sẵn:");
        List<Category> categories = categoryDAO.getAllCategories();
        if (categories.isEmpty()) {
            System.out.println("Không có category nào! Cần tạo category trước.");
            // Tạo một category test
            Category testCategory = new Category(0, "Test Category");
            categoryDAO.insertCategory(testCategory);
            categories = categoryDAO.getAllCategories();
        }

        for (Category category : categories) {
            System.out.println("Category ID: " + category.getCategoryID() + " - Name: " + category.getCategoryName());
        }
        System.out.println();

        // Test 3: Lấy tất cả subcategories
        System.out.println("3. Lấy tất cả subcategories:");
        List<SubCategory> allSubcategories = subcategoryDAO.getAllSubcategories();
        if (allSubcategories.isEmpty()) {
            System.out.println("Chưa có subcategory nào trong database.");
        } else {
            for (SubCategory subcategory : allSubcategories) {
                System.out.println(subcategory.toString());
            }
        }
        System.out.println();

        // Test 4: Thêm subcategories mới
        System.out.println("4. Thêm subcategories mới:");
        if (!categories.isEmpty()) {
            int testCategoryID = categories.get(0).getCategoryID();

            SubCategory newSub1 = new SubCategory(testCategoryID, "Laptop");
            SubCategory newSub2 = new SubCategory(testCategoryID, "Desktop");
            SubCategory newSub3 = new SubCategory(testCategoryID, "Monitor");

            boolean insert1 = subcategoryDAO.insertSubcategory(newSub1);
            boolean insert2 = subcategoryDAO.insertSubcategory(newSub2);
            boolean insert3 = subcategoryDAO.insertSubcategory(newSub3);

            System.out.println("Thêm 'Laptop': " + (insert1 ? "Thành công" : "Thất bại"));
            System.out.println("Thêm 'Desktop': " + (insert2 ? "Thành công" : "Thất bại"));
            System.out.println("Thêm 'Monitor': " + (insert3 ? "Thành công" : "Thất bại"));
        }
        System.out.println();

        // Test 5: Lấy subcategories theo CategoryID
        System.out.println("5. Lấy subcategories theo CategoryID:");
        if (!categories.isEmpty()) {
            int testCategoryID = categories.get(0).getCategoryID();
            List<SubCategory> subcategoriesByCategory = subcategoryDAO.getSubcategoriesByCategoryID(testCategoryID);
            System.out.println("Subcategories cho Category ID " + testCategoryID + ":");
            for (SubCategory subcategory : subcategoriesByCategory) {
                System.out.println("  " + subcategory.toString());
            }
        }
        System.out.println();

        // Test 6: Tìm kiếm subcategories
        System.out.println("6. Test tìm kiếm subcategories:");
        List<SubCategory> searchResults = subcategoryDAO.searchSubcategoriesByName("top");
        System.out.println("Kết quả tìm kiếm 'top':");
        for (SubCategory subcategory : searchResults) {
            System.out.println("  " + subcategory.toString());
        }
        System.out.println();

        // Test 7: Lấy subcategories với thông tin category
        System.out.println("7. Test lấy subcategories với thông tin category:");
        List<Object[]> subcategoriesWithInfo = subcategoryDAO.getSubcategoriesWithCategoryInfo();
        for (Object[] info : subcategoriesWithInfo) {
            SubCategory sub = (SubCategory) info[0];
            Category cat = (Category) info[1];
            System.out.println("Subcategory: " + sub.getSubcategoryName() + " - Category: " + cat.getCategoryName());
        }
        System.out.println();

        // Test 8: Test các utility methods
        System.out.println("8. Test utility methods:");
        allSubcategories = subcategoryDAO.getAllSubcategories();
        if (!allSubcategories.isEmpty()) {
            SubCategory testSub = allSubcategories.get(0);
            System.out.println("Subcategory ID " + testSub.getSubcategoryID() + " tồn tại: "
                    + subcategoryDAO.subcategoryExists(testSub.getSubcategoryID()));
            System.out.println("Tên '" + testSub.getSubcategoryName() + "' tồn tại trong category "
                    + testSub.getCategoryID() + ": "
                    + subcategoryDAO.subcategoryNameExistsInCategory(testSub.getCategoryID(), testSub.getSubcategoryName()));
        }
        System.out.println();

        // Test 9: Test cập nhật
        System.out.println("9. Test cập nhật subcategory:");
        allSubcategories = subcategoryDAO.getAllSubcategories();
        if (!allSubcategories.isEmpty()) {
            SubCategory subcategoryToUpdate = allSubcategories.get(0);
            String oldName = subcategoryToUpdate.getSubcategoryName();
            subcategoryToUpdate.setSubcategoryName(oldName + " (Updated)");

            boolean updateResult = subcategoryDAO.updateSubcategory(subcategoryToUpdate);
            System.out.println("Cập nhật subcategory ID " + subcategoryToUpdate.getSubcategoryID() + ": "
                    + (updateResult ? "Thành công" : "Thất bại"));
        }
        System.out.println();

        // Test 10: Test xóa
        System.out.println("10. Test xóa subcategory:");
        allSubcategories = subcategoryDAO.getAllSubcategories();
        if (!allSubcategories.isEmpty()) {
            SubCategory subcategoryToDelete = allSubcategories.get(allSubcategories.size() - 1);
            int deleteID = subcategoryToDelete.getSubcategoryID();

            boolean deleteResult = subcategoryDAO.deleteSubcategory(deleteID);
            System.out.println("Xóa subcategory ID " + deleteID + ": "
                    + (deleteResult ? "Thành công" : "Thất bại"));
        }
        System.out.println();

        // Test 11: Kiểm tra kết quả cuối
        System.out.println("11. Kết quả cuối:");
        totalSubcategories = subcategoryDAO.getTotalSubcategories();
        System.out.println("Tổng số subcategories cuối: " + totalSubcategories);

        allSubcategories = subcategoryDAO.getAllSubcategories();
        System.out.println("Danh sách subcategories cuối:");
        for (SubCategory subcategory : allSubcategories) {
            System.out.println("  " + subcategory.toString());
        }

        System.out.println("\n=== KẾT THÚC TEST SUBCATEGORY DAO ===");
    }

    public List<SubCategory> getAllSubCategories() throws SQLException {
        List<SubCategory> subCategories = new ArrayList<>();

        String sql = "SELECT * FROM subcategories";

        try (Connection conn = DBUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                SubCategory subcategory = new SubCategory();
                int subcategoryID = rs.getInt("SubcategoryID");
                int categoryID = rs.getInt("CategoryID");
                String subcategoryName = rs.getString("SubcategoryName");

                subCategories.add(new SubCategory(subcategoryID, categoryID, subcategoryName));
            }

        }
        return subCategories;
    }
    // edit by Bui Hieu
    public List<SubCategory> getAllSubcategoriesforfillter() {
        List<SubCategory> subcategories = new ArrayList<>();
        String sql = "SELECT SubcategoryID, CategoryID, SubcategoryName FROM subcategories";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SubCategory subcategory = new SubCategory();
                subcategory.setSubcategoryID(rs.getInt("SubcategoryID"));
                subcategory.setCategoryID(rs.getInt("CategoryID"));
                subcategory.setSubcategoryName(rs.getString("SubcategoryName"));
                subcategories.add(subcategory);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subcategories;
    }

    public List<SubCategory> getSubcategoriesByCategory(String categoryId) {
        List<SubCategory> subcategories = new ArrayList<>();
        String sql = "SELECT * FROM SubCategory WHERE categoryID = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                  while (rs.next()) {
                    SubCategory subcategory = new SubCategory();
                    subcategory.setSubcategoryID(rs.getInt("subcategoryID"));
                    subcategory.setSubcategoryName(rs.getString("subcategoryName"));
                    subcategory.setCategoryID(rs.getInt("categoryID"));
                    subcategories.add(subcategory);
                }
            }catch (SQLException e) {
        e.printStackTrace();
    }
            return subcategories;
        }
    }
