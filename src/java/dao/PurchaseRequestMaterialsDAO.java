package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.PurchaseRequestMaterials;
import util.DBUtil;

public class PurchaseRequestMaterialsDAO {

    public List<PurchaseRequestMaterials> getByPurchaseRequestId(int purchaseRequestId) {
        List<PurchaseRequestMaterials> list = new ArrayList<>();
        String sql = "SELECT PurchaseRequestID, MaterialID, Quantity FROM purchaserequestmaterials WHERE PurchaseRequestID = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, purchaseRequestId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PurchaseRequestMaterials prm = new PurchaseRequestMaterials();
                    prm.setPurchaseRequestID(rs.getInt("PurchaseRequestID"));
                    prm.setMaterialID(rs.getInt("MaterialID"));
                    prm.setQuantity(rs.getInt("Quantity"));
                    list.add(prm);
                }
            }
            System.out.println("PurchaseRequestMaterialsDAO");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    // create method getByPurchaseRequestId
    // initialize MaterialsDAO to get material name and quantity and unit 
    // have to join purchaserequestmaterials join purchaserequest join material join units
}
