package model;

import java.util.Date;


public class PurchaseRequest {
    private int purchaseRequestID;
    private int createdByID;
    private Integer approvedByID; // Có thể null
    private Date createdDate;
    private Date approvedDate;
    private String note;
    private byte status;

    // Getters and Setters
    public int getPurchaseRequestID() {
        return purchaseRequestID;
    }

    public void setPurchaseRequestID(int purchaseRequestID) {
        this.purchaseRequestID = purchaseRequestID;
    }

    public int getCreatedByID() {
        return createdByID;
    }

    public void setCreatedByID(int createdByID) {
        this.createdByID = createdByID;
    }

    public Integer getApprovedByID() {
        return approvedByID;
    }

    public void setApprovedByID(Integer approvedByID) {
        this.approvedByID = approvedByID;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(Date approvedDate) {
        this.approvedDate = approvedDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }
}
