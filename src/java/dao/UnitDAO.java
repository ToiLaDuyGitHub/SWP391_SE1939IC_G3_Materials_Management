package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import util.DBUtil;

// Lớp DAO để xử lý các thao tác liên quan đến đơn vị của vật tư
public class UnitDAO {

    // Lấy đơn vị nhỏ nhất (MinUnit) của vật tư theo MaterialID
    public String getMinUnitByMaterialId(int materialId) {
        // Biến để lưu giá trị đơn vị nhỏ nhất
        String minUnit = null;
        // Câu lệnh SQL lấy MinUnit từ bảng units theo MaterialID, giới hạn 1 bản ghi
        String sql = "SELECT MinUnit FROM units WHERE MaterialID = ? LIMIT 1";
        
        // Sử dụng try-with-resources để tự động đóng kết nối và tài nguyên
        try (Connection conn = DBUtil.getConnection(); // Lấy kết nối từ DBUtil
             PreparedStatement ps = conn.prepareStatement(sql)) { // Chuẩn bị câu lệnh SQL
            
            // Gán giá trị MaterialID vào tham số trong câu lệnh SQL
            ps.setInt(1, materialId);
            try (ResultSet rs = ps.executeQuery()) { // Thực thi truy vấn
                // Nếu có bản ghi, lấy giá trị MinUnit
                if (rs.next()) {
                    minUnit = rs.getString("MinUnit");
                }
            }
            // In thông báo để debug (nên bỏ trong môi trường production)
            System.out.println("unit");
        } catch (SQLException e) {
            // In lỗi nếu có vấn đề khi truy vấn cơ sở dữ liệu
            e.printStackTrace();
        }
        // Trả về đơn vị nhỏ nhất hoặc null nếu không tìm thấy
        return minUnit;
    }
}