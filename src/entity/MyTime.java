package entity;

public class MyTime {
    private int hour, minute;

    public MyTime(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public int getHour() { return hour; }
    public int getMinute() { return minute; }

    public int compareTo(MyTime other) {
        if (hour != other.hour) return hour - other.hour;
        return minute - other.minute;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MyTime)) return false;
        MyTime t = (MyTime) o;
        return hour == t.hour && minute == t.minute;
    }

    @Override
    public String toString() {
        return (hour < 10 ? "0" : "") + hour + ":" + (minute < 10 ? "0" : "") + minute;
    }
}
