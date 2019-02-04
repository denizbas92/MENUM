package menum.menum.Model;

/**
 * Created by deniz on 8.2.2018.
 */

public class NewNotificationPost {
    private String notification;
    private String date;
    private String hour;
    private String seen;
    private String codeNot;
    private String menuCategoryName;
    private String productName;

    public NewNotificationPost() {
    }

    public NewNotificationPost(String notification, String date, String hour,String seen,String codeNot,String menuCategoryName ,String productName) {
        this.notification = notification;
        this.date = date;
        this.hour = hour;
        this.seen=seen;
        this.codeNot=codeNot;
        this.menuCategoryName=menuCategoryName;
        this.productName=productName;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
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

    public String getCodeNot() {
        return codeNot;
    }

    public void setCodeNot(String codeNot) {
        this.codeNot = codeNot;
    }

    public String getMenuCategoryName() {
        return menuCategoryName;
    }

    public void setMenuCategoryName(String menuCategoryName) {
        this.menuCategoryName = menuCategoryName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
