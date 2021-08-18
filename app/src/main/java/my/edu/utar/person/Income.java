package my.edu.utar.person;

public class Income {
    String key,income,description,date,imageid,incomeCategory,monthYear,uid;

    public Income() {
    }

    public Income(String key, String income, String description, String date, String imageid, String incomeCategory, String monthYear, String uid) {
        this.key = key;
        this.income = income;
        this.description = description;
        this.date = date;
        this.imageid = imageid;
        this.incomeCategory=incomeCategory;
        this.monthYear=monthYear;
        this.uid=uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMonthYear() {
        return monthYear;
    }

    public void setMonthYear(String monthYear) {
        this.monthYear = monthYear;
    }

    public String getIncomeCategory() {
        return incomeCategory;
    }

    public void setIncomeCategory(String incomeCategory) {
        this.incomeCategory = incomeCategory;
    }

    public String getImageid() {
        return imageid;
    }

    public void setImageid(String imageid) {
        this.imageid = imageid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
        this.income = income;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
