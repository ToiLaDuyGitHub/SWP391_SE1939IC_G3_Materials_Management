package model.dto;

public class SearchMaterialDTO {
    private int materialID;
    private String materialName;
    private String unit;
    private String supplierName;

    public SearchMaterialDTO() {
    }

    public SearchMaterialDTO(int materialID, String materialName, String unit, String supplierName) {
        this.materialID = materialID;
        this.materialName = materialName;
        this.unit = unit;
        this.supplierName = supplierName;
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }
}