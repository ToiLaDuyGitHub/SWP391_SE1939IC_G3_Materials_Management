package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.PurchaseRequest;
import util.DBUtil;

// Lớp DAO để xử lý các thao tác liên quan đến yêu cầu mua hàng (PurchaseRequest)
public class PurchaseRequestDAO {

    // Lấy danh sách các yêu cầu mua hàng đã được duyệt (Status = 1) và trạng thái nhập kho
    public List<PurchaseRequest> getApprovedRequestsWithImportStatus() {
        // Tạo danh sách để lưu các yêu cầu mua hàng
        List<PurchaseRequest> list = new ArrayList<>();
        // Câu lệnh SQL lấy thông tin yêu cầu mua hàng và kiểm tra xem đã nhập kho chưa
        String sql = "SELECT pr.PurchaseRequestID, pr.CreatedByID, pr.ApprovedByID, pr.CreatedDate, pr.ApprovedDate, pr.Note, pr.Status, " +
                "CASE WHEN ih.PurchaseRequestID IS NOT NULL THEN 1 ELSE 0 END AS IsImported " +
                "FROM purchaserequests pr " +
                "LEFT JOIN importhistory ih ON pr.PurchaseRequestID = ih.PurchaseRequestID " +
                "WHERE pr.Status = 1";
        
        // Sử dụng try-with-resources để tự động đóng kết nối và tài nguyên
        try (Connection conn = DBUtil.getConnection(); // Lấy kết nối từ DBUtil
             PreparedStatement ps = conn.prepareStatement(sql); // Chuẩn bị câu lệnh SQL
             ResultSet rs = ps.executeQuery()) { // Thực thi truy vấn và lấy kết quả
            
            // Duyệt qua từng bản ghi trong kết quả
            while (rs.next()) {
                // Tạo đối tượng PurchaseRequest từ dữ liệu bản ghi
                PurchaseRequest pr = new PurchaseRequest();
                pr.setPurchaseRequestID(rs.getInt("PurchaseRequestID")); // ID yêu cầu mua hàng
                pr.setCreatedByID(rs.getInt("CreatedByID")); // ID người tạo yêu cầu
                pr.setApprovedByID(rs.getObject("ApprovedByID") != null ? rs.getInt("ApprovedByID") : null); // ID người duyệt (có thể null)
                pr.setCreatedDate(rs.getTimestamp("CreatedDate")); // Ngày tạo yêu cầu
                pr.setApprovedDate(rs.getTimestamp("ApprovedDate")); // Ngày duyệt yêu cầu
                pr.setNote(rs.getString("Note")); // Ghi chú của yêu cầu
                pr.setStatus(rs.getByte("Status")); // Trạng thái yêu cầu (1 là đã duyệt)
                // Thêm đối tượng vào danh sách
                list.add(pr);
            }
        } catch (Exception e) {
            // In lỗi nếu có vấn đề khi truy vấn cơ sở dữ liệu
            e.printStackTrace();
        }
        // Trả về danh sách yêu cầu mua hàng
        return list;
    }

    // Kiểm tra xem một yêu cầu mua hàng đã được nhập kho hay chưa
    public boolean isImported(int requestId) throws SQLException {
        // Câu lệnh SQL đếm số bản ghi trong importhistory liên quan đến PurchaseRequestID
        String sql = "SELECT COUNT(*) FROM importhistory WHERE PurchaseRequestID = ?";
        
        try (Connection conn = DBUtil.getConnection(); // Lấy kết nối
             PreparedStatement stmt = conn.prepareStatement(sql)) { // Chuẩn bị câu lệnh
            
            // Gán giá trị requestId vào tham số trong câu lệnh SQL
            stmt.setInt(1, requestId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Nếu số lượng bản ghi > 0, yêu cầu đã được nhập kho
                return rs.getInt(1) > 0;
            }
        }
        // Trả về false nếu không tìm thấy hoặc có lỗi
        return false;
    }
    
    // Lấy thông tin yêu cầu mua hàng theo ID
    public PurchaseRequest getById(int purchaseRequestId) {
        // Câu lệnh SQL lấy thông tin yêu cầu mua hàng theo PurchaseRequestID
        String sql = "SELECT PurchaseRequestID, CreatedByID, ApprovedByID, CreatedDate, ApprovedDate, Note, Status " +
                "FROM purchaserequests WHERE PurchaseRequestID = ?";
        
        try (Connection conn = DBUtil.getConnection(); // Lấy kết nối
             PreparedStatement ps = conn.prepareStatement(sql)) { // Chuẩn bị câu lệnh
            
            // Gán giá trị purchaseRequestId vào tham số
            ps.setInt(1, purchaseRequestId);
            try (ResultSet rs = ps.executeQuery()) { // Thực thi truy vấn
                if (rs.next()) {
                    // Nếu tìm thấy bản ghi, tạo và trả về đối tượng PurchaseRequest
                    PurchaseRequest pr = new PurchaseRequest();
                    pr.setPurchaseRequestID(rs.getInt("PurchaseRequestID")); // ID yêu cầu mua hàng
                    pr.setCreatedByID(rs.getInt("CreatedByID")); // ID người tạo yêu cầu
                    pr.setApprovedByID(rs.getObject("ApprovedByID") != null ? rs.getInt("ApprovedByID") : null); // ID người duyệt
                    pr.setCreatedDate(rs.getTimestamp("CreatedDate")); // Ngày tạo yêu cầu
                    pr.setApprovedDate(rs.getTimestamp("ApprovedDate")); // Ngày duyệt yêu cầu
                    pr.setNote(rs.getString("Note")); // Ghi chú
                    pr.setStatus(rs.getByte("Status")); // Trạng thái yêu cầu
                    return pr;
                }
            }
            // In thông báo để debug (có thể bỏ trong môi trường production)
            System.out.println("getById PurchaseRequest");
        } catch (Exception e) {
            // In lỗi nếu có vấn đề khi truy vấn
            e.printStackTrace();
        }
        // Trả về null nếu không tìm thấy hoặc có lỗi
        return null;
    }

    // Cập nhật trạng thái của yêu cầu mua hàng
    public void updateImportStatus(int requestId, int isImported) {
        // Câu lệnh SQL cập nhật trạng thái của yêu cầu mua hàng
        String sql = "UPDATE purchaserequests SET Status = ? WHERE PurchaseRequestID = ?";
        
        try (Connection conn = DBUtil.getConnection(); // Lấy kết nối
             PreparedStatement ps = conn.prepareStatement(sql)) { // Chuẩn bị câu lệnh
            
            // Gán giá trị trạng thái và ID yêu cầu vào tham số
            ps.setInt(1, isImported);
            ps.setInt(2, requestId);
            // Thực thi câu lệnh cập nhật
            ps.executeUpdate();
        } catch (SQLException e) {
            // In lỗi nếu có vấn đề khi cập nhật
            e.printStackTrace();
        }
    }
}