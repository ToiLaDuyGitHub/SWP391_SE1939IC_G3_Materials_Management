package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.ExportHistory;
import util.DBUtil;
import java.util.logging.Level;
import java.util.logging.Logger;

// Lớp DAO để xử lý các thao tác liên quan đến lịch sử xuất kho
public class ExportHistoryDAO {
    
    // Lấy toàn bộ danh sách lịch sử xuất kho, sắp xếp theo ngày xuất giảm dần
    public List<ExportHistory> getExportHistory() {
        // Tạo danh sách để lưu các bản ghi lịch sử xuất kho
        List<ExportHistory> list = new ArrayList<>();
        // Câu lệnh SQL lấy tất cả bản ghi từ bảng exporthistory
        String sql = "SELECT * FROM exporthistory ORDER BY ExportDate DESC";
        
        // Sử dụng try-with-resources để tự động đóng kết nối và tài nguyên
        try (Connection conn = DBUtil.getConnection(); // Lấy kết nối từ DBUtil
             PreparedStatement ps = conn.prepareStatement(sql); // Chuẩn bị câu lệnh SQL
             ResultSet rs = ps.executeQuery()) { // Thực thi truy vấn và lấy kết quả
            
            // Duyệt qua từng bản ghi trong kết quả
            while (rs.next()) {
                // Tạo đối tượng ExportHistory từ dữ liệu bản ghi
                ExportHistory history = new ExportHistory(
                    rs.getInt("ExportHistoryID"), // ID lịch sử xuất kho
                    rs.getInt("ExportedByID"), // ID người thực hiện xuất kho
                    rs.getTimestamp("ExportDate"), // Ngày giờ xuất kho
                    rs.getInt("ExportRequestID"), // ID yêu cầu xuất kho
                    rs.getInt("RepairRequestID") // ID yêu cầu sửa chữa
                );
                // Thêm đối tượng vào danh sách
                list.add(history);
            }
        } catch (SQLException ex) {
            // Ghi log lỗi nếu có vấn đề khi truy vấn cơ sở dữ liệu
            Logger.getLogger(ExportHistoryDAO.class.getName()).log(Level.SEVERE, "Lỗi khi lấy danh sách lịch sử xuất kho", ex);
        }
        // Trả về danh sách lịch sử xuất kho
        return list;
    }
    
    // Lấy thông tin lịch sử xuất kho theo ID
    public ExportHistory getExportHistoryById(int id) {
        // Câu lệnh SQL lấy bản ghi theo ExportHistoryID
        String sql = "SELECT * FROM exporthistory WHERE ExportHistoryID = ?";
        
        try (Connection conn = DBUtil.getConnection(); // Lấy kết nối
             PreparedStatement ps = conn.prepareStatement(sql)) { // Chuẩn bị câu lệnh
            
            // Gán giá trị ID vào tham số trong câu lệnh SQL
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { // Thực thi truy vấn
                if (rs.next()) {
                    // Nếu tìm thấy bản ghi, tạo và trả về đối tượng ExportHistory
                    return new ExportHistory(
                        rs.getInt("ExportHistoryID"), // ID lịch sử xuất kho
                        rs.getInt("ExportedByID"), // ID người xuất kho
                        rs.getTimestamp("ExportDate"), // Ngày giờ xuất kho
                        rs.getInt("ExportRequestID"), // ID yêu cầu xuất kho
                        rs.getInt("RepairRequestID") // ID yêu cầu sửa chữa
                    );
                }
            }
        } catch (SQLException ex) {
            // Ghi log lỗi nếu có vấn đề khi truy vấn
            Logger.getLogger(ExportHistoryDAO.class.getName()).log(Level.SEVERE, "Lỗi khi lấy lịch sử xuất kho theo ID: " + id, ex);
        }
        // Trả về null nếu không tìm thấy hoặc có lỗi
        return null;
    }
    
    // Lấy ID vật tư đầu tiên liên quan đến một bản ghi lịch sử xuất kho
    public int getFirstMaterialIdFromHistory(int historyId) {
        // Câu lệnh SQL lấy MaterialID đầu tiên từ bảng exporthistorymaterials
        String sql = "SELECT MaterialID FROM exporthistorymaterials WHERE ExportHistoryID = ? LIMIT 1";
        
        try (Connection conn = DBUtil.getConnection(); // Lấy kết nối
             PreparedStatement ps = conn.prepareStatement(sql)) { // Chuẩn bị câu lệnh
            
            // Gán giá trị ExportHistoryID vào tham số
            ps.setInt(1, historyId);
            try (ResultSet rs = ps.executeQuery()) { // Thực thi truy vấn
                if (rs.next()) {
                    // Nếu tìm thấy, trả về ID vật tư
                    return rs.getInt("MaterialID");
                }
            }
        } catch (SQLException ex) {
            // Ghi log lỗi nếu có vấn đề khi truy vấn
            Logger.getLogger(ExportHistoryDAO.class.getName()).log(Level.SEVERE, "Lỗi khi lấy ID vật tư đầu tiên từ lịch sử xuất kho: " + historyId, ex);
        }
        // Trả về -1 nếu không tìm thấy hoặc có lỗi
        return -1;
    }
    
    // Lấy danh sách tất cả ID vật tư liên quan đến một bản ghi lịch sử xuất kho
    public List<Integer> getAllMaterialIdsFromHistory(int historyId) {
        // Tạo danh sách để lưu các ID vật tư
        List<Integer> materialIds = new ArrayList<>();
        // Câu lệnh SQL lấy tất cả MaterialID từ bảng exporthistorymaterials
        String sql = "SELECT MaterialID FROM exporthistorymaterials WHERE ExportHistoryID = ?";
        
        try (Connection conn = DBUtil.getConnection(); // Lấy kết nối
             PreparedStatement ps = conn.prepareStatement(sql)) { // Chuẩn bị câu lệnh
            
            // Gán giá trị ExportHistoryID vào tham số
            ps.setInt(1, historyId);
            try (ResultSet rs = ps.executeQuery()) { // Thực thi truy vấn
                // Duyệt qua từng bản ghi và thêm ID vật tư vào danh sách
                while (rs.next()) {
                    materialIds.add(rs.getInt("MaterialID"));
                }
            }
        } catch (SQLException ex) {
            // Ghi log lỗi nếu có vấn đề khi truy vấn
            Logger.getLogger(ExportHistoryDAO.class.getName()).log(Level.SEVERE, "Lỗi khi lấy danh sách ID vật tư từ lịch sử xuất kho: " + historyId, ex);
        }
        // Trả về danh sách ID vật tư
        return materialIds;
    }
    
    // Lấy tổng số lượng vật tư (bao gồm cả vật tư sử dụng được và hỏng) từ lịch sử xuất kho
    public int getMaterialQuantity(int historyId, int materialId) {
        // Câu lệnh SQL tính tổng số lượng (UsableQuantity + BrokenQuantity)
        String sql = "SELECT (UsableQuantity + BrokenQuantity) as TotalQuantity FROM exporthistorymaterials WHERE ExportHistoryID = ? AND MaterialID = ?";
        
        try (Connection conn = DBUtil.getConnection(); // Lấy kết nối
             PreparedStatement ps = conn.prepareStatement(sql)) { // Chuẩn bị câu lệnh
            
            // Gán giá trị ExportHistoryID và MaterialID vào tham số
            ps.setInt(1, historyId);
            ps.setInt(2, materialId);
            try (ResultSet rs = ps.executeQuery()) { // Thực thi truy vấn
                if (rs.next()) {
                    // Trả về tổng số lượng vật tư
                    return rs.getInt("TotalQuantity");
                }
            }
        } catch (SQLException ex) {
            // Ghi log lỗi nếu có vấn đề khi truy vấn
            Logger.getLogger(ExportHistoryDAO.class.getName()).log(Level.SEVERE, "Lỗi khi lấy số lượng vật tư từ lịch sử xuất kho", ex);
        }
        // Trả về 0 nếu không tìm thấy hoặc có lỗi
        return 0;
    }
}