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

public class ExportHistoryDAO {
    
    public List<ExportHistory> getExportHistory() {
        List<ExportHistory> list = new ArrayList<>();
        String sql = "SELECT * FROM exporthistory ORDER BY ExportDate DESC";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                ExportHistory history = new ExportHistory(
                    rs.getInt("ExportHistoryID"),
                    rs.getInt("ExportedByID"),
                    rs.getTimestamp("ExportDate"),
                    rs.getInt("ExportRequestID"),
                    rs.getInt("RepairRequestID")
                );
                list.add(history);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ExportHistoryDAO.class.getName()).log(Level.SEVERE, "Error getting export history", ex);
        }
        return list;
    }
    
    public ExportHistory getExportHistoryById(int id) {
        String sql = "SELECT * FROM exporthistory WHERE ExportHistoryID = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ExportHistory(
                        rs.getInt("ExportHistoryID"),
                        rs.getInt("ExportedByID"),
                        rs.getTimestamp("ExportDate"),
                        rs.getInt("ExportRequestID"),
                        rs.getInt("RepairRequestID")
                    );
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ExportHistoryDAO.class.getName()).log(Level.SEVERE, "Error getting export history by ID: " + id, ex);
        }
        return null;
    }
    
    public int getFirstMaterialIdFromHistory(int historyId) {
        String sql = "SELECT MaterialID FROM exporthistorymaterials WHERE ExportHistoryID = ? LIMIT 1";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, historyId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("MaterialID");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ExportHistoryDAO.class.getName()).log(Level.SEVERE, "Error getting first material ID from export history: " + historyId, ex);
        }
        return -1;
    }
    
    public List<Integer> getAllMaterialIdsFromHistory(int historyId) {
        List<Integer> materialIds = new ArrayList<>();
        String sql = "SELECT MaterialID FROM exporthistorymaterials WHERE ExportHistoryID = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, historyId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    materialIds.add(rs.getInt("MaterialID"));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ExportHistoryDAO.class.getName()).log(Level.SEVERE, "Error getting all material IDs from export history: " + historyId, ex);
        }
        return materialIds;
    }
    
    public int getMaterialQuantity(int historyId, int materialId) {
        String sql = "SELECT (UsableQuantity + BrokenQuantity) as TotalQuantity FROM exporthistorymaterials WHERE ExportHistoryID = ? AND MaterialID = ?";
        
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
            Logger.getLogger(ExportHistoryDAO.class.getName()).log(Level.SEVERE, "Error getting material quantity from export history", ex);
        }
        return 0;
    }
} 