package menum.menum.Model;

/**
 * Created by deniz on 3.2.2018.
 */

public class AddProductPost {
    private String productName;
    private String productPrice;
    private String productExplanation;
    private String productServiceTime;
    private String productImageUrl;
    private String salesSituation;
    private String commentSituation;

    public AddProductPost() {
    }

    public AddProductPost(String productName, String productPrice, String productExplanation,
                          String productServiceTime, String productImageUrl,String salesSituation,String commentSituation) {
        this.productName = productName;
        this.productPrice = productPrice;
        this.productExplanation = productExplanation;
        this.productServiceTime = productServiceTime;
        this.productImageUrl = productImageUrl;
        this.salesSituation=salesSituation;
        this.commentSituation=commentSituation;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductExplanation() {
        return productExplanation;
    }

    public void setProductExplanation(String productExplanation) {
        this.productExplanation = productExplanation;
    }

    public String getProductServiceTime() {
        return productServiceTime;
    }

    public void setProductServiceTime(String productServiceTime) {
        this.productServiceTime = productServiceTime;
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

    public String getCommentSituation() {
        return commentSituation;
    }

    public void setCommentSituation(String commentSituation) {
        this.commentSituation = commentSituation;
    }
}
