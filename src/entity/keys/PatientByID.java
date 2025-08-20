package entity.keys;

import entity.Patient;

public class PatientByID implements Comparable<PatientByID>{
    public final String id;
    public final Patient ref; // pointer to the actual Patient
    
    public PatientByID(String id, Patient ref) {
        this.id = id;
        this.ref = ref;
    }
    
    public Patient getPatient() {
        return ref;
    }
    
    @Override
    public int compareTo(PatientByID o) {
        if (o == null)
            return 1;
        
        if (this.id == null)
            return (o.id == null) ? 0 : -1;
        
        return this.id.compareTo(o.id);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        
        if (!(obj instanceof PatientByID other))
            return false;
        
        return id != null && id.equals(other.id);
    }
    
    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }
}
