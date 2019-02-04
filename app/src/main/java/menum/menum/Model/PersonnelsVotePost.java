package menum.menum.Model;

/**
 * Created by deniz on 5.2.2018.
 */

public class PersonnelsVotePost {
    private String firstDate;
    private String lastDate;
    private String voteRate;
    private String voteCounter;

    public PersonnelsVotePost() {
    }

    public PersonnelsVotePost(String firstDate, String lastDate, String voteRate,String voteCounter) {
        this.firstDate = firstDate;
        this.lastDate = lastDate;
        this.voteRate = voteRate;
        this.voteCounter=voteCounter;
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

    public String getVoteRate() {
        return voteRate;
    }

    public void setVoteRate(String voteRate) {
        this.voteRate = voteRate;
    }

    public String getVoteCounter() {
        return voteCounter;
    }

    public void setVoteCounter(String voteCounter) {
        this.voteCounter = voteCounter;
    }
}
