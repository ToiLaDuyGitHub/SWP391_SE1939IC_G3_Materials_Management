/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.dto;

/**
 *
 * @author ADMIN
 */
public class UnitsDTO {
    private int unitID;
    private int materialID;
    private String materialName;
    private String minUnit;
    private String maxUnit;
    private int ratio;
    private String subcategoryName;

    public UnitsDTO() {
    }

    public UnitsDTO(int unitID, int materialID, String materialName, String minUnit, 
                   String maxUnit, int ratio, String subcategoryName) {
        this.unitID = unitID;
        this.materialID = materialID;
        this.materialName = materialName;
        this.minUnit = minUnit;
        this.maxUnit = maxUnit;
        this.ratio = ratio;
        this.subcategoryName = subcategoryName;
    }

    public int getUnitID() {
        return unitID;
    }

    public void setUnitID(int unitID) {
        this.unitID = unitID;
    }

    public int getMaterialID() {
        return materialID;
    }

    public void setMaterialID(int materialID) {
        this.materialID = materialID;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getMinUnit() {
        return minUnit;
    }

    public void setMinUnit(String minUnit) {
        this.minUnit = minUnit;
    }

    public String getMaxUnit() {
        return maxUnit;
    }

    public void setMaxUnit(String maxUnit) {
        this.maxUnit = maxUnit;
    }

    public int getRatio() {
        return ratio;
    }

    public void setRatio(int ratio) {
        this.ratio = ratio;
    }

    public String getSubcategoryName() {
        return subcategoryName;
    }

    public void setSubcategoryName(String subcategoryName) {
        this.subcategoryName = subcategoryName;
    }
}