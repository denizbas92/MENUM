package menum.menum.Model;

/**
 * Created by deniz on 7.1.2018.
 */

public class NewNotificationSettings {
    private String CloseNotification;
    private String CloseVibration;
    private String CloseBeep;
    private String RemoveOldAppoinments;
    private String CloseLight;

    public NewNotificationSettings() {
    }

    public NewNotificationSettings(String closeNotification, String closeVibration, String closeBeep, String closeLight ) {
        CloseNotification = closeNotification;
        CloseVibration = closeVibration;
        CloseBeep = closeBeep;
        CloseLight=closeLight;
    }

    public String getCloseNotification() {
        return CloseNotification;
    }

    public void setCloseNotification(String closeNotification) {
        CloseNotification = closeNotification;
    }

    public String getCloseVibration() {
        return CloseVibration;
    }

    public void setCloseVibration(String closeVibration) {
        CloseVibration = closeVibration;
    }

    public String getCloseBeep() {
        return CloseBeep;
    }

    public void setCloseBeep(String closeBeep) {
        CloseBeep = closeBeep;
    }

    public String getRemoveOldAppoinments() {
        return RemoveOldAppoinments;
    }

    public void setRemoveOldAppoinments(String removeOldAppoinments) {
        RemoveOldAppoinments = removeOldAppoinments;
    }

    public String getCloseLight() {
        return CloseLight;
    }

    public void setCloseLight(String closeLight) {
        CloseLight = closeLight;
    }
}
