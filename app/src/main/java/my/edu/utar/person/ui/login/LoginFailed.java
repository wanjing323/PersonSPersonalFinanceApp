package my.edu.utar.person.ui.login;

public class LoginFailed extends LoginResult {
    private String errorMsg;

    public LoginFailed(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
