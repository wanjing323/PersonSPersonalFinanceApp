package my.edu.utar.person;


public class Expenses {
    private String key, amount, description,imageId,expDate,expensesCategory,monthYear,expensesCategory_Month,uid;

    public Expenses() {
    }

    public Expenses(String key, String amount, String description, String imageId, String expDate,
                    String expensesCategory, String monthYear, String expensesCategory_Month ,String uid) {
        this.key = key;
        this.amount = amount;
        this.description = description;
        this.imageId = imageId;
        this.expDate = expDate;
        this.expensesCategory=expensesCategory;
        this.monthYear = monthYear;
        this.expensesCategory_Month=expensesCategory_Month;
        this.uid=uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getExpensesCategory_Month() {
        return expensesCategory_Month;
    }

    public void setExpensesCategory_Month(String expensesCategory_Month) {
        this.expensesCategory_Month = expensesCategory_Month;
    }

    public String getMonthYear() {
        return monthYear;
    }

    public void setMonthYear(String monthYear) {
        this.monthYear = monthYear;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public String getExpensesCategory() {
        return expensesCategory;
    }

    public void setExpensesCategory(String expensesCategory) {
        this.expensesCategory = expensesCategory;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

}
