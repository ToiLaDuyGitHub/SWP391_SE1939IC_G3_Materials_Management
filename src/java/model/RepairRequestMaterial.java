package model;

public class RepairRequestMaterial {

    private int repairRequestID;
    private int materialID;
    private int quantity;

    // Constructor
    public RepairRequestMaterial() {
    }

    public RepairRequestMaterial(int repairRequestID, int materialID, int quantity) {
        this.repairRequestID = repairRequestID;
        this.materialID = materialID;
        this.quantity = quantity;
    }

    // Getters and Setters
    public int getRepairRequestID() {
        return repairRequestID;
    }

    public void setRepairRequestID(int repairRequestID) {
        this.repairRequestID = repairRequestID;
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
