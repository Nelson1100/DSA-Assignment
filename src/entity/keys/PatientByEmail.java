package entity.keys;

import entity.Patient;

/**
 *
 * @author Ng Wei Jian
 */
public class PatientByEmail implements Comparable<PatientByEmail> {
    public final String email;
    public final Patient ref;

    public PatientByEmail(String email, Patient ref) {
        this.email = email;
        this.ref = ref;
    }
    
    public Patient getPatient() {
        return ref;
    }

    @Override
    public int compareTo(PatientByEmail o) {
        if (o == null) 
            return 1;
        
        if (this.email == null) 
            return (o.email == null) ? 0 : -1;
        
        return this.email.compareTo(o.email);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) 
            return true;
        
        if (!(obj instanceof PatientByEmail other)) 
            return false;
        
        return email != null && email.equals(other.email);
    }

    @Override
    public int hashCode() {
        return email == null ? 0 : email.hashCode();
    }
}
