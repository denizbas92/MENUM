package menum.menum.Model;

/**
 * Created by deniz on 8.2.2018.
 */

public class GoToCampaignPost {
    private String phoneNumber;
    private String storeName;
    private String publishDate;
    private String publishHour;

    public GoToCampaignPost() {
    }

    public GoToCampaignPost(String phoneNumber, String storeName, String publishDate, String publishHour) {
        this.phoneNumber = phoneNumber;
        this.storeName = storeName;
        this.publishDate = publishDate;
        this.publishHour = publishHour;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getPublishHour() {
        return publishHour;
    }

    public void setPublishHour(String publishHour) {
        this.publishHour = publishHour;
    }
}
