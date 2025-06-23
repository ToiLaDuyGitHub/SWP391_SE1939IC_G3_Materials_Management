package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import util.DBUtil;

public class UnitDAO {

    public String getMinUnitByMaterialId(int materialId) {
        String minUnit = null;
        String sql = "SELECT MinUnit FROM units WHERE MaterialID = ? LIMIT 1";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, materialId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    minUnit = rs.getString("MinUnit");
                }
            }
            System.out.println("unit");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return minUnit;
    }
} 