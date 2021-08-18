package my.edu.utar.person;

public class TimeNotification {

    private int id;
    private String title;
    private String content;
    private int hour;
    private int min;
    private int year;
    private int month;
    private int day;

    public TimeNotification(int id, String title, String content, int hour, int min, int year, int month, int day) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.hour = hour;
        this.min = min;
        this.year = year;
        this.month = month;
        this.day = day;
    }
    public TimeNotification(String title, String content, int hour, int min, int year, int month, int day) {

        this.title = title;
        this.content = content;
        this.hour = hour;
        this.min = min;
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

}
