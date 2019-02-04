package menum.menum.Model;

/**
 * Created by deniz on 1.2.2018.
 */

public class StorePropertyPost {
    private String phoneNumber;
    private String name;
    private String surname;
    private String storeName;
    private String adress;
    private String city;
    private String userName;
    private String password;
    private String iconStore;

    public StorePropertyPost() {
    }

    public StorePropertyPost(String phoneNumber, String name, String surname, String storeName, String adress,
                             String city, String userName, String password,String iconStore) {
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.surname = surname;
        this.storeName = storeName;
        this.adress = adress;
        this.city = city;
        this.userName = userName;
        this.password = password;
        this.iconStore=iconStore;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIconStore() {
        return iconStore;
    }

    public void setIconStore(String iconStore) {
        this.iconStore = iconStore;
    }
}
