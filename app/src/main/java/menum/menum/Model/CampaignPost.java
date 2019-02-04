package menum.menum.Model;

/**
 * Created by deniz on 19.1.2018.
 */

public class CampaignPost {
    private String title;
    private String campaignDetail;
    private String publishHour;
    private String publishDate;

    public CampaignPost() {
    }

    public CampaignPost(String title, String campaignDetail) {
        this.title = title;
        this.campaignDetail = campaignDetail;
    }

    public CampaignPost(String title, String campaignDetail, String publishDate, String publishHour) {
        this.title = title;
        this.campaignDetail = campaignDetail;
        this.publishHour = publishHour;
        this.publishDate = publishDate;
    }

    public String getPublishHour() {
        return publishHour;
    }

    public void setPublishHour(String publishHour) {
        this.publishHour = publishHour;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCampaignDetail() {
        return campaignDetail;
    }

    public void setCampaignDetail(String campaignDetail) {
        this.campaignDetail = campaignDetail;
    }
}
