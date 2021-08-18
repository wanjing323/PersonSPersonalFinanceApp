package my.edu.utar.person.application;

public class UserModel {
    private String email;
    private String loginType;
    private String phone;
    private String uid;
    private String username;

    public UserModel() {
    }

    public UserModel(String email, String loginType, String phone, String uid, String username) {
        this.email = email;
        this.loginType = loginType;
        this.phone = phone;
        this.uid = uid;
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
