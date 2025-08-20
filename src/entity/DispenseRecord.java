package entity;

import java.time.LocalDate;
import java.time.LocalTime;

public class DispenseRecord {
    private String recordID;
    private String consultationID;
    private String medicineID;
    private int quantity;
    private LocalDate dispenseDate;
    private LocalTime dispenseTime;

    public DispenseRecord(String recordID, String consultationID, String medicineID,
                            int quantity, LocalDate dispenseDate, LocalTime dispenseTime) {
        this.recordID = recordID;
        this.consultationID = consultationID;
        this.medicineID = medicineID;
        this.quantity = quantity;
        this.dispenseDate = dispenseDate;
        this.dispenseTime = dispenseTime;
    }

    public String getRecordID() { 
        return recordID; 
    }
    
    public String getConsultationID() { 
        return consultationID; 
    }
    
    public String getMedicineID() { 
        return medicineID; 
    }
    
    public int getQuantity() { 
        return quantity; 
    }
    
    public LocalDate getDispenseDate() { 
        return dispenseDate; 
    }
    
    public LocalTime getDispenseTime() { 
        return dispenseTime; 
    }

    @Override
    public String toString() {
        return String.format("DispenseRecord{id=%s, consult=%s, med=%s, qty=%d, date=%s, time=%s}",
            recordID, consultationID, medicineID, quantity, dispenseDate, dispenseTime);
    }
    
}
