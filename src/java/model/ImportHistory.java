package model;

import java.sql.Timestamp;

public class ImportHistory {
    private int importHistoryID;
    private int importedByID;
    private Timestamp importDate;
    private Integer purchaseRequestID;
    private Integer returnRequestID;
    
    public ImportHistory() {
    }
    
    public ImportHistory(int importHistoryID, int importedByID, Timestamp importDate, 
                        Integer purchaseRequestID, Integer returnRequestID) {
        this.importHistoryID = importHistoryID;
        this.importedByID = importedByID;
        this.importDate = importDate;
        this.purchaseRequestID = purchaseRequestID;
        this.returnRequestID = returnRequestID;
    }
    
    public int getImportHistoryID() {
        return importHistoryID;
    }
    
    public void setImportHistoryID(int importHistoryID) {
        this.importHistoryID = importHistoryID;
    }
    
    public int getImportedByID() {
        return importedByID;
    }
    
    public void setImportedByID(int importedByID) {
        this.importedByID = importedByID;
    }
    
    public Timestamp getImportDate() {
        return importDate;
    }
    
    public void setImportDate(Timestamp importDate) {
        this.importDate = importDate;
    }
    
    public Integer getPurchaseRequestID() {
        return purchaseRequestID;
    }
    
    public void setPurchaseRequestID(Integer purchaseRequestID) {
        this.purchaseRequestID = purchaseRequestID;
    }
    
    public Integer getReturnRequestID() {
        return returnRequestID;
    }
    
    public void setReturnRequestID(Integer returnRequestID) {
        this.returnRequestID = returnRequestID;
    }
} 