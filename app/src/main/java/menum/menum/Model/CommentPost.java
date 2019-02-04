package menum.menum.Model;

/**
 * Created by deniz on 7.2.2018.
 */

public class CommentPost {
    private String date;
    private String hour;
    private String name;
    private String surname;
    private String comment;
    private String UID;
    private String imeiNumber;

    public CommentPost() {
    }

    public CommentPost(String date, String hour, String name, String surname, String comment,String UID,String imeiNumber) {
        this.date = date;
        this.hour = hour;
        this.name = name;
        this.surname = surname;
        this.comment = comment;
        this.UID=UID;
        this.imeiNumber=imeiNumber;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getImeiNumber() {
        return imeiNumber;
    }

    public void setImeiNumber(String imeiNumber) {
        this.imeiNumber = imeiNumber;
    }
}
