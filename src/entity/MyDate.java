package entity;

public class MyDate {
    private int year, month, day;
    
    public MyDate(int year, int month, int day){
        this.year = year;
        this.month = month;
        this.day = day;
    }
    
    public int getYear(){
        return year;
    }
    
    public int getMonth(){
        return month;
    }
    
    public int getDay(){
        return day;
    }
    
    public int compareTo(MyDate other){
        if (year != other.year){
            return year - other.year;
        }
        if (month != other.month){
            return month - other.month;
        }
        return day - other.day;
    }
    
    @Override
    public boolean equals(Object o){
        if (this == o){
            return true;
        }else if (!(o instanceof MyDate)){
            return false;
        }
        MyDate d = (MyDate) o;
        return year == d.year && month == d.month && day == d.day;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + this.year;
        hash = 97 * hash + this.month;
        hash = 97 * hash + this.day;
        return hash;
    }
    
    @Override
    public String toString() {
        return year + "-" + (month < 10 ? "0" : "") + month + "-" + (day < 10 ? "0" : "") + day;
    }
}
