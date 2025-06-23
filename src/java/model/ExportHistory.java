package model;

import java.sql.Timestamp;

public class ExportHistory {
    private int exportHistoryID;
    private int exportedByID;
    private Timestamp exportDate;
    private Integer exportRequestID;
    private Integer repairRequestID;
    
    public ExportHistory() {
    }
    
    public ExportHistory(int exportHistoryID, int exportedByID, Timestamp exportDate, 
                        Integer exportRequestID, Integer repairRequestID) {
        this.exportHistoryID = exportHistoryID;
        this.exportedByID = exportedByID;
        this.exportDate = exportDate;
        this.exportRequestID = exportRequestID;
        this.repairRequestID = repairRequestID;
    }
    
    public int getExportHistoryID() {
        return exportHistoryID;
    }
    
    public void setExportHistoryID(int exportHistoryID) {
        this.exportHistoryID = exportHistoryID;
    }
    
    public int getExportedByID() {
        return exportedByID;
    }
    
    public void setExportedByID(int exportedByID) {
        this.exportedByID = exportedByID;
    }
    
    public Timestamp getExportDate() {
        return exportDate;
    }
    
    public void setExportDate(Timestamp exportDate) {
        this.exportDate = exportDate;
    }
    
    public Integer getExportRequestID() {
        return exportRequestID;
    }
    
    public void setExportRequestID(Integer exportRequestID) {
        this.exportRequestID = exportRequestID;
    }
    
    public Integer getRepairRequestID() {
        return repairRequestID;
    }
    
    public void setRepairRequestID(Integer repairRequestID) {
        this.repairRequestID = repairRequestID;
    }
} 