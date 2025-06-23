package model;

public class PurchaseRequestMaterials {
    private int purchaseRequestID;
    private int materialID;
    private int quantity;

    // Getters and Setters
    public int getPurchaseRequestID() {
        return purchaseRequestID;
    }

    public void setPurchaseRequestID(int purchaseRequestID) {
        this.purchaseRequestID = purchaseRequestID;
    }

    public int getMaterialID() {
        return materialID;
    }

    public void setMaterialID(int materialID) {
        this.materialID = materialID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
