package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.ImportHistory;
import util.DBUtil;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImportHistoryDAO {
    
    public List<ImportHistory> getImportHistory() {
        List<ImportHistory> list = new ArrayList<>();
        String sql = "SELECT * FROM importhistory ORDER BY ImportDate DESC";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                ImportHistory history = new ImportHistory(
                    rs.getInt("ImportHistoryID"),
                    rs.getInt("ImportedByID"),
                    rs.getTimestamp("ImportDate"),
                    rs.getInt("PurchaseRequestID"),
                    rs.getInt("ReturnRequestID")
                );
                list.add(history);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ImportHistoryDAO.class.getName()).log(Level.SEVERE, "Error getting import history", ex);
        }
        return list;
    }
    
    public ImportHistory getImportHistoryById(int id) {
        String sql = "SELECT * FROM importhistory WHERE ImportHistoryID = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ImportHistory(
                        rs.getInt("ImportHistoryID"),
                        rs.getInt("ImportedByID"),
                        rs.getTimestamp("ImportDate"),
                        rs.getInt("PurchaseRequestID"),
                        rs.getInt("ReturnRequestID")
                    );
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ImportHistoryDAO.class.getName()).log(Level.SEVERE, "Error getting import history by ID: " + id, ex);
        }
        return null;
    }
    
    public int getFirstMaterialIdFromHistory(int historyId) {
        String sql = "SELECT MaterialID FROM importhistorymaterials WHERE ImportHistoryID = ? LIMIT 1";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, historyId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("MaterialID");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ImportHistoryDAO.class.getName()).log(Level.SEVERE, "Error getting first material ID from import history: " + historyId, ex);
        }
        return -1;
    }
    
    public List<Integer> getAllMaterialIdsFromHistory(int historyId) {
        List<Integer> materialIds = new ArrayList<>();
        String sql = "SELECT MaterialID FROM importhistorymaterials WHERE ImportHistoryID = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, historyId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    materialIds.add(rs.getInt("MaterialID"));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ImportHistoryDAO.class.getName()).log(Level.SEVERE, "Error getting all material IDs from import history: " + historyId, ex);
        }
        return materialIds;
    }
    
    public int getMaterialQuantity(int historyId, int materialId) {
        String sql = "SELECT (UsableQuantity + BrokenQuantity) as TotalQuantity FROM importhistorymaterials WHERE ImportHistoryID = ? AND MaterialID = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, historyId);
            ps.setInt(2, materialId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("TotalQuantity");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ImportHistoryDAO.class.getName()).log(Level.SEVERE, "Error getting material quantity from import history", ex);
        }
        return 0;
    }
} 