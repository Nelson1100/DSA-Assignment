package entity;


public class Pharmacist implements Comparable<Pharmacist>{
    private String ID;
    private String name;
    private String phone;

    public Pharmacist(String id, String name, String phone) {
        this.ID = ID;
        this.name = name;
        this.phone = phone;
    }

    public String getID(){ 
        return ID; 
    }
    
    public String getName(){ 
        return name; 
    }
    
    public String getPhone(){ 
        return phone; 
    }

    public void setName(String name){ 
        this.name = name; 
    }
    
    public void setPhone(String phone){ 
        this.phone = phone; 
    }

    @Override
    public String toString() {
        return String.format("Pharmacist[ID=%s, Name=%s, Phone=%s]", 
                                ID, name, phone);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pharmacist)) return false;
        Pharmacist other = (Pharmacist) o;
        return ID.equals(other.ID);
    }
    
    @Override
    public int compareTo(Pharmacist other) {
        return this.ID.compareTo(other.ID);
    }
}
