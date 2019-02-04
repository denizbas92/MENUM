package menum.menum.Model;

/**
 * Created by deniz on 1.2.2018.
 */

public class ForgotPasswordPost {
    private String resetQuestion;
    private String resetAnswer;

    public ForgotPasswordPost() {
    }

    public ForgotPasswordPost(String resetQuestion, String resetAnswer) {
        this.resetQuestion = resetQuestion;
        this.resetAnswer = resetAnswer;
    }

    public String getResetQuestion() {
        return resetQuestion;
    }

    public void setResetQuestion(String resetQuestion) {
        this.resetQuestion = resetQuestion;
    }

    public String getResetAnswer() {
        return resetAnswer;
    }

    public void setResetAnswer(String resetAnswer) {
        this.resetAnswer = resetAnswer;
    }
}
