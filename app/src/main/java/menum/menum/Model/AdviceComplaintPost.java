package menum.menum.Model;

/**
 * Created by deniz on 5.2.2018.
 */

public class AdviceComplaintPost {
    private String title;
    private String content;
    private String date;
    private String hour;
    private String seen;

    public AdviceComplaintPost() {
    }

    public AdviceComplaintPost(String title, String content, String date, String hour,String seen) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.hour = hour;
        this.seen=seen;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }
}
