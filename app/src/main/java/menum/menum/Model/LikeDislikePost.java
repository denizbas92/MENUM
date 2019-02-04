package menum.menum.Model;

/**
 * Created by deniz on 6.2.2018.
 */

public class LikeDislikePost {
    private String likeDislike;
    private String likeCounter;
    private String dislikeCounter;
    private String productImageUrl;
    private String salesSituation;

    public LikeDislikePost() {
    }

    public LikeDislikePost(String likeDislike) {
        this.likeDislike = likeDislike;
    }

    public String getLikeDislike() {
        return likeDislike;
    }

    public void setLikeDislike(String likeDislike) {
        this.likeDislike = likeDislike;
    }

    public String getLikeCounter() {
        return likeCounter;
    }

    public void setLikeCounter(String likeCounter) {
        this.likeCounter = likeCounter;
    }

    public String getDislikeCounter() {
        return dislikeCounter;
    }

    public void setDislikeCounter(String dislikeCounter) {
        this.dislikeCounter = dislikeCounter;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public String getSalesSituation() {
        return salesSituation;
    }

    public void setSalesSituation(String salesSituation) {
        this.salesSituation = salesSituation;
    }
}
