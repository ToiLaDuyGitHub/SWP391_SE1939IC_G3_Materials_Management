/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import model.Material;
import util.DBUtil;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.Category;
import model.MaterialQuantity;
import model.SubCategory;
import model.Supplier;
import model.Units;
import model.dto.MaterialDTO;
import java.sql.Statement;
import java.util.Map;

/**
 *
 * @author Admin
 */
public class MaterialDAO {

    public List<Material> getAllMaterials() {
        List<Material> list = new ArrayList<>();
        String sql = "SELECT m.MaterialID, m.MaterialName, m.Image, m.Detail, "
                + "                     c.CategoryID, c.CategoryName, "
                + "                     sc.SubcategoryID, sc.SubcategoryName, "
                + "                     s.SupplierID, s.SupplierName,s.Address, s.PhoneNum, "
                + "			mq.UsableQuantity, mq.BrokenQuantity, mq.TotalQuantity"
                + "                     FROM materials m"
                + "                     LEFT JOIN categories c ON m.CategoryID = c.CategoryID "
                + "			LEFT JOIN subcategories sc ON m.SubcategoryID = sc.SubcategoryID "
                + "                     LEFT JOIN suppliers s ON m.SupplierID = s.SupplierID "
                + "                     LEFT JOIN materialquantities mq ON m.MaterialID = mq.MaterialID";

        try (Connection conn = DBUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
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
                list.add(material);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    // Thêm vật tư mới
    public void addMaterial(String materialName, int subcategoryId, String supplierName,
            String address, String phoneNum, String image, String detail,
            int totalQuantity, int usableQuantity, int unitId) throws SQLException {
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
            int CategoryID = 0;
            if (rs.next()) {
                CategoryID = rs.getInt("CategoryID");
            } else {
                throw new SQLException("Không tìm thấy danh mục con với SubcategoryID: " + subcategoryId);
            }
            rs.close();
            stmt.close();

            String insertSupplierSQL = "INSERT INTO suppliers (SupplierName, Address, PhoneNum) VALUES (?, ?, ?)";
            stmt = conn.prepareStatement(insertSupplierSQL, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, supplierName);
            stmt.setString(2, address);
            stmt.setString(3, phoneNum);
            stmt.executeUpdate();
            rs = stmt.getGeneratedKeys();
            int supplierId = 0;
            if (rs.next()) {
                supplierId = rs.getInt(1);
            }
            rs.close();
            stmt.close();

            // Thêm vật tư
            String insertMaterialSQL = "INSERT INTO materials (MaterialName, CategoryID, SubcategoryID, SupplierID, Image, Detail) VALUES (?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(insertMaterialSQL, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, materialName);
            stmt.setInt(2, CategoryID);
            stmt.setInt(3, subcategoryId);
            stmt.setInt(4, supplierId);
            stmt.setString(5, image);
            stmt.setString(6, detail);
            stmt.executeUpdate();
            rs = stmt.getGeneratedKeys();
            int materialId = 0;
            if (rs.next()) {
                materialId = rs.getInt(1);
            }
            rs.close();
            stmt.close();

            // 4. Thêm số lượng vật tư
            String insertQuantitySQL = "INSERT INTO materialquantities (MaterialID, UsableQuantity, BrokenQuantity) VALUES (?, ?, 0)";
            stmt = conn.prepareStatement(insertQuantitySQL);
            stmt.setInt(1, materialId);
            stmt.setInt(2, usableQuantity);
            stmt.executeUpdate();
            stmt.close();
            // 5. Lấy MinUnit dựa trên unitId trước khi chèn
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
            // 6. Thêm đơn vị
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
        String sql = "SELECT f.*, c.CategoryName, sc.SubcategoryName, sup.SupplierName, sup.Address, sup.PhoneNum, fc.NewQuantity, fc.UsableQuantity, fc.BrokenQuantity "
                + "FROM materials f "
                + "LEFT JOIN categories c ON f.CategoryID = c.CategoryID "
                + "LEFT JOIN subcategories sc ON f.SubcategoryID = sc.SubcategoryID "
                + "LEFT JOIN suppliers sup ON f.SupplierID = sup.SupplierID "
                + "LEFT JOIN materialconditions fc ON f.MaterialID = fc.MaterialID "
                + "WHERE TRIM(LOWER(f.MaterialName)) = ?";

        try (Connection conn = DBUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, materialName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Material material = new Material();
                material.setMaterialID(rs.getInt("MaterialID"));
                material.setMaterialName(rs.getString("MaterialName"));
                material.setCategory(new Category(rs.getInt("CategoryID"), rs.getString("CategoryName")));
                material.setSubcategory(new SubCategory(rs.getInt("SubcategoryID"), rs.getInt("CategoryID"), rs.getString("SubcategoryName")));
                material.setSupplierID(new Supplier(rs.getInt("SupplierID"), rs.getString("SupplierName"), rs.getString("Address"), rs.getString("PhoneNum")));
                
                material.setImage(rs.getString("Image"));

                // Gán đối tượng MaterialCondition
                MaterialQuantity quantity = new MaterialQuantity(
                        rs.getInt("MaterialID"),
                        rs.getInt("UsableQuantity"),
                        rs.getInt("BrokenQuantity"),
                        rs.getInt("TotalQuantity"));
                material.setQuantity(quantity);
                return material;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Material> suggestMaterialsByName(String materialName) {
        List<Material> materials = new ArrayList<>();
        String sql = "SELECT m.*, c.CategoryName, sc.SubcategoryName, s.SupplierName, s.Address, s.PhoneNum, " +
                    "mq.UsableQuantity, mq.BrokenQuantity, mq.TotalQuantity " +
                    "FROM materials m " +
                    "LEFT JOIN categories c ON m.CategoryID = c.CategoryID " +
                    "LEFT JOIN subcategories sc ON m.SubcategoryID = sc.SubcategoryID " +
                    "LEFT JOIN suppliers s ON m.SupplierID = s.SupplierID " +
                    "LEFT JOIN materialquantities mq ON m.MaterialID = mq.MaterialID " +
                    "WHERE LOWER(m.MaterialName) LIKE LOWER(?)";

        try (Connection conn = DBUtil.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + materialName.trim().toLowerCase() + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Material material = new Material();
                material.setMaterialID(rs.getInt("MaterialID"));
                material.setMaterialName(rs.getString("MaterialName"));
                
                Category category = new Category();
                category.setCategoryID(rs.getInt("CategoryID"));
                category.setCategoryName(rs.getString("CategoryName"));
                material.setCategory(category);
                
                SubCategory subCategory = new SubCategory();
                subCategory.setSubcategoryID(rs.getInt("SubcategoryID"));
                subCategory.setSubcategoryName(rs.getString("SubcategoryName"));
                material.setSubcategory(subCategory);
                
                Supplier supplier = new Supplier();
                supplier.setSupplierID(rs.getInt("SupplierID"));
                supplier.setSupplierName(rs.getString("SupplierName"));
                supplier.setAddress(rs.getString("Address"));
                supplier.setPhoneNum(rs.getString("PhoneNum"));
                material.setSupplierID(supplier);
                
                MaterialQuantity quantity = new MaterialQuantity();
                quantity.setMaterialID(rs.getInt("MaterialID"));
                quantity.setUsableQuantity(rs.getInt("UsableQuantity"));
                quantity.setBrokenQuantity(rs.getInt("BrokenQuantity"));
                quantity.setTotalQuantity(rs.getInt("TotalQuantity"));
                material.setQuantity(quantity);
                
                materials.add(material);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return materials;
    }

    // Cập nhật thông tin vật tư trong cơ sở dữ liệu
    public void updateMaterial(int materialID, String materialName, int subcategoryID, String supplierName,
            String supplierAddress, String supplierPhone, String imageUrl,
            int usableQuantity, int brokenQuantity, String detail) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // 1. Lấy SupplierID và CategoryID hiện tại của vật tư
            String getMaterialSQL = "SELECT SupplierID, CategoryID FROM materials WHERE MaterialID = ?";
            stmt = conn.prepareStatement(getMaterialSQL);
            stmt.setInt(1, materialID);
            rs = stmt.executeQuery();
            int supplierID = 0;
            int categoryID = 0;
            if (rs.next()) {
                supplierID = rs.getInt("SupplierID");
                categoryID = rs.getInt("CategoryID");
            } else {
                throw new SQLException("Không tìm thấy vật tư với MaterialID: " + materialID);
            }
            rs.close();
            stmt.close();

            // 2. Lấy CategoryID từ SubcategoryID (nếu subcategoryID thay đổi)
            String getCategorySQL = "SELECT CategoryID FROM subcategories WHERE SubcategoryID = ?";
            stmt = conn.prepareStatement(getCategorySQL);
            stmt.setInt(1, subcategoryID);
            rs = stmt.executeQuery();
            if (rs.next()) {
                categoryID = rs.getInt("CategoryID");
            } else {
                throw new SQLException("Không tìm thấy danh mục con với SubcategoryID: " + subcategoryID);
            }
            rs.close();
            stmt.close();

            // 3. Cập nhật thông tin nhà cung cấp trong bảng suppliers
            String updateSupplierSQL = "UPDATE suppliers SET SupplierName = ?, Address = ?, PhoneNum = ? WHERE SupplierID = ?";
            stmt = conn.prepareStatement(updateSupplierSQL);
            stmt.setString(1, supplierName);
            stmt.setString(2, supplierAddress);
            stmt.setString(3, supplierPhone);
            stmt.setInt(4, supplierID);
            stmt.executeUpdate();
            stmt.close();

            // 4. Cập nhật thông tin vật tư trong bảng materials
            String updateMaterialSQL = "UPDATE materials SET MaterialName = ?, CategoryID = ?, SubcategoryID = ?, SupplierID = ?, Image = ?, Detail = ? WHERE MaterialID = ?";
            stmt = conn.prepareStatement(updateMaterialSQL);
            stmt.setString(1, materialName);
            stmt.setInt(2, categoryID);
            stmt.setInt(3, subcategoryID);
            stmt.setInt(4, supplierID);
            stmt.setString(5, imageUrl);
            stmt.setString(6, detail);
            stmt.setInt(7, materialID);
            stmt.executeUpdate();
            stmt.close();

            // 5. Cập nhật trạng thái vật tư trong bảng materialconditions
            String updateQuantitySQL = "UPDATE materialquantities SET UsableQuantity = ?, BrokenQuantity = ? WHERE MaterialID = ?";
            stmt = conn.prepareStatement(updateQuantitySQL);
            stmt.setInt(1, usableQuantity);
            stmt.setInt(2, brokenQuantity);
            stmt.setInt(3, materialID);
            stmt.executeUpdate();
            stmt.close();

            conn.commit();
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
            + "                     c.CategoryID, c.CategoryName, "
            + "                     sc.SubcategoryID, sc.SubcategoryName, "
            + "                     s.SupplierID, s.SupplierName, "
            + "                     mq.UsableQuantity, mq.BrokenQuantity, mq.TotalQuantity"
            + "                     FROM materials m"
            + "                     LEFT JOIN categories c ON m.CategoryID = c.CategoryID "
            + "                     LEFT JOIN subcategories sc ON m.SubcategoryID = sc.SubcategoryID "
            + "                     LEFT JOIN suppliers s ON m.SupplierID = s.SupplierID "
            + "                     LEFT JOIN materialquantities mq ON m.MaterialID = mq.MaterialID"
            + "                     WHERE m.CategoryID = ?"; 

    try (Connection conn = DBUtil.getConnection(); 
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, categoryID);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Material material = new Material();

            material.setMaterialID(rs.getInt("MaterialID"));
            material.setMaterialName(rs.getString("MaterialName"));
            material.setCategory(new Category(rs.getInt("CategoryID"), rs.getString("CategoryName")));
            material.setSubcategory(new SubCategory(rs.getInt("SubcategoryID"), rs.getInt("CategoryID"), rs.getString("SubcategoryName")));
            material.setSupplierID(new Supplier(rs.getInt("SupplierID"), rs.getString("SupplierName"), null, null));
            material.setImage(rs.getString("Image"));
            material.setDetail(rs.getString("Detail"));

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

            // 3. Xóa trạng thái vật tư trong bảng materialconditions
            String deleteConditionSQL = "DELETE FROM materialconditions WHERE MaterialID = ?";
            stmt = conn.prepareStatement(deleteConditionSQL);
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

    // Lấy danh sách vật tư với SupplierName và MinUnit
    public List<MaterialDTO> getMaterialsWithCategoryAndSupplier() {
        List<MaterialDTO> materials = new ArrayList<>();
        String sql = "SELECT m.MaterialID, m.MaterialName, s.SupplierName, u.MinUnit, u.MaxUnit " +
                     "FROM materials m " +
                     "LEFT JOIN suppliers s ON m.SupplierID = s.SupplierID " +
                     "LEFT JOIN units u ON m.MaterialID = u.MaterialID";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

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
            if (rs != null) rs.close();
            if (stmtHistory != null) stmtHistory.close();
            if (stmtUpdate != null) stmtUpdate.close();
            if (stmtInsert != null) stmtInsert.close();
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
            if (rs != null) rs.close();
            if (stmtHistory != null) stmtHistory.close();
            if (stmtUpdate != null) stmtUpdate.close();
            if (stmtInsert != null) stmtInsert.close();
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
                + "s.SupplierID, s.SupplierName, "
                + "mq.UsableQuantity, mq.BrokenQuantity, mq.TotalQuantity "
                + "FROM materials m "
                + "LEFT JOIN categories c ON m.CategoryID = c.CategoryID "
                + "LEFT JOIN subcategories sc ON m.SubcategoryID = sc.SubcategoryID "
                + "LEFT JOIN suppliers s ON m.SupplierID = s.SupplierID "
                + "LEFT JOIN materialquantities mq ON m.MaterialID = mq.MaterialID "
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
                material.setSupplierID(new Supplier(rs.getInt("SupplierID"), rs.getString("SupplierName"), null, null));
                material.setImage(rs.getString("Image"));
                material.setDetail(rs.getString("Detail"));

                MaterialQuantity quantity = new MaterialQuantity(
                        rs.getInt("MaterialID"),
                        rs.getInt("UsableQuantity"),
                        rs.getInt("BrokenQuantity"),
                        rs.getInt("TotalQuantity"));
                material.setQuantity(quantity);
                System.out.println("getMaterialById");
                return material;

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}


