package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.PurchaseRequest;
import util.DBUtil;

public class PurchaseRequestDAO {

    public List<PurchaseRequest> getApprovedRequestsWithImportStatus() {
        List<PurchaseRequest> list = new ArrayList<>();
        String sql = "SELECT pr.PurchaseRequestID, pr.CreatedByID, pr.ApprovedByID, pr.CreatedDate, pr.ApprovedDate, pr.Note, pr.Status, " +
                "CASE WHEN ih.PurchaseRequestID IS NOT NULL THEN 1 ELSE 0 END AS IsImported " +
                "FROM purchaserequests pr " +
                "LEFT JOIN importhistory ih ON pr.PurchaseRequestID = ih.PurchaseRequestID " +
                "WHERE pr.Status = 1";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                PurchaseRequest pr = new PurchaseRequest();
                pr.setPurchaseRequestID(rs.getInt("PurchaseRequestID"));
                pr.setCreatedByID(rs.getInt("CreatedByID"));
                pr.setApprovedByID(rs.getObject("ApprovedByID") != null ? rs.getInt("ApprovedByID") : null);
                pr.setCreatedDate(rs.getTimestamp("CreatedDate"));
                pr.setApprovedDate(rs.getTimestamp("ApprovedDate"));
                pr.setNote(rs.getString("Note"));
                pr.setStatus(rs.getByte("Status"));
                list.add(pr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public boolean isImported(int requestId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM importhistory WHERE PurchaseRequestID = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, requestId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
    
    public PurchaseRequest getById(int purchaseRequestId) {
        String sql = "SELECT PurchaseRequestID, CreatedByID, ApprovedByID, CreatedDate, ApprovedDate, Note, Status " +
                "FROM purchaserequests WHERE PurchaseRequestID = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, purchaseRequestId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    PurchaseRequest pr = new PurchaseRequest();
                    pr.setPurchaseRequestID(rs.getInt("PurchaseRequestID"));
                    pr.setCreatedByID(rs.getInt("CreatedByID"));
                    pr.setApprovedByID(rs.getObject("ApprovedByID") != null ? rs.getInt("ApprovedByID") : null);
                    pr.setCreatedDate(rs.getTimestamp("CreatedDate"));
                    pr.setApprovedDate(rs.getTimestamp("ApprovedDate"));
                    pr.setNote(rs.getString("Note"));
                    pr.setStatus(rs.getByte("Status"));
                    return pr;
                }
            }
            System.out.println("getById PurchaseRequest");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateImportStatus(int requestId, int isImported) {
        String sql = "UPDATE purchaserequests SET Status = ? WHERE PurchaseRequestID = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, isImported);
            ps.setInt(2, requestId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
