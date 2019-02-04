package menum.menum.Model;

/**
 * Created by deniz on 1.2.2018.
 */

public class NotificationCounterPost {
    private String oldCounter;
    private String newCounter;

    public NotificationCounterPost() {
    }

    public NotificationCounterPost(String oldCounter, String newCounter) {
        this.oldCounter = oldCounter;
        this.newCounter = newCounter;
    }

    public String getOldCounter() {
        return oldCounter;
    }

    public void setOldCounter(String oldCounter) {
        this.oldCounter = oldCounter;
    }

    public String getNewCounter() {
        return newCounter;
    }

    public void setNewCounter(String newCounter) {
        this.newCounter = newCounter;
    }
}
