package my.edu.utar.person.ui.register;

public class RegistrationFailed extends RegistrationResult{
    private String errorMsg;

    public RegistrationFailed(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
