package entity.keys;

import entity.Patient;

/**
 *
 * @author Ng Wei Jian
 */
public class PatientByName implements Comparable<PatientByName> {
    public final String nameKey; // UPPER(name) + "|" + ID
    public final Patient ref;
    
    public PatientByName(String name, String id, Patient ref) {
        String up = (name == null ? "" : name.toUpperCase());
        this.nameKey = up + "|" + (id == null ? "" : id);
        this.ref = ref;
    }
    
    public Patient getPatient() {
        return ref;
    }
    
    @Override
    public int compareTo(PatientByName o) {
        if (o == null)
            return 1;
        
        if (this.nameKey == null)
            return (o.nameKey == null ? 0 : -1);
        
        return this.nameKey.compareTo(o.nameKey);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        
        if (!(obj instanceof PatientByName other))
            return false;
        
        return nameKey != null && nameKey.equals(other.nameKey);
    }
    
    @Override
    public int hashCode() {
        return nameKey == null ? 0 : nameKey.hashCode();
    }
}
