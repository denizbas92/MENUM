package menum.menum.Model;

/**
 * Created by deniz on 1.2.2018.
 */

public class FreeUsePost {
    private String firstDate;
    private String lastDate;
    private String firstEnter;
    private String timeOver;

    public FreeUsePost() {
    }

    public FreeUsePost(String firstDate, String lastDate, String firstEnter, String timeOver) {
        this.firstDate = firstDate;
        this.lastDate = lastDate;
        this.firstEnter = firstEnter;
        this.timeOver = timeOver;
    }

    public String getFirstDate() {
        return firstDate;
    }

    public void setFirstDate(String firstDate) {
        this.firstDate = firstDate;
    }

    public String getLastDate() {
        return lastDate;
    }

    public void setLastDate(String lastDate) {
        this.lastDate = lastDate;
    }

    public String getFirstEnter() {
        return firstEnter;
    }

    public void setFirstEnter(String firstEnter) {
        this.firstEnter = firstEnter;
    }

    public String getTimeOver() {
        return timeOver;
    }

    public void setTimeOver(String timeOver) {
        this.timeOver = timeOver;
    }
}
