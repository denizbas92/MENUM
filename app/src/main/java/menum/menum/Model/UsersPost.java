package menum.menum.Model;

/**
 * Created by deniz on 1.2.2018.
 */

public class UsersPost {
    private String isActive;
    private String password;
    private String phoneNumber;
    private String resetAnswer;
    private String resetQuestion;
    private String storeName;
    private String userName;

    public UsersPost() {
    }

    public UsersPost(String phoneNumber, String userName,String password, String storeName,String resetQuestion,String resetAnswer,
                     String isACtive) {
        this.isActive = isACtive;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.resetAnswer = resetAnswer;
        this.resetQuestion = resetQuestion;
        this.storeName = storeName;
        this.userName = userName;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getResetAnswer() {
        return resetAnswer;
    }

    public void setResetAnswer(String resetAnswer) {
        this.resetAnswer = resetAnswer;
    }

    public String getResetQuestion() {
        return resetQuestion;
    }

    public void setResetQuestion(String resetQuestion) {
        this.resetQuestion = resetQuestion;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
