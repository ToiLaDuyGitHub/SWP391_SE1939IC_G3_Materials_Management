/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.ExportRequest;
import model.ExportRequestMaterial;
import model.dto.RequestDTO;
import model.dto.RequestMaterialDTO;
import util.DBUtil;

/**
 *
 * @author ADMIN
 */
public class RequestDAO {

    public List<RequestDTO> getAllRequests() throws Exception {
        List<RequestDTO> requests = new ArrayList<>();
        String sql = "SELECT \n"
                + "    'Export' AS request_type,\n"
                + "    er.ExportRequestID AS request_id,\n"
                + "    er.RequestCode AS request_code,\n"
                + "    er.CreatedDate AS created_date,\n"
                + "    CONCAT(uc.LastName, ' ', uc.FirstName) AS created_by_name, \n"
                + "    CONCAT(ua.LastName, ' ', ua.FirstName) AS approved_by_name, \n"
                + "    er.Status AS status\n"
                + "FROM exportrequests er\n"
                + "LEFT JOIN users uc ON er.CreatedByID = uc.UserID\n"
                + "LEFT JOIN users ua ON er.ApprovedByID = ua.UserID\n"
                + "UNION\n"
                + "SELECT \n"
                + "    'Import' AS request_type,\n"
                + "    ih.ImportHistoryID AS request_id,\n"
                + "    ih.RequestCode AS request_code,\n"
                + "    ih.ImportDate AS created_date,\n"
                + "    CONCAT(uc.LastName, ' ', uc.FirstName) AS created_by_name,\n"
                + "    NULL AS approved_by_name,\n"
                + "    1 AS status\n"
                + "FROM importhistory ih\n"
                + "LEFT JOIN users uc ON ih.ImportedByID = uc.UserID\n"
                + "UNION\n"
                + "SELECT \n"
                + "    'Repair' AS request_type,\n"
                + "    rr.RepairRequestID AS request_id,\n"
                + "    rr.RequestCode AS request_code,\n"
                + "    rr.CreatedDate AS created_date,\n"
                + "    CONCAT(uc.LastName, ' ', uc.FirstName) AS created_by_name,\n"
                + "    CONCAT(ua.LastName, ' ', ua.FirstName) AS approved_by_name, \n"
                + "    rr.Status AS status\n"
                + "FROM repairrequests rr\n"
                + "LEFT JOIN users uc ON rr.CreatedByID = uc.UserID\n"
                + "LEFT JOIN users ua ON rr.ApprovedByID = ua.UserID\n"
                + "UNION\n"
                + "SELECT \n"
                + "    'Return' AS request_type,\n"
                + "    rr.ReturnRequestID AS request_id,\n"
                + "    rr.RequestCode AS request_code,\n"
                + "    rr.CreatedDate AS created_date,\n"
                + "    CONCAT(uc.LastName, ' ', uc.FirstName) AS created_by_name, \n"
                + "    CONCAT(ua.LastName, ' ', ua.FirstName) AS approved_by_name, \n"
                + "    rr.Status AS status\n"
                + "FROM returnrequests rr\n"
                + "LEFT JOIN users uc ON rr.CreatedByID = uc.UserID\n"
                + "LEFT JOIN users ua ON rr.InputByID = ua.UserID\n"
                + "UNION\n"
                + "SELECT \n"
                + "    'Purchase' AS request_type,\n"
                + "    pr.PurchaseRequestID AS request_id,\n"
                + "    pr.RequestCode AS request_code,\n"
                + "    pr.CreatedDate AS created_date,\n"
                + "    CONCAT(uc.LastName, ' ', uc.FirstName) AS created_by_name, \n"
                + "    CONCAT(ua.LastName, ' ', ua.FirstName) AS approved_by_name, \n"
                + "    pr.Status AS status\n"
                + "FROM purchaserequests pr\n"
                + "LEFT JOIN users uc ON pr.CreatedByID = uc.UserID\n"
                + "LEFT JOIN users ua ON pr.ApprovedByID = ua.UserID\n"
                + "ORDER BY created_date DESC";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                RequestDTO request = new RequestDTO();
                request.setRequestType(rs.getString("request_type"));
                request.setRequestId(rs.getInt("request_id"));
                request.setRequestCode(rs.getString("request_code"));
                request.setCreatedDate(rs.getTimestamp("created_date"));
                request.setCreatedByName(rs.getString("created_by_name"));
                request.setApprovedByName(rs.getString("approved_by_name"));
                request.setStatus(rs.getInt("status"));

                String statusText;
                switch (rs.getInt("status")) {
                    case 0:
                        statusText = "Chưa duyệt";
                        break;
                    case 1:
                        statusText = "Đã duyệt";
                        break;
                    case 2:
                        statusText = "Từ chối";
                        break;
                    default:
                        statusText = "Không xác định";
                }
                request.setStatusText(statusText);

                requests.add(request);
            }
        }
        return requests;
    }
    // Lấy chi tiết yêu cầu và danh sách vật tư

    public RequestDTO getRequestDetail(int requestId, String requestType) throws SQLException {
        RequestDTO request = new RequestDTO();
        List<RequestMaterialDTO> materials = new ArrayList<>();
        String sqlRequest = "";
        String sqlMaterials = "";

        // Xác định bảng và cột dựa trên loại yêu cầu
        switch (requestType.toLowerCase()) {
            case "export":
                sqlRequest = "SELECT er.ExportRequestID AS request_id, er.RequestCode AS request_code, "
                        + "'Export' AS request_type, er.CreatedDate AS created_date, "
                        + "CONCAT(uc.LastName, ' ', uc.FirstName) AS created_by_name, "
                        + "CONCAT(ua.LastName, ' ', ua.FirstName) AS approved_by_name, "
                        + "er.Status AS status "
                        + "FROM exportrequests er "
                        + "LEFT JOIN users uc ON er.CreatedByID = uc.UserID "
                        + "LEFT JOIN users ua ON er.ApprovedByID = ua.UserID "
                        + "WHERE er.ExportRequestID = ?";
                sqlMaterials = "SELECT m.MaterialID, m.MaterialName, erm.Quantity "
                        + "FROM exportrequestmaterials erm "
                        + "JOIN materials m ON erm.MaterialID = m.MaterialID "
                        + "WHERE erm.ExportRequestID = ?";
                break;
            case "import":
                sqlRequest = "SELECT ih.ImportHistoryID AS request_id, ih.RequestCode AS request_code, "
                        + "'Import' AS request_type, ih.ImportDate AS created_date, "
                        + "CONCAT(uc.LastName, ' ', uc.FirstName) AS created_by_name, "
                        + "NULL AS approved_by_name, 1 AS status "
                        + "FROM importhistory ih "
                        + "LEFT JOIN users uc ON ih.ImportedByID = uc.UserID "
                        + "WHERE ih.ImportHistoryID = ?";
                sqlMaterials = "SELECT m.MaterialID, m.MaterialName, ihm.UsableQuantity AS Quantity "
                        + "FROM importhistorymaterials ihm "
                        + "JOIN materials m ON ihm.MaterialID = m.MaterialID "
                        + "WHERE ihm.ImportHistoryID = ?";
                break;
            case "repair":
                sqlRequest = "SELECT rr.RepairRequestID AS request_id, rr.RequestCode AS request_code, "
                        + "'Repair' AS request_type, rr.CreatedDate AS created_date, "
                        + "CONCAT(uc.LastName, ' ', uc.FirstName) AS created_by_name, "
                        + "CONCAT(ua.LastName, ' ', ua.FirstName) AS approved_by_name, "
                        + "rr.Status AS status "
                        + "FROM repairrequests rr "
                        + "LEFT JOIN users uc ON rr.CreatedByID = uc.UserID "
                        + "LEFT JOIN users ua ON rr.ApprovedByID = ua.UserID "
                        + "WHERE rr.RepairRequestID = ?";
                sqlMaterials = "SELECT m.MaterialID, m.MaterialName, rrm.Quantity "
                        + "FROM repairrequestmaterials rrm "
                        + "JOIN materials m ON rrm.MaterialID = m.MaterialID "
                        + "WHERE rrm.RepairRequestID = ?";
                break;
            case "return":
                sqlRequest = "SELECT rr.ReturnRequestID AS request_id, rr.RequestCode AS request_code, "
                        + "'Return' AS request_type, rr.CreatedDate AS created_date, "
                        + "CONCAT(uc.LastName, ' ', uc.FirstName) AS created_by_name, "
                        + "CONCAT(ua.LastName, ' ', ua.FirstName) AS approved_by_name, "
                        + "rr.Status AS status "
                        + "FROM returnrequests rr "
                        + "LEFT JOIN users uc ON rr.CreatedByID = uc.UserID "
                        + "LEFT JOIN users ua ON rr.InputByID = ua.UserID "
                        + "WHERE rr.ReturnRequestID = ?";
                sqlMaterials = "SELECT m.MaterialID, m.MaterialName, rrm.Quantity "
                        + "FROM returnrequestmaterials rrm "
                        + "JOIN materials m ON rrm.MaterialID = m.MaterialID "
                        + "WHERE rrm.ReturnRequestID = ?";
                break;
            case "purchase":
                sqlRequest = "SELECT pr.PurchaseRequestID AS request_id, pr.RequestCode AS request_code, "
                        + "'Purchase' AS request_type, pr.CreatedDate AS created_date, "
                        + "CONCAT(uc.LastName, ' ', uc.FirstName) AS created_by_name, "
                        + "CONCAT(ua.LastName, ' ', ua.FirstName) AS approved_by_name, "
                        + "pr.Status AS status "
                        + "FROM purchaserequests pr "
                        + "LEFT JOIN users uc ON pr.CreatedByID = uc.UserID "
                        + "LEFT JOIN users ua ON pr.ApprovedByID = ua.UserID "
                        + "WHERE pr.PurchaseRequestID = ?";
                sqlMaterials = "SELECT m.MaterialID, m.MaterialName, prm.Quantity "
                        + "FROM purchaserequestmaterials prm "
                        + "JOIN materials m ON prm.MaterialID = m.MaterialID "
                        + "WHERE prm.PurchaseRequestID = ?";
                break;
            default:
                throw new SQLException("Loại yêu cầu không hợp lệ: " + requestType);
        }

        // Lấy thông tin chi tiết yêu cầu
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sqlRequest)) {
            ps.setInt(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    request.setRequestId(rs.getInt("request_id"));
                    request.setRequestCode(rs.getString("request_code"));
                    request.setRequestType(rs.getString("request_type"));
                    request.setCreatedDate(rs.getTimestamp("created_date"));
                    request.setCreatedByName(rs.getString("created_by_name"));
                    request.setApprovedByName(rs.getString("approved_by_name"));
                    request.setStatus(rs.getInt("status"));

                    String statusText;
                    switch (rs.getInt("status")) {
                        case 0:
                            statusText = "Chưa duyệt";
                            break;
                        case 1:
                            statusText = "Đã duyệt";
                            break;
                        case 2:
                            statusText = "Từ chối";
                            break;
                        default:
                            statusText = "Không xác định";
                    }
                    request.setStatusText(statusText);
                } else {
                    throw new SQLException("Không tìm thấy yêu cầu với ID: " + requestId);
                }
            }
        }

        // Lấy danh sách vật tư
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sqlMaterials)) {
            ps.setInt(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RequestMaterialDTO material = new RequestMaterialDTO();
                    material.setMaterialId(rs.getInt("MaterialID"));
                    material.setMaterialName(rs.getString("MaterialName"));
                    material.setQuantity(rs.getInt("Quantity"));
                    materials.add(material);
                }
            }
        }

        request.setMaterials(materials);
        return request;
    }

    // Cập nhật trạng thái yêu cầu
    public void updateRequestStatus(int requestId, String requestType, int status) throws SQLException {
        String sql = "";
        String idColumn = "";
        switch (requestType.toLowerCase()) {
            case "export":
                sql = "UPDATE exportrequests SET Status = ?, ApprovedDate = CURRENT_TIMESTAMP WHERE ExportRequestID = ?";
                idColumn = "ExportRequestID";
                break;
            case "purchase":
                sql = "UPDATE purchaserequests SET Status = ?, ApprovedDate = CURRENT_TIMESTAMP WHERE PurchaseRequestID = ?";
                idColumn = "PurchaseRequestID";
                break;
            case "repair":
                sql = "UPDATE repairrequests SET Status = ?, ApprovedDate = CURRENT_TIMESTAMP WHERE RepairRequestID = ?";
                idColumn = "RepairRequestID";
                break;
            case "return":
                sql = "UPDATE returnrequests SET Status = ?, InputByID = ?, ApprovedDate = CURRENT_TIMESTAMP WHERE ReturnRequestID = ?";
                idColumn = "ReturnRequestID";
                break;
            default:
                throw new SQLException("Loại yêu cầu không hợp lệ: " + requestType);
        }

        try (Connection conn = DBUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, status);
            stmt.setInt(2, requestId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Không tìm thấy yêu cầu với " + idColumn + ": " + requestId);
            }
        }
    }

    public int getRequestIdFromCode(String requestCode, String requestType) throws SQLException {
        String sql = "";
        switch (requestType.toLowerCase()) {
            case "export":
                sql = "SELECT ExportRequestID AS request_id FROM exportrequests WHERE RequestCode = ?";
                break;
            case "purchase":
                sql = "SELECT PurchaseRequestID AS request_id FROM purchaserequests WHERE RequestCode = ?";
                break;
            case "repair":
                sql = "SELECT RepairRequestID AS request_id FROM repairrequests WHERE RequestCode = ?";
                break;
            case "return":
                sql = "SELECT ReturnRequestID AS request_id FROM returnrequests WHERE RequestCode = ?";
                break;
            default:
                return -1;
        }
        try (Connection conn = DBUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, requestCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("request_id");
                }
            }
        }
        return -1;
    }


    // Tìm UserID của giám đốc (RoleID = 3)
    public int getDirectorUserID() throws SQLException {
        String sql = "SELECT UserID FROM users WHERE RoleID = 3 LIMIT 1";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("UserID");
            }
        }
        return -1; // Trả về -1 nếu không tìm thấy giám đốc
    }

    // Lưu thông tin đơn xuất kho vào bảng exportrequests
    public int createExportRequest(ExportRequest request) throws SQLException {
        String sql = "INSERT INTO exportrequests (CreatedByID, ApprovedByID, Note, Status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            int directorID = getDirectorUserID();
            if (directorID == -1) {
                throw new SQLException("Không tìm thấy giám đốc để gán người duyệt.");
            }
            stmt.setInt(1, request.getCreatedByID());
            stmt.setInt(2, directorID);
            stmt.setString(3, request.getNote());
            stmt.setInt(4, request.getStatus());
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Rows affected in exportrequests: " + rowsAffected);
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi tạo ExportRequest: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return -1;
    }

    // Lưu danh sách vật tư và số lượng vào bảng exportrequestmaterials
    public void saveExportRequestMaterials(List<ExportRequestMaterial> materials) throws SQLException {
        String sql = "INSERT INTO exportrequestmaterials (ExportRequestID, MaterialID, Quantity) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (ExportRequestMaterial material : materials) {
                stmt.setInt(1, material.getExportRequestID());
                stmt.setInt(2, material.getMaterialID());
                stmt.setInt(3, material.getQuantity());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }
    
     // Phương thức mới để lấy yêu cầu theo UserID của người tạo, bao gồm vật tư
    public List<RequestDTO> getRequestsByCreator(int userId) throws Exception {
        List<RequestDTO> requests = new ArrayList<>();
        String sql = "SELECT \n"
                + "    'Export' AS request_type,\n"
                + "    er.ExportRequestID AS request_id,\n"
                + "    er.RequestCode AS request_code,\n"
                + "    er.CreatedDate AS created_date,\n"
                + "    CONCAT(uc.LastName, ' ', uc.FirstName) AS created_by_name, \n"
                + "    CONCAT(ua.LastName, ' ', ua.FirstName) AS approved_by_name, \n"
                + "    er.Status AS status\n"
                + "FROM exportrequests er\n"
                + "LEFT JOIN users uc ON er.CreatedByID = uc.UserID\n"
                + "LEFT JOIN users ua ON er.ApprovedByID = ua.UserID\n"
                + "WHERE er.CreatedByID = ?\n"
                + "UNION\n"
                + "SELECT \n"
                + "    'Import' AS request_type,\n"
                + "    ih.ImportHistoryID AS request_id,\n"
                + "    ih.RequestCode AS request_code,\n"
                + "    ih.ImportDate AS created_date,\n"
                + "    CONCAT(uc.LastName, ' ', uc.FirstName) AS created_by_name,\n"
                + "    NULL AS approved_by_name,\n"
                + "    1 AS status\n"
                + "FROM importhistory ih\n"
                + "LEFT JOIN users uc ON ih.ImportedByID = uc.UserID\n"
                + "WHERE ih.ImportedByID = ?\n"
                + "UNION\n"
                + "SELECT \n"
                + "    'Repair' AS request_type,\n"
                + "    rr.RepairRequestID AS request_id,\n"
                + "    rr.RequestCode AS request_code,\n"
                + "    rr.CreatedDate AS created_date,\n"
                + "    CONCAT(uc.LastName, ' ', uc.FirstName) AS created_by_name,\n"
                + "    CONCAT(ua.LastName, ' ', ua.FirstName) AS approved_by_name, \n"
                + "    rr.Status AS status\n"
                + "FROM repairrequests rr\n"
                + "LEFT JOIN users uc ON rr.CreatedByID = uc.UserID\n"
                + "LEFT JOIN users ua ON rr.ApprovedByID = ua.UserID\n"
                + "WHERE rr.CreatedByID = ?\n"
                + "UNION\n"
                + "SELECT \n"
                + "    'Return' AS request_type,\n"
                + "    rr.ReturnRequestID AS request_id,\n"
                + "    rr.RequestCode AS request_code,\n"
                + "    rr.CreatedDate AS created_date,\n"
                + "    CONCAT(uc.LastName, ' ', uc.FirstName) AS created_by_name, \n"
                + "    CONCAT(ua.LastName, ' ', ua.FirstName) AS approved_by_name, \n"
                + "    rr.Status AS status\n"
                + "FROM returnrequests rr\n"
                + "LEFT JOIN users uc ON rr.CreatedByID = uc.UserID\n"
                + "LEFT JOIN users ua ON rr.InputByID = ua.UserID\n"
                + "WHERE rr.CreatedByID = ?\n"
                + "UNION\n"
                + "SELECT \n"
                + "    'Purchase' AS request_type,\n"
                + "    pr.PurchaseRequestID AS request_id,\n"
                + "    pr.RequestCode AS request_code,\n"
                + "    pr.CreatedDate AS created_date,\n"
                + "    CONCAT(uc.LastName, ' ', uc.FirstName) AS created_by_name, \n"
                + "    CONCAT(ua.LastName, ' ', ua.FirstName) AS approved_by_name, \n"
                + "    pr.Status AS status\n"
                + "FROM purchaserequests pr\n"
                + "LEFT JOIN users uc ON pr.CreatedByID = uc.UserID\n"
                + "LEFT JOIN users ua ON pr.ApprovedByID = ua.UserID\n"
                + "WHERE pr.CreatedByID = ?\n"
                + "ORDER BY created_date DESC";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            // Gán userId cho các điều kiện WHERE
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            ps.setInt(3, userId);
            ps.setInt(4, userId);
            ps.setInt(5, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RequestDTO request = new RequestDTO();
                    request.setRequestType(rs.getString("request_type"));
                    request.setRequestId(rs.getInt("request_id"));
                    request.setRequestCode(rs.getString("request_code"));
                    request.setCreatedDate(rs.getTimestamp("created_date"));
                    request.setCreatedByName(rs.getString("created_by_name"));
                    request.setApprovedByName(rs.getString("approved_by_name"));
                    request.setStatus(rs.getInt("status"));

                    String statusText;
                    switch (rs.getInt("status")) {
                        case 0:
                            statusText = "Chưa duyệt";
                            break;
                        case 1:
                            statusText = "Đã duyệt";
                            break;
                        case 2:
                            statusText = "Từ chối";
                            break;
                        default:
                            statusText = "Không xác định";
                    }
                    request.setStatusText(statusText);

                    // Lấy danh sách vật tư cho yêu cầu
                    RequestDTO detailedRequest = getRequestDetail(rs.getInt("request_id"), rs.getString("request_type"));
                    request.setMaterials(detailedRequest.getMaterials());

                    requests.add(request);
                }
            }
        }
        return requests;
    }

    
    public List<RequestDTO> searchRequests(String requestCode, java.sql.Date startDate, java.sql.Date endDate, int userId) throws Exception {
        List<RequestDTO> requests = new ArrayList<>();
        String sql = "SELECT \n"
                + "    'Export' AS request_type,\n"
                + "    er.ExportRequestID AS request_id,\n"
                + "    er.RequestCode AS request_code,\n"
                + "    er.CreatedDate AS created_date,\n"
                + "    CONCAT(uc.LastName, ' ', uc.FirstName) AS created_by_name, \n"
                + "    CONCAT(ua.LastName, ' ', ua.FirstName) AS approved_by_name, \n"
                + "    er.Status AS status\n"
                + "FROM exportrequests er\n"
                + "LEFT JOIN users uc ON er.CreatedByID = uc.UserID\n"
                + "LEFT JOIN users ua ON er.ApprovedByID = ua.UserID\n"
                + "WHERE (? IS NULL OR UPPER(er.RequestCode) LIKE UPPER(?))\n"
                + "AND (? IS NULL OR DATE(er.CreatedDate) >= ?)\n"
                + "AND (? IS NULL OR DATE(er.CreatedDate) <= ?)\n"
                + "AND er.CreatedByID = ?\n"
                + "UNION\n"
                + "SELECT \n"
                + "    'Import' AS request_type,\n"
                + "    ih.ImportHistoryID AS request_id,\n"
                + "    ih.RequestCode AS request_code,\n"
                + "    ih.ImportDate AS created_date,\n"
                + "    CONCAT(uc.LastName, ' ', uc.FirstName) AS created_by_name,\n"
                + "    NULL AS approved_by_name,\n"
                + "    1 AS status\n"
                + "FROM importhistory ih\n"
                + "LEFT JOIN users uc ON ih.ImportedByID = uc.UserID\n"
                + "WHERE (? IS NULL OR UPPER(ih.RequestCode) LIKE UPPER(?))\n"
                + "AND (? IS NULL OR DATE(ih.ImportDate) >= ?)\n"
                + "AND (? IS NULL OR DATE(ih.ImportDate) <= ?)\n"
                + "AND ih.ImportedByID = ?\n"
                + "UNION\n"
                + "SELECT \n"
                + "    'Repair' AS request_type,\n"
                + "    rr.RepairRequestID AS request_id,\n"
                + "    rr.RequestCode AS request_code,\n"
                + "    rr.CreatedDate AS created_date,\n"
                + "    CONCAT(uc.LastName, ' ', uc.FirstName) AS created_by_name,\n"
                + "    CONCAT(ua.LastName, ' ', ua.FirstName) AS approved_by_name, \n"
                + "    rr.Status AS status\n"
                + "FROM repairrequests rr\n"
                + "LEFT JOIN users uc ON rr.CreatedByID = uc.UserID\n"
                + "LEFT JOIN users ua ON rr.ApprovedByID = ua.UserID\n"
                + "WHERE (? IS NULL OR UPPER(rr.RequestCode) LIKE UPPER(?))\n"
                + "AND (? IS NULL OR DATE(rr.CreatedDate) >= ?)\n"
                + "AND (? IS NULL OR DATE(rr.CreatedDate) <= ?)\n"
                + "AND rr.CreatedByID = ?\n"
                + "UNION\n"
                + "SELECT \n"
                + "    'Return' AS request_type,\n"
                + "    rr.ReturnRequestID AS request_id,\n"
                + "    rr.RequestCode AS request_code,\n"
                + "    rr.CreatedDate AS created_date,\n"
                + "    CONCAT(uc.LastName, ' ', uc.FirstName) AS created_by_name, \n"
                + "    CONCAT(ua.LastName, ' ', ua.FirstName) AS approved_by_name, \n"
                + "    rr.Status AS status\n"
                + "FROM returnrequests rr\n"
                + "LEFT JOIN users uc ON rr.CreatedByID = uc.UserID\n"
                + "LEFT JOIN users ua ON rr.InputByID = ua.UserID\n"
                + "WHERE (? IS NULL OR UPPER(rr.RequestCode) LIKE UPPER(?))\n"
                + "AND (? IS NULL OR DATE(rr.CreatedDate) >= ?)\n"
                + "AND (? IS NULL OR DATE(rr.CreatedDate) <= ?)\n"
                + "AND rr.CreatedByID = ?\n"
                + "UNION\n"
                + "SELECT \n"
                + "    'Purchase' AS request_type,\n"
                + "    pr.PurchaseRequestID AS request_id,\n"
                + "    pr.RequestCode AS request_code,\n"
                + "    pr.CreatedDate AS created_date,\n"
                + "    CONCAT(uc.LastName, ' ', uc.FirstName) AS created_by_name, \n"
                + "    CONCAT(ua.LastName, ' ', ua.FirstName) AS approved_by_name, \n"
                + "    pr.Status AS status\n"
                + "FROM purchaserequests pr\n"
                + "LEFT JOIN users uc ON pr.CreatedByID = uc.UserID\n"
                + "LEFT JOIN users ua ON pr.ApprovedByID = ua.UserID\n"
                + "WHERE (? IS NULL OR UPPER(pr.RequestCode) LIKE UPPER(?))\n"
                + "AND (? IS NULL OR DATE(pr.CreatedDate) >= ?)\n"
                + "AND (? IS NULL OR DATE(pr.CreatedDate) <= ?)\n"
                + "AND pr.CreatedByID = ?\n"
                + "ORDER BY created_date DESC";

        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            System.out.println("SQL Query: " + sql);
            System.out.println("Parameters: requestCode=" + requestCode + ", startDate=" + startDate + ", endDate=" + endDate + ", userId=" + userId);

            String likePattern = requestCode != null && !requestCode.isEmpty() ? "%" + requestCode + "%" : null;
            int paramIndex = 1;

            // Export
            ps.setString(paramIndex++, requestCode != null && !requestCode.isEmpty() ? requestCode : null);
            ps.setString(paramIndex++, likePattern);
            ps.setDate(paramIndex++, startDate);
            ps.setDate(paramIndex++, startDate);
            ps.setDate(paramIndex++, endDate);
            ps.setDate(paramIndex++, endDate);
            ps.setInt(paramIndex++, userId);

            // Import
            ps.setString(paramIndex++, requestCode != null && !requestCode.isEmpty() ? requestCode : null);
            ps.setString(paramIndex++, likePattern);
            ps.setDate(paramIndex++, startDate);
            ps.setDate(paramIndex++, startDate);
            ps.setDate(paramIndex++, endDate);
            ps.setDate(paramIndex++, endDate);
            ps.setInt(paramIndex++, userId);

            // Repair
            ps.setString(paramIndex++, requestCode != null && !requestCode.isEmpty() ? requestCode : null);
            ps.setString(paramIndex++, likePattern);
            ps.setDate(paramIndex++, startDate);
            ps.setDate(paramIndex++, startDate);
            ps.setDate(paramIndex++, endDate);
            ps.setDate(paramIndex++, endDate);
            ps.setInt(paramIndex++, userId);

            // Return
            ps.setString(paramIndex++, requestCode != null && !requestCode.isEmpty() ? requestCode : null);
            ps.setString(paramIndex++, likePattern);
            ps.setDate(paramIndex++, startDate);
            ps.setDate(paramIndex++, startDate);
            ps.setDate(paramIndex++, endDate);
            ps.setDate(paramIndex++, endDate);
            ps.setInt(paramIndex++, userId);

            // Purchase
            ps.setString(paramIndex++, requestCode != null && !requestCode.isEmpty() ? requestCode : null);
            ps.setString(paramIndex++, likePattern);
            ps.setDate(paramIndex++, startDate);
            ps.setDate(paramIndex++, startDate);
            ps.setDate(paramIndex++, endDate);
            ps.setDate(paramIndex++, endDate);
            ps.setInt(paramIndex++, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RequestDTO request = new RequestDTO();
                    request.setRequestType(rs.getString("request_type"));
                    request.setRequestId(rs.getInt("request_id"));
                    request.setRequestCode(rs.getString("request_code"));
                    request.setCreatedDate(rs.getTimestamp("created_date"));
                    request.setCreatedByName(rs.getString("created_by_name"));
                    request.setApprovedByName(rs.getString("approved_by_name"));
                    request.setStatus(rs.getInt("status"));

                    String statusText;
                    switch (rs.getInt("status")) {
                        case 0:
                            statusText = "Chưa duyệt";
                            break;
                        case 1:
                            statusText = "Đã duyệt";
                            break;
                        case 2:
                            statusText = "Từ chối";
                            break;
                        default:
                            statusText = "Không xác định";
                    }
                    request.setStatusText(statusText);

                    RequestDTO detailedRequest = getRequestDetail(rs.getInt("request_id"), rs.getString("request_type"));
                    request.setMaterials(detailedRequest.getMaterials());

                    requests.add(request);
                }
            }
        }
        return requests;
    }
    
    public List<RequestDTO> searchDirectorRequests(String requestCode, java.sql.Date startDate, java.sql.Date endDate, Integer createdById) throws Exception {
    List<RequestDTO> requests = new ArrayList<>();
    String sql = "SELECT \n"
            + "    'Export' AS request_type,\n"
            + "    er.ExportRequestID AS request_id,\n"
            + "    er.RequestCode AS request_code,\n"
            + "    er.CreatedDate AS created_date,\n"
            + "    CONCAT(uc.LastName, ' ', uc.FirstName) AS created_by_name, \n"
            + "    CONCAT(ua.LastName, ' ', ua.FirstName) AS approved_by_name, \n"
            + "    er.Status AS status\n"
            + "FROM exportrequests er\n"
            + "LEFT JOIN users uc ON er.CreatedByID = uc.UserID\n"
            + "LEFT JOIN users ua ON er.ApprovedByID = ua.UserID\n"
            + "WHERE (? IS NULL OR UPPER(er.RequestCode) LIKE UPPER(?))\n"
            + "AND (? IS NULL OR DATE(er.CreatedDate) >= ?)\n"
            + "AND (? IS NULL OR DATE(er.CreatedDate) <= ?)\n"
            + "AND (? IS NULL OR er.CreatedByID = ?)\n"
            + "UNION\n"
            + "SELECT \n"
            + "    'Import' AS request_type,\n"
            + "    ih.ImportHistoryID AS request_id,\n"
            + "    ih.RequestCode AS request_code,\n"
            + "    ih.ImportDate AS created_date,\n"
            + "    CONCAT(uc.LastName, ' ', uc.FirstName) AS created_by_name,\n"
            + "    NULL AS approved_by_name,\n"
            + "    1 AS status\n"
            + "FROM importhistory ih\n"
            + "LEFT JOIN users uc ON ih.ImportedByID = uc.UserID\n"
            + "WHERE (? IS NULL OR UPPER(ih.RequestCode) LIKE UPPER(?))\n"
            + "AND (? IS NULL OR DATE(ih.ImportDate) >= ?)\n"
            + "AND (? IS NULL OR DATE(ih.ImportDate) <= ?)\n"
            + "AND (? IS NULL OR ih.ImportedByID = ?)\n"
            + "UNION\n"
            + "SELECT \n"
            + "    'Repair' AS request_type,\n"
            + "    rr.RepairRequestID AS request_id,\n"
            + "    rr.RequestCode AS request_code,\n"
            + "    rr.CreatedDate AS created_date,\n"
            + "    CONCAT(uc.LastName, ' ', uc.FirstName) AS created_by_name,\n"
            + "    CONCAT(ua.LastName, ' ', ua.FirstName) AS approved_by_name, \n"
            + "    rr.Status AS status\n"
            + "FROM repairrequests rr\n"
            + "LEFT JOIN users uc ON rr.CreatedByID = uc.UserID\n"
            + "LEFT JOIN users ua ON rr.ApprovedByID = ua.UserID\n"
            + "WHERE (? IS NULL OR UPPER(rr.RequestCode) LIKE UPPER(?))\n"
            + "AND (? IS NULL OR DATE(rr.CreatedDate) >= ?)\n"
            + "AND (? IS NULL OR DATE(rr.CreatedDate) <= ?)\n"
            + "AND (? IS NULL OR rr.CreatedByID = ?)\n"
            + "UNION\n"
            + "SELECT \n"
            + "    'Return' AS request_type,\n"
            + "    rr.ReturnRequestID AS request_id,\n"
            + "    rr.RequestCode AS request_code,\n"
            + "    rr.CreatedDate AS created_date,\n"
            + "    CONCAT(uc.LastName, ' ', uc.FirstName) AS created_by_name, \n"
            + "    CONCAT(ua.LastName, ' ', ua.FirstName) AS approved_by_name, \n"
            + "    rr.Status AS status\n"
            + "FROM returnrequests rr\n"
            + "LEFT JOIN users uc ON rr.CreatedByID = uc.UserID\n"
            + "LEFT JOIN users ua ON rr.InputByID = ua.UserID\n"
            + "WHERE (? IS NULL OR UPPER(rr.RequestCode) LIKE UPPER(?))\n"
            + "AND (? IS NULL OR DATE(rr.CreatedDate) >= ?)\n"
            + "AND (? IS NULL OR DATE(rr.CreatedDate) <= ?)\n"
            + "AND (? IS NULL OR rr.CreatedByID = ?)\n"
            + "UNION\n"
            + "SELECT \n"
            + "    'Purchase' AS request_type,\n"
            + "    pr.PurchaseRequestID AS request_id,\n"
            + "    pr.RequestCode AS request_code,\n"
            + "    pr.CreatedDate AS created_date,\n"
            + "    CONCAT(uc.LastName, ' ', uc.FirstName) AS created_by_name, \n"
            + "    CONCAT(ua.LastName, ' ', ua.FirstName) AS approved_by_name, \n"
            + "    pr.Status AS status\n"
            + "FROM purchaserequests pr\n"
            + "LEFT JOIN users uc ON pr.CreatedByID = uc.UserID\n"
            + "LEFT JOIN users ua ON pr.ApprovedByID = ua.UserID\n"
            + "WHERE (? IS NULL OR UPPER(pr.RequestCode) LIKE UPPER(?))\n"
            + "AND (? IS NULL OR DATE(pr.CreatedDate) >= ?)\n"
            + "AND (? IS NULL OR DATE(pr.CreatedDate) <= ?)\n"
            + "AND (? IS NULL OR pr.CreatedByID = ?)\n"
            + "ORDER BY created_date DESC";

    try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
        String likePattern = requestCode != null && !requestCode.isEmpty() ? "%" + requestCode + "%" : null;
        int paramIndex = 1;

        // Export
        ps.setString(paramIndex++, requestCode != null && !requestCode.isEmpty() ? requestCode : null);
        ps.setString(paramIndex++, likePattern);
        ps.setDate(paramIndex++, startDate);
        ps.setDate(paramIndex++, startDate);
        ps.setDate(paramIndex++, endDate);
        ps.setDate(paramIndex++, endDate);
        ps.setObject(paramIndex++, createdById);
        ps.setObject(paramIndex++, createdById);

        // Import
        ps.setString(paramIndex++, requestCode != null && !requestCode.isEmpty() ? requestCode : null);
        ps.setString(paramIndex++, likePattern);
        ps.setDate(paramIndex++, startDate);
        ps.setDate(paramIndex++, startDate);
        ps.setDate(paramIndex++, endDate);
        ps.setDate(paramIndex++, endDate);
        ps.setObject(paramIndex++, createdById);
        ps.setObject(paramIndex++, createdById);

        // Repair
        ps.setString(paramIndex++, requestCode != null && !requestCode.isEmpty() ? requestCode : null);
        ps.setString(paramIndex++, likePattern);
        ps.setDate(paramIndex++, startDate);
        ps.setDate(paramIndex++, startDate);
        ps.setDate(paramIndex++, endDate);
        ps.setDate(paramIndex++, endDate);
        ps.setObject(paramIndex++, createdById);
        ps.setObject(paramIndex++, createdById);

        // Return
        ps.setString(paramIndex++, requestCode != null && !requestCode.isEmpty() ? requestCode : null);
        ps.setString(paramIndex++, likePattern);
        ps.setDate(paramIndex++, startDate);
        ps.setDate(paramIndex++, startDate);
        ps.setDate(paramIndex++, endDate);
        ps.setDate(paramIndex++, endDate);
        ps.setObject(paramIndex++, createdById);
        ps.setObject(paramIndex++, createdById);

        // Purchase
        ps.setString(paramIndex++, requestCode != null && !requestCode.isEmpty() ? requestCode : null);
        ps.setString(paramIndex++, likePattern);
        ps.setDate(paramIndex++, startDate);
        ps.setDate(paramIndex++, startDate);
        ps.setDate(paramIndex++, endDate);
        ps.setDate(paramIndex++, endDate);
        ps.setObject(paramIndex++, createdById);
        ps.setObject(paramIndex++, createdById);

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                RequestDTO request = new RequestDTO();
                request.setRequestType(rs.getString("request_type"));
                request.setRequestId(rs.getInt("request_id"));
                request.setRequestCode(rs.getString("request_code"));
                request.setCreatedDate(rs.getTimestamp("created_date"));
                request.setCreatedByName(rs.getString("created_by_name"));
                request.setApprovedByName(rs.getString("approved_by_name"));
                request.setStatus(rs.getInt("status"));

                String statusText;
                switch (rs.getInt("status")) {
                    case 0:
                        statusText = "Chưa duyệt";
                        break;
                    case 1:
                        statusText = "Đã duyệt";
                        break;
                    case 2:
                        statusText = "Từ chối";
                        break;
                    default:
                        statusText = "Không xác định";
                }
                request.setStatusText(statusText);

                RequestDTO detailedRequest = getRequestDetail(rs.getInt("request_id"), rs.getString("request_type"));
                request.setMaterials(detailedRequest.getMaterials());

                requests.add(request);
            }
        }
    }
    return requests;
}
    
    // Phương thức xóa yêu cầu
    public void deleteRequest(int requestId, String requestType) throws SQLException {
        String sqlDeleteMaterials = "";
        String sqlDeleteRequest = "";
        String idColumn = "";

        switch (requestType.toLowerCase()) {
            case "export":
                sqlDeleteMaterials = "DELETE FROM exportrequestmaterials WHERE ExportRequestID = ?";
                sqlDeleteRequest = "DELETE FROM exportrequests WHERE ExportRequestID = ?";
                idColumn = "ExportRequestID";
                break;
            case "purchase":
                sqlDeleteMaterials = "DELETE FROM purchaserequestmaterials WHERE PurchaseRequestID = ?";
                sqlDeleteRequest = "DELETE FROM purchaserequests WHERE PurchaseRequestID = ?";
                idColumn = "PurchaseRequestID";
                break;
            case "repair":
                sqlDeleteMaterials = "DELETE FROM repairrequestmaterials WHERE RepairRequestID = ?";
                sqlDeleteRequest = "DELETE FROM repairrequests WHERE RepairRequestID = ?";
                idColumn = "RepairRequestID";
                break;
            case "return":
                sqlDeleteMaterials = "DELETE FROM returnrequestmaterials WHERE ReturnRequestID = ?";
                sqlDeleteRequest = "DELETE FROM returnrequests WHERE ReturnRequestID = ?";
                idColumn = "ReturnRequestID";
                break;
            case "import":
                sqlDeleteMaterials = "DELETE FROM importhistorymaterials WHERE ImportHistoryID = ?";
                sqlDeleteRequest = "DELETE FROM importhistory WHERE ImportHistoryID = ?";
                idColumn = "ImportHistoryID";
                break;
            default:
                throw new SQLException("Loại yêu cầu không hợp lệ: " + requestType);
        }

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false); // Bắt đầu transaction
            try {
                // Xóa bản ghi trong bảng trung gian (vật tư liên quan)
                try (PreparedStatement stmtMaterials = conn.prepareStatement(sqlDeleteMaterials)) {
                    stmtMaterials.setInt(1, requestId);
                    stmtMaterials.executeUpdate();
                }

                // Xóa bản ghi trong bảng chính
                try (PreparedStatement stmtRequest = conn.prepareStatement(sqlDeleteRequest)) {
                    stmtRequest.setInt(1, requestId);
                    int rowsAffected = stmtRequest.executeUpdate();
                    if (rowsAffected == 0) {
                        throw new SQLException("Không tìm thấy yêu cầu với " + idColumn + ": " + requestId);
                    }
                }

                conn.commit(); // Commit transaction
            } catch (SQLException e) {
                conn.rollback(); // Rollback nếu có lỗi
                throw e;
            } finally {
                conn.setAutoCommit(true); // Khôi phục chế độ auto-commit
            }
        }
    }
}

