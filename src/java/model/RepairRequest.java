package model;

import java.sql.Timestamp;

public class RepairRequest {

    private int repairRequestID;
    private int createdByID;
    private Integer approvedByID;
    private Timestamp createdDate;
    private Timestamp approvedDate;
    private String note;
    private int status;

    // Constructor
    public RepairRequest() {
    }

    public RepairRequest(int repairRequestID, int createdByID, Integer approvedByID,
            Timestamp createdDate, Timestamp approvedDate, String note, int status) {
        this.repairRequestID = repairRequestID;
        this.createdByID = createdByID;
        this.approvedByID = approvedByID;
        this.createdDate = createdDate;
        this.approvedDate = approvedDate;
        this.note = note;
        this.status = status;
    }

    // Getters and Setters
    public int getRepairRequestID() {
        return repairRequestID;
    }

    public void setRepairRequestID(int repairRequestID) {
        this.repairRequestID = repairRequestID;
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

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    public Timestamp getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(Timestamp approvedDate) {
        this.approvedDate = approvedDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
