/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import model.Material;
import model.Category;
import model.MaterialQuantity;
import model.SubCategory;
import model.Supplier;
import model.Units;
import model.dto.MaterialDTO;
import util.DBUtil;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import model.dto.SearchMaterialDTO;

/**
 *
 * @author Admin
 */
public class MaterialDAO {

    public List<Material> getAllMaterials() {
        List<Material> list = new ArrayList<>();
        String sql = "SELECT m.MaterialID, m.MaterialName, m.Image, m.Detail, "
                + "c.CategoryID, c.CategoryName, "
                + "sc.SubcategoryID, sc.SubcategoryName, "
                + "u.MinUnit "
                + "FROM materials m "
                + "LEFT JOIN categories c ON m.CategoryID = c.CategoryID "
                + "LEFT JOIN subcategories sc ON m.SubcategoryID = sc.SubcategoryID "
                + "LEFT JOIN units u ON m.MaterialID = u.MaterialID";

        try (Connection conn = DBUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Material material = new Material();
                material.setMaterialID(rs.getInt("MaterialID"));
                material.setMaterialName(rs.getString("MaterialName"));
                material.setCategory(new Category(rs.getInt("CategoryID"), rs.getString("CategoryName")));
                material.setSubcategory(new SubCategory(rs.getInt("SubcategoryID"), rs.getInt("CategoryID"), rs.getString("SubcategoryName")));
                material.setImage(rs.getString("Image"));
                material.setDetail(rs.getString("Detail"));
                material.setMinUnit(rs.getString("MinUnit"));
                list.add(material);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Thêm vật tư mới
    public void addMaterial(String materialName, int subcategoryId, String image, String detail, int unitId) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // Bắt đầu transaction

            // 1. Lấy CategoryID từ SubcategoryID 
            String getCategorySQL = "SELECT CategoryID FROM subcategories WHERE SubcategoryID = ?";
            stmt = conn.prepareStatement(getCategorySQL);
            stmt.setInt(1, subcategoryId);
            rs = stmt.executeQuery();
            int categoryID = 0;
            if (rs.next()) {
                categoryID = rs.getInt("CategoryID");
            } else {
                throw new SQLException("Không tìm thấy danh mục con với SubcategoryID: " + subcategoryId);
            }
            rs.close();
            stmt.close();

            // Thêm vật tư
            String insertMaterialSQL = "INSERT INTO materials (MaterialName, CategoryID, SubcategoryID, Image, Detail) VALUES (?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(insertMaterialSQL, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, materialName);
            stmt.setInt(2, categoryID);
            stmt.setInt(3, subcategoryId);
            stmt.setString(4, image);
            stmt.setString(5, detail);
            stmt.executeUpdate();
            rs = stmt.getGeneratedKeys();
            int materialId = 0;
            if (rs.next()) {
                materialId = rs.getInt(1);
            }
            rs.close();
            stmt.close();

            // Lấy MinUnit dựa trên unitId trước khi chèn
            String getMinUnitSQL = "SELECT MinUnit FROM units WHERE UnitID = ?";
            stmt = conn.prepareStatement(getMinUnitSQL);
            stmt.setInt(1, unitId);
            rs = stmt.executeQuery();
            String minUnit = null;
            if (rs.next()) {
                minUnit = rs.getString("MinUnit");
            } else {
                throw new SQLException("Không tìm thấy MinUnit cho UnitID: " + unitId);
            }
            rs.close();
            stmt.close();

            // Thêm đơn vị
            String insertUnitSQL = "INSERT INTO units (MaterialID, MinUnit) VALUES (?, ?)";
            stmt = conn.prepareStatement(insertUnitSQL);
            stmt.setInt(1, materialId);
            stmt.setString(2, minUnit);
            stmt.executeUpdate();
            stmt.close();

            conn.commit(); // Xác nhận giao dịch
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback nếu có lỗi
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e; // Ném lại ngoại lệ để Servlet xử lý
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public List<Units> getAllUnits() {
        List<Units> list = new ArrayList<>();
        String sql = "SELECT DISTINCT MinUnit FROM units";

        try (Connection conn = DBUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Units unit = new Units();
                unit.setMinUnit(rs.getString("MinUnit")); // Chỉ gán MinUnit vì chúng ta chỉ cần giá trị duy nhất
                list.add(unit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int getUnitIdFromMinUnit(String minUnit) throws SQLException {
        String sql = "SELECT UnitID FROM units WHERE MinUnit = ? LIMIT 1";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, minUnit);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("UnitID");
            }
            throw new SQLException("Không tìm thấy UnitID cho MinUnit: " + minUnit);
        }
    }

    // Tìm vật tư theo tên
    public Material getMaterialByName(String materialName) {
        String sql = "SELECT m.*, c.CategoryName, sc.SubcategoryName, "
                + "u.MinUnit "
                + "FROM materials m "
                + "LEFT JOIN categories c ON m.CategoryID = c.CategoryID "
                + "LEFT JOIN subcategories sc ON m.SubcategoryID = sc.SubcategoryID "
                + "LEFT JOIN units u ON m.MaterialID = u.MaterialID "
                + "WHERE TRIM(LOWER(m.MaterialName)) = ?";

        try (Connection conn = DBUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, materialName.trim().toLowerCase());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Material material = new Material();
                material.setMaterialID(rs.getInt("MaterialID"));
                material.setMaterialName(rs.getString("MaterialName"));
                material.setCategory(new Category(rs.getInt("CategoryID"), rs.getString("CategoryName")));
                material.setSubcategory(new SubCategory(rs.getInt("SubcategoryID"), rs.getInt("CategoryID"), rs.getString("SubcategoryName")));
                material.setImage(rs.getString("Image"));
                material.setDetail(rs.getString("Detail"));
                material.setMinUnit(rs.getString("MinUnit"));
                return material;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Material> suggestMaterialsByName(String materialName) {
        List<Material> materials = new ArrayList<>();
        String sql = "SELECT m.*, c.CategoryName, sc.SubcategoryName, s.SupplierName, s.Address, s.PhoneNum, "
                + "mq.UsableQuantity, mq.BrokenQuantity, mq.TotalQuantity "
                + "FROM materials m "
                + "LEFT JOIN categories c ON m.CategoryID = c.CategoryID "
                + "LEFT JOIN subcategories sc ON m.SubcategoryID = sc.SubcategoryID "
                + "LEFT JOIN suppliers s ON m.SupplierID = s.SupplierID "
                + "LEFT JOIN materialquantities mq ON m.MaterialID = mq.MaterialID "
                + "WHERE LOWER(m.MaterialName) LIKE LOWER(?)";

        try (Connection conn = DBUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + materialName.trim().toLowerCase() + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Material material = new Material();
                material.setMaterialID(rs.getInt("MaterialID"));
                material.setMaterialName(rs.getString("MaterialName"));
                material.setCategory(new Category(rs.getInt("CategoryID"), rs.getString("CategoryName")));
                material.setSubcategory(new SubCategory(rs.getInt("SubcategoryID"), rs.getInt("CategoryID"), rs.getString("SubcategoryName")));
                material.setSupplierID(new Supplier(rs.getInt("SupplierID"), rs.getString("SupplierName"), rs.getString("Address"), rs.getString("PhoneNum")));
                material.setImage(rs.getString("Image"));
                material.setDetail(rs.getString("Detail"));

                MaterialQuantity quantity = new MaterialQuantity(
                        rs.getInt("MaterialID"),
                        rs.getInt("UsableQuantity"),
                        rs.getInt("BrokenQuantity"),
                        rs.getInt("TotalQuantity"));
                material.setQuantity(quantity);
                materials.add(material);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return materials;
    }

    // Cập nhật thông tin vật tư trong cơ sở dữ liệu
    public void updateMaterial(int materialID, String materialName, int subcategoryID, String imageUrl, String detail) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // Lấy CategoryID từ SubcategoryID
            String getCategorySQL = "SELECT CategoryID FROM subcategories WHERE SubcategoryID = ?";
            stmt = conn.prepareStatement(getCategorySQL);
            stmt.setInt(1, subcategoryID);
            rs = stmt.executeQuery();
            int categoryID = 0;
            if (rs.next()) {
                categoryID = rs.getInt("CategoryID");
            } else {
                throw new SQLException("Không tìm thấy danh mục con với SubcategoryID: " + subcategoryID);
            }
            rs.close();
            stmt.close();

            // Cập nhật thông tin vật tư trong bảng materials
            String updateMaterialSQL = "UPDATE materials SET MaterialName = ?, CategoryID = ?, SubcategoryID = ?, Image = ?, Detail = ? WHERE MaterialID = ?";
            stmt = conn.prepareStatement(updateMaterialSQL);
            stmt.setString(1, materialName);
            stmt.setInt(2, categoryID);
            stmt.setInt(3, subcategoryID);
            stmt.setString(4, imageUrl);
            stmt.setString(5, detail);
            stmt.setInt(6, materialID);
            stmt.executeUpdate();
            stmt.close();

            conn.commit(); // Xác nhận giao dịch nếu mọi thứ thành công
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e; // Ném ngoại lệ để servlet xử lý
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public List<Material> getMaterialsByCategory(int categoryID) {
        List<Material> list = new ArrayList<>();
        String sql = "SELECT m.MaterialID, m.MaterialName, m.Image, m.Detail, "
                + "c.CategoryID, c.CategoryName, "
                + "sc.SubcategoryID, sc.SubcategoryName, "
                + "u.MinUnit "
                + "FROM materials m "
                + "LEFT JOIN categories c ON m.CategoryID = c.CategoryID "
                + "LEFT JOIN subcategories sc ON m.SubcategoryID = sc.SubcategoryID "
                + "LEFT JOIN units u ON m.MaterialID = u.MaterialID "
                + "WHERE m.CategoryID = ?";

        try (Connection conn = DBUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoryID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Material material = new Material();
                material.setMaterialID(rs.getInt("MaterialID"));
                material.setMaterialName(rs.getString("MaterialName"));
                material.setCategory(new Category(rs.getInt("CategoryID"), rs.getString("CategoryName")));
                material.setSubcategory(new SubCategory(rs.getInt("SubcategoryID"), rs.getInt("CategoryID"), rs.getString("SubcategoryName")));
                material.setImage(rs.getString("Image"));
                material.setDetail(rs.getString("Detail"));
                material.setMinUnit(rs.getString("MinUnit"));
                list.add(material);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Xóa vật tư
    public void deleteMaterial(int materialID) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();

            // 1. Lấy SupplierID của vật tư
            String getSupplierSQL = "SELECT SupplierID FROM materials WHERE MaterialID = ?";
            stmt = conn.prepareStatement(getSupplierSQL);
            stmt.setInt(1, materialID);
            rs = stmt.executeQuery();
            int supplierID = 0;
            if (rs.next()) {
                supplierID = rs.getInt("SupplierID");
            } else {
                throw new SQLException("Không tìm thấy vật tư với MaterialID: " + materialID);
            }
            rs.close();
            stmt.close();

            // 2. Xóa các bản ghi liên quan trong bảng records
            String deleteRecordsSQL = "DELETE FROM records WHERE MaterialID = ?";
            stmt = conn.prepareStatement(deleteRecordsSQL);
            stmt.setInt(1, materialID);
            stmt.executeUpdate();
            stmt.close();

            // 3. Xóa trạng thái vật tư trong bảng materialquantities
            String deleteQuantitySQL = "DELETE FROM materialquantities WHERE MaterialID = ?";
            stmt = conn.prepareStatement(deleteQuantitySQL);
            stmt.setInt(1, materialID);
            stmt.executeUpdate();
            stmt.close();

            // 4. Xóa vật tư trong bảng materials
            String deleteMaterialSQL = "DELETE FROM materials WHERE MaterialID = ?";
            stmt = conn.prepareStatement(deleteMaterialSQL);
            stmt.setInt(1, materialID);
            stmt.executeUpdate();
            stmt.close();

            // 5. Kiểm tra xem Supplier có còn được tham chiếu bởi vật tư nào khác không
            String checkSupplierSQL = "SELECT COUNT(*) FROM materials WHERE SupplierID = ?";
            stmt = conn.prepareStatement(checkSupplierSQL);
            stmt.setInt(1, supplierID);
            rs = stmt.executeQuery();
            int count = 0;
            if (rs.next()) {
                count = rs.getInt(1);
            }
            rs.close();
            stmt.close();

            // Nếu không còn vật tư nào tham chiếu đến Supplier, xóa Supplier
            if (count == 0) {
                String deleteSupplierSQL = "DELETE FROM suppliers WHERE SupplierID = ?";
                stmt = conn.prepareStatement(deleteSupplierSQL);
                stmt.setInt(1, supplierID);
                stmt.executeUpdate();
                stmt.close();
            }

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Hủy bỏ giao dịch nếu có lỗi
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e; // Ném ngoại lệ để servlet xử lý
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public void importMaterials(List<Map<String, Integer>> materials, int userID) throws SQLException {
        Connection conn = null;
        PreparedStatement stmtHistory = null;
        PreparedStatement stmtUpdate = null;
        PreparedStatement stmtInsert = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // Bắt đầu transaction

            // 1. Tạo bản ghi trong importhistory
            String insertHistorySQL = "INSERT INTO importhistory (ImportedByID) VALUES (?)";
            stmtHistory = conn.prepareStatement(insertHistorySQL, Statement.RETURN_GENERATED_KEYS);
            stmtHistory.setInt(1, userID);
            stmtHistory.executeUpdate();

            // Lấy ID của bản ghi vừa tạo
            rs = stmtHistory.getGeneratedKeys();
            int importHistoryID = 0;
            if (rs.next()) {
                importHistoryID = rs.getInt(1);
            }

            // 2. Chuẩn bị batch cho cập nhật số lượng
            String updateQuantitySQL = "UPDATE materialquantities SET "
                    + "UsableQuantity = UsableQuantity + ?, "
                    + "BrokenQuantity = BrokenQuantity + ? "
                    + "WHERE MaterialID = ?";
            stmtUpdate = conn.prepareStatement(updateQuantitySQL);

            // 3. Chuẩn bị batch cho chèn lịch sử chi tiết
            String insertHistoryMaterialsSQL = "INSERT INTO importhistorymaterials "
                    + "(ImportHistoryID, MaterialID, UsableQuantity, BrokenQuantity) "
                    + "VALUES (?, ?, ?, ?)";
            stmtInsert = conn.prepareStatement(insertHistoryMaterialsSQL);

            // Thêm các bản ghi vào batch
            for (Map<String, Integer> material : materials) {
                int materialID = material.get("materialId");
                int usableQuantity = material.get("usableQuantity");
                int brokenQuantity = material.get("brokenQuantity");

                // Batch cho cập nhật số lượng
                stmtUpdate.setInt(1, usableQuantity);
                stmtUpdate.setInt(2, brokenQuantity);
                stmtUpdate.setInt(3, materialID);
                stmtUpdate.addBatch();

                // Batch cho chèn lịch sử
                stmtInsert.setInt(1, importHistoryID);
                stmtInsert.setInt(2, materialID);
                stmtInsert.setInt(3, usableQuantity);
                stmtInsert.setInt(4, brokenQuantity);
                stmtInsert.addBatch();
            }

            // Thực thi batch
            stmtUpdate.executeBatch();
            stmtInsert.executeBatch();

            conn.commit(); // Commit transaction

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback nếu có lỗi
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmtHistory != null) {
                stmtHistory.close();
            }
            if (stmtUpdate != null) {
                stmtUpdate.close();
            }
            if (stmtInsert != null) {
                stmtInsert.close();
            }
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public void importMaterialsForPurchase(List<Map<String, Integer>> materials, int userID, int purchaseRequestID) throws SQLException {
        Connection conn = null;
        PreparedStatement stmtHistory = null;
        PreparedStatement stmtUpdate = null;
        PreparedStatement stmtInsert = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // Bắt đầu transaction

            // 1. Tạo bản ghi trong importhistory
            String insertHistorySQL = "INSERT INTO importhistory (ImportedByID, PurchaseRequestID) VALUES (?, ?)";
            stmtHistory = conn.prepareStatement(insertHistorySQL, Statement.RETURN_GENERATED_KEYS);
            stmtHistory.setInt(1, userID);
            stmtHistory.setInt(2, purchaseRequestID);
            stmtHistory.executeUpdate();

            // Lấy ID của bản ghi vừa tạo
            rs = stmtHistory.getGeneratedKeys();
            int importHistoryID = 0;
            if (rs.next()) {
                importHistoryID = rs.getInt(1);
            }

            // 2. Chuẩn bị batch cho cập nhật số lượng
            String updateQuantitySQL = "UPDATE materialquantities SET "
                    + "UsableQuantity = UsableQuantity + ?, "
                    + "BrokenQuantity = BrokenQuantity + ?, "
                    + "TotalQuantity = TotalQuantity + ? "
                    + "WHERE MaterialID = ?";
            stmtUpdate = conn.prepareStatement(updateQuantitySQL);

            // 3. Chuẩn bị batch cho chèn lịch sử chi tiết
            String insertHistoryMaterialsSQL = "INSERT INTO importhistorymaterials "
                    + "(ImportHistoryID, MaterialID, UsableQuantity, BrokenQuantity) "
                    + "VALUES (?, ?, ?, ?)";
            stmtInsert = conn.prepareStatement(insertHistoryMaterialsSQL);

            // Thêm các bản ghi vào batch
            for (Map<String, Integer> material : materials) {
                int materialID = material.get("materialId");
                int usableQuantity = material.get("usableQuantity");
                int brokenQuantity = material.get("brokenQuantity");
                int totalQuantity = usableQuantity + brokenQuantity; // Tính tổng số lượng

                // Batch cho cập nhật số lượng
                stmtUpdate.setInt(1, usableQuantity);
                stmtUpdate.setInt(2, brokenQuantity);
                stmtUpdate.setInt(3, totalQuantity);
                stmtUpdate.setInt(4, materialID);
                stmtUpdate.addBatch();

                // Batch cho chèn lịch sử
                stmtInsert.setInt(1, importHistoryID);
                stmtInsert.setInt(2, materialID);
                stmtInsert.setInt(3, usableQuantity);
                stmtInsert.setInt(4, brokenQuantity);
                stmtInsert.addBatch();
            }

            // Thực thi batch
            stmtUpdate.executeBatch();
            stmtInsert.executeBatch();

            conn.commit(); // Commit transaction

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback nếu có lỗi
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmtHistory != null) {
                stmtHistory.close();
            }
            if (stmtUpdate != null) {
                stmtUpdate.close();
            }
            if (stmtInsert != null) {
                stmtInsert.close();
            }
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public Material getMaterialById(int materialID) {
        String sql = "SELECT m.MaterialID, m.MaterialName, m.Image, m.Detail, "
                + "c.CategoryID, c.CategoryName, "
                + "sc.SubcategoryID, sc.SubcategoryName, "
                + "u.MinUnit "
                + "FROM materials m "
                + "LEFT JOIN categories c ON m.CategoryID = c.CategoryID "
                + "LEFT JOIN subcategories sc ON m.SubcategoryID = sc.SubcategoryID "
                + "LEFT JOIN units u ON m.MaterialID = u.MaterialID "
                + "WHERE m.MaterialID = ?";

        try (Connection conn = DBUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, materialID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Material material = new Material();
                material.setMaterialID(rs.getInt("MaterialID"));
                material.setMaterialName(rs.getString("MaterialName"));
                material.setCategory(new Category(rs.getInt("CategoryID"), rs.getString("CategoryName")));
                material.setSubcategory(new SubCategory(rs.getInt("SubcategoryID"), rs.getInt("CategoryID"), rs.getString("SubcategoryName")));
                material.setImage(rs.getString("Image"));
                material.setDetail(rs.getString("Detail"));
                material.setMinUnit(rs.getString("MinUnit"));
                return material;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Lấy danh sách vật tư với SupplierName và MinUnit
    public List<MaterialDTO> getMaterialsWithCategoryAndSupplier() {
        List<MaterialDTO> materials = new ArrayList<>();
        String sql = "SELECT m.MaterialID, m.MaterialName, s.SupplierName, u.MinUnit, u.MaxUnit "
                + "FROM materials m "
                + "LEFT JOIN suppliers s ON m.SupplierID = s.SupplierID "
                + "LEFT JOIN units u ON m.MaterialID = u.MaterialID";

        try (Connection conn = DBUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                MaterialDTO material = new MaterialDTO();
                material.setMaterialID(rs.getInt("MaterialID"));
                material.setMaterialName(rs.getString("MaterialName"));
                material.setSupplierName(rs.getString("SupplierName"));
                material.setMinUnit(rs.getString("MinUnit"));
                material.setMaxUnit(rs.getString("MaxUnit"));
                materials.add(material);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return materials;
    }

    // Phương thức lấy tất cả nhà cung cấp
    public List<Supplier> getAllSuppliers() {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT SupplierID, SupplierName FROM suppliers";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                suppliers.add(new Supplier(rs.getInt("SupplierID"), rs.getString("SupplierName"), null, null));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return suppliers;
    }

    /// Phương thức tìm kiếm vật tư dựa trên từ khóa, danh mục và danh mục con
    public List<Material> searchMaterials(String keyword, String categoryId, String subcategoryId) {
        List<Material> list = new ArrayList<>();
        String sql = "SELECT m.MaterialID, m.MaterialName, m.Image, m.Detail, "
                + "c.CategoryID, c.CategoryName, "
                + "sc.SubcategoryID, sc.SubcategoryName, "
                + "mq.UsableQuantity, mq.BrokenQuantity, mq.TotalQuantity, "
                + "u.MinUnit "
                + "FROM materials m "
                + "LEFT JOIN categories c ON m.CategoryID = c.CategoryID "
                + "LEFT JOIN subcategories sc ON m.SubcategoryID = sc.SubcategoryID "
                + "LEFT JOIN materialquantities mq ON m.MaterialID = mq.MaterialID "
                + "LEFT JOIN units u ON m.MaterialID = u.MaterialID "
                + "WHERE 1=1 ";

        if (keyword != null && !keyword.isEmpty()) {
            sql += "AND TRIM(LOWER(m.MaterialName)) LIKE ? ";
        }
        if (categoryId != null && !categoryId.isEmpty()) {
            sql += "AND m.CategoryID = ? ";
        }
        if (subcategoryId != null && !subcategoryId.isEmpty()) {
            sql += "AND m.SubcategoryID = ? ";
        }

        try (Connection conn = DBUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            int paramIndex = 1;
            if (keyword != null && !keyword.isEmpty()) {
                stmt.setString(paramIndex++, "%" + keyword.trim().toLowerCase() + "%");
            }
            if (categoryId != null && !categoryId.isEmpty()) {
                stmt.setInt(paramIndex++, Integer.parseInt(categoryId));
            }
            if (subcategoryId != null && !subcategoryId.isEmpty()) {
                stmt.setInt(paramIndex++, Integer.parseInt(subcategoryId));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Material material = new Material();
                material.setMaterialID(rs.getInt("MaterialID"));
                material.setMaterialName(rs.getString("MaterialName"));
                material.setCategory(new Category(rs.getInt("CategoryID"), rs.getString("CategoryName")));
                material.setSubcategory(new SubCategory(rs.getInt("SubcategoryID"), rs.getInt("CategoryID"), rs.getString("SubcategoryName")));
                material.setImage(rs.getString("Image"));
                material.setDetail(rs.getString("Detail"));
                material.setMinUnit(rs.getString("MinUnit"));

                MaterialQuantity quantity = new MaterialQuantity(
                        rs.getInt("MaterialID"),
                        rs.getInt("UsableQuantity"),
                        rs.getInt("BrokenQuantity"),
                        rs.getInt("TotalQuantity"));
                material.setQuantity(quantity);
                list.add(material);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    //edit by Bui Hieu

    public List<SearchMaterialDTO> searchMaterialsForPurchase(String term, String category, String subcategory) {
        List<SearchMaterialDTO> materials = new ArrayList<>();
        String sql = "SELECT m.MaterialID, m.MaterialName, s.SupplierName, u.MinUnit "
                + "FROM materials m "
                + "LEFT JOIN suppliers s ON m.SupplierID = s.SupplierID "
                + "LEFT JOIN units u ON m.MaterialID = u.MaterialID "
                + "WHERE (m.MaterialName LIKE ? OR m.MaterialID LIKE ?) "
                + "AND (m.CategoryID = ? OR ? IS NULL) "
                + "AND (m.SubcategoryID = ? OR ? IS NULL)";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            String searchTerm = "%" + (term != null ? term : "") + "%";
            ps.setString(1, searchTerm);
            ps.setString(2, searchTerm);
            if (category != null && !category.isEmpty()) {
                ps.setInt(3, Integer.parseInt(category));
                ps.setInt(4, Integer.parseInt(category));
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
                ps.setNull(4, java.sql.Types.INTEGER);
            }
            if (subcategory != null && !subcategory.isEmpty()) {
                ps.setInt(5, Integer.parseInt(subcategory));
                ps.setInt(6, Integer.parseInt(subcategory));
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
                ps.setNull(6, java.sql.Types.INTEGER);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SearchMaterialDTO material = new SearchMaterialDTO();
                    material.setMaterialID(rs.getInt("MaterialID"));
                    material.setMaterialName(rs.getString("MaterialName"));
                    material.setSupplierName(rs.getString("SupplierName"));
                    material.setUnit(rs.getString("MinUnit"));
                    materials.add(material);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return materials;
    }

    public SearchMaterialDTO getMaterialByID(int materialID) throws SQLException {
        String sql = "SELECT m.MaterialID, m.MaterialName, u.MinUnit, s.SupplierName "
                + "FROM materials m "
                + "JOIN suppliers s ON m.SupplierID = s.SupplierID "
                + "LEFT JOIN units u ON m.MaterialID = u.MaterialID "
                + "WHERE m.MaterialID = ?";

        try (Connection conn = DBUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, materialID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    SearchMaterialDTO dto = new SearchMaterialDTO();
                    dto.setMaterialID(rs.getInt("MaterialID"));
                    dto.setMaterialName(rs.getString("MaterialName"));
                    dto.setUnit(rs.getString("MinUnit"));
                    dto.setSupplierName(rs.getString("SupplierName"));
                    return dto;
                }
            }
            return null;
        }
    }
//hieunt    
    // Phương thức lấy tất cả vật tư cùng số lượng

    public List<Material> getAllMaterialsWithQuantities() {
        List<Material> list = new ArrayList<>();
        String sql = """
                     SELECT m.MaterialID,
                         m.MaterialName,
                         mq.UsableQuantity,
                         mq.BrokenQuantity,
                         mq.TotalQuantity
                     FROM 
                         materials m
                         INNER JOIN materialquantities mq ON m.MaterialID = mq.MaterialID;""";

        try (Connection conn = DBUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Material material = new Material();

                material.setMaterialID(rs.getInt("MaterialID"));
                material.setMaterialName(rs.getString("MaterialName"));

                MaterialQuantity quantity = new MaterialQuantity(
                        rs.getInt("MaterialID"),
                        rs.getInt("UsableQuantity"),
                        rs.getInt("BrokenQuantity"),
                        rs.getInt("TotalQuantity"));
                material.setQuantity(quantity);
                list.add(material);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
// Phương thức cập nhật số lượng vật tư

    public void updateMaterialQuantities(int materialID, int usableQuantity, int brokenQuantity) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            String updateSQL = "UPDATE materialquantities SET UsableQuantity = ?, BrokenQuantity = ? WHERE MaterialID = ?";
            stmt = conn.prepareStatement(updateSQL);
            stmt.setInt(1, usableQuantity);
            stmt.setInt(2, brokenQuantity);
            stmt.setInt(3, materialID);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                // Nếu không có bản ghi, thêm mới
                String insertSQL = "INSERT INTO materialquantities (MaterialID, UsableQuantity, BrokenQuantity) VALUES (?, ?, ?)";
                stmt = conn.prepareStatement(insertSQL);
                stmt.setInt(1, materialID);
                stmt.setInt(2, usableQuantity);
                stmt.setInt(3, brokenQuantity);
                stmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    // Phương thức tìm kiếm vật tư cho trang inventory
    public List<Material> searchInventoryMaterials(String keyword) {
        List<Material> list = new ArrayList<>();
        String sql = """
                     SELECT m.MaterialID,
                         m.MaterialName,
                         mq.UsableQuantity,
                         mq.BrokenQuantity,
                         mq.TotalQuantity
                     FROM 
                         materials m
                         INNER JOIN materialquantities mq ON m.MaterialID = mq.MaterialID
                     WHERE TRIM(LOWER(m.MaterialName)) LIKE ?""";

        try (Connection conn = DBUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + keyword.trim().toLowerCase() + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Material material = new Material();
                material.setMaterialID(rs.getInt("MaterialID"));
                material.setMaterialName(rs.getString("MaterialName"));

                MaterialQuantity quantity = new MaterialQuantity(
                        rs.getInt("MaterialID"),
                        rs.getInt("UsableQuantity"),
                        rs.getInt("BrokenQuantity"),
                        rs.getInt("TotalQuantity"));
                material.setQuantity(quantity);
                list.add(material);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
