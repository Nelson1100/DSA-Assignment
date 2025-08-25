package entity.keys;

import entity.Patient;

/**
 *
 * @author Ng Wei Jian
 */
public class PatientByContactNo implements Comparable<PatientByContactNo> {
    public final String contactNo;
    public final Patient ref;
    
    public PatientByContactNo(String contactNo, Patient ref) {
        this.contactNo = contactNo;
        this.ref = ref;
    }
    
    public Patient getPatient() {
        return ref;
    }

    @Override
    public int compareTo(PatientByContactNo o) {
        if (o == null) 
            return 1;
        
        if (this.contactNo == null) 
            return (o.contactNo == null) ? 0 : -1;
        
        return this.contactNo.compareTo(o.contactNo);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) 
            return true;
        
        if (!(obj instanceof PatientByContactNo other)) 
            return false;
        
        return contactNo != null && contactNo.equals(other.contactNo);
    }

    @Override
    public int hashCode() {
        return contactNo == null ? 0 : contactNo.hashCode();
    }
}
