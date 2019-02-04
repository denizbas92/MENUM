package menum.menum.Model;

/**
 * Created by deniz on 4.2.2018.
 */

public class AddPersonnelPost {
    private String name;
    private String surname;
    private String imageUrl;
    private String voteRate;
    private String UID;

    public AddPersonnelPost() {
    }

    public AddPersonnelPost(String name, String surname, String imageUrl, String voteRate, String UID) {
        this.name = name;
        this.surname = surname;
        this.imageUrl = imageUrl;
        this.voteRate = voteRate;
        this.UID = UID;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVoteRate() {
        return voteRate;
    }

    public void setVoteRate(String voteRate) {
        this.voteRate = voteRate;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }
}
