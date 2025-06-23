/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java 
 */
package dao;

import model.dto.UnitsDTO;
import model.SubCategory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import util.DBUtil;

/**
 *
 * @author ADMIN
 */
public class UnitsDAO {
    
    public List<UnitsDTO> getUnitsBySearchAndCategory(String searchQuery, String categoryName) {
        List<UnitsDTO> unitsList = new ArrayList<>();
        
        String sql = "SELECT u.unitID, u.MaterialID, u.minUnit, u.maxUnit, u.ratio, " +
                    "m.materialName, sc.subcategoryName " +
                    "FROM units u " +
                    "JOIN materials m ON u.MaterialID = m.MaterialID " +
                    "JOIN subcategories sc ON m.SubcategoryID = sc.SubcategoryID";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                UnitsDTO unit = new UnitsDTO();
                unit.setUnitID(rs.getInt("unitID"));
                unit.setMaterialID(rs.getInt("MaterialID"));
                unit.setMaterialName(rs.getString("materialName"));
                unit.setMinUnit(rs.getString("minUnit"));
                unit.setMaxUnit(rs.getString("maxUnit"));
                unit.setRatio(rs.getInt("ratio"));
                unit.setSubcategoryName(rs.getString("subcategoryName"));
                
                boolean matchesSearch = true;
                boolean matchesCategory = true;
                
                if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                    String searchLower = searchQuery.toLowerCase();
                    String materialName = unit.getMaterialName().toLowerCase();
                    String subCategoryName = unit.getSubcategoryName().toLowerCase();
                    matchesSearch = materialName.contains(searchLower) || subCategoryName.contains(searchLower);
                }
                
                if (categoryName != null && !categoryName.trim().isEmpty()) {
                    matchesCategory = unit.getSubcategoryName().equals(categoryName);
                }
                
                if (matchesSearch && matchesCategory) {
                    unitsList.add(unit);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            return unitsList;
        }
        
        return unitsList;
    }
    
    public List<SubCategory> getAllSubCategories() {
        List<SubCategory> categories = new ArrayList<>();
        String sql = "SELECT SubcategoryID, categoryID, subcategoryName FROM subcategories";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                SubCategory category = new SubCategory();
                category.setSubcategoryID(rs.getInt("SubcategoryID"));
                category.setCategoryID(rs.getInt("categoryID"));
                category.setSubcategoryName(rs.getString("subcategoryName"));
                categories.add(category);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return categories;
    }
}
