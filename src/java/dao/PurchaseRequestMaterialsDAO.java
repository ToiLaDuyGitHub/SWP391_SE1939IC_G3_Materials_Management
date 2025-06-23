package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.PurchaseRequestMaterials;
import util.DBUtil;
// Lớp DAO để quản lý các thao tác liên quan đến vật tư trong yêu cầu mua hàng
public class PurchaseRequestMaterialsDAO {

// Lấy danh sách vật tư theo ID của yêu cầu mua hàng
    public List<PurchaseRequestMaterials> getByPurchaseRequestId(int purchaseRequestId) {
        // Tạo danh sách để lưu các bản ghi vật tư
        List<PurchaseRequestMaterials> list = new ArrayList<>();
        // Câu lệnh SQL lấy thông tin vật tư từ bảng purchaserequestmaterials
        String sql = "SELECT PurchaseRequestID, MaterialID, Quantity FROM purchaserequestmaterials WHERE PurchaseRequestID = ?";
        // Sử dụng try-with-resources để tự động đóng kết nối và tài nguyên
        try (Connection conn = DBUtil.getConnection();// Lấy kết nối từ DBUtil 
             PreparedStatement ps = conn.prepareStatement(sql)) { // Chuẩn bị câu lệnh SQL
            // Gán giá trị PurchaseRequestID vào tham số
            ps.setInt(1, purchaseRequestId);
            try (ResultSet rs = ps.executeQuery()) { // Thực thi truy vấn
                 // Duyệt qua từng bản ghi trong kết quả
                while (rs.next()) {
                    // Tạo đối tượng PurchaseRequestMaterials từ dữ liệu
                    PurchaseRequestMaterials prm = new PurchaseRequestMaterials();
                    prm.setPurchaseRequestID(rs.getInt("PurchaseRequestID"));// ID yêu cầu mua hàng
                    prm.setMaterialID(rs.getInt("MaterialID"));// ID vật tư
                    prm.setQuantity(rs.getInt("Quantity"));// Số lượng vật tư
                        // Thêm đối tượng vào danh sách
                    list.add(prm);
                }
            }
              // In thông báo để debug (nên bỏ trong môi trường production)
            System.out.println("PurchaseRequestMaterialsDAO");
        } catch (SQLException e) {
             // In lỗi nếu có vấn đề khi truy vấn cơ sở dữ liệu
            e.printStackTrace();
        }
        return list;
    }
    // create method getByPurchaseRequestId
    // initialize MaterialsDAO to get material name and quantity and unit 
    // have to join purchaserequestmaterials join purchaserequest join material join units
}
