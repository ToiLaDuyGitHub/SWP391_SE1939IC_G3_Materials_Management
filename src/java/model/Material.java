/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author acer
 */
public class Material {

    private int materialID;
    private String materialName;
    private Category category;
    private SubCategory subcategory;
    private Supplier supplierID;
    private String image;
    private MaterialQuantity quantity;
    private String detail;
    private String minUnit;

    public Material() {
    }

    public Material(int materialID, String materialName, Category category, SubCategory subcategory, Supplier supplierID, String image, MaterialQuantity quantity, String detail, String minUnit) {
        this.materialID = materialID;
        this.materialName = materialName;
        this.category = category;
        this.subcategory = subcategory;
        this.supplierID = supplierID;
        this.image = image;
        this.quantity = quantity;
        this.detail = detail;
        this.minUnit = minUnit;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public SubCategory getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(SubCategory subcategory) {
        this.subcategory = subcategory;
    }

    public Supplier getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(Supplier supplierID) {
        this.supplierID = supplierID;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public MaterialQuantity getQuantity() {
        return quantity;
    }

    public void setQuantity(MaterialQuantity quantity) {
        this.quantity = quantity;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getMinUnit() {
        return minUnit;
    }

    public void setMinUnit(String minUnit) {
        this.minUnit = minUnit;
    }

}