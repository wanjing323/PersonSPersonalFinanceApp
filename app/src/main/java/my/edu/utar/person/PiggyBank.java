package my.edu.utar.person;

public class PiggyBank {
    public String key,goalTitle,goalAmount,latestUpdate,progressPerctg,amountLeft,amountSaved,status,uid,latest;

    public PiggyBank() {
    }

    public PiggyBank(String key, String goalTitle, String goalAmount, String latestUpdate, String progressPerctg, String amountLeft, String amountSaved, String status, String uid,String latest) {
        this.key = key;
        this.goalTitle = goalTitle;
        this.goalAmount = goalAmount;
        this.latestUpdate = latestUpdate;
        this.progressPerctg = progressPerctg;
        this.amountLeft = amountLeft;
        this.amountSaved = amountSaved;
        this.status = status;
        this.uid=uid;
        this.latest=latest;
    }

    public String getLatest() {
        return latest;
    }

    public void setLatest(String latest) {
        this.latest = latest;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getGoalTitle() {
        return goalTitle;
    }

    public void setGoalTitle(String goalTitle) {
        this.goalTitle = goalTitle;
    }

    public String getGoalAmount() {
        return goalAmount;
    }

    public void setGoalAmount(String goalAmount) {
        this.goalAmount = goalAmount;
    }

    public String getLatestUpdate() {
        return latestUpdate;
    }

    public void setLatestUpdate(String latestUpdate) {
        this.latestUpdate = latestUpdate;
    }

    public String getProgressPerctg() {
        return progressPerctg;
    }

    public void setProgressPerctg(String progressPerctg) {
        this.progressPerctg = progressPerctg;
    }

    public String getAmountLeft() {
        return amountLeft;
    }

    public void setAmountLeft(String amountLeft) {
        this.amountLeft = amountLeft;
    }

    public String getAmountSaved() {
        return amountSaved;
    }

    public void setAmountSaved(String amountSaved) {
        this.amountSaved = amountSaved;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
