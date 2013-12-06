/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao.query;

/**
 *
 * @author sunil
 */
public class NotificationQuery extends BaseQuery {
    Integer notificationCode;
    Long notificationID;
    String notificationCodeName;
    String notificationText;
    Integer notificationMethod;
    Integer language;

    public String getNotificationCodeName() {
        return notificationCodeName;
    }

    public void setNotificationCodeName(String notificationCodeName) {
        this.notificationCodeName = notificationCodeName;
    }

    public Long getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(Long notificationID) {
        this.notificationID = notificationID;
    }


    public String getNotificationText() {
        return notificationText;
    }

    public void setNotificationText(String NotificationText) {
        this.notificationText = NotificationText;
    }
    

    public Integer getLanguage() {
        return language;
    }

    public void setLanguage(Integer language) {
        this.language = language;
    }

    public Integer getNotificationCode() {
        return notificationCode;
    }

    public void setNotificationCode(Integer notificationCode) {
        this.notificationCode = notificationCode;
    }

    public Integer getNotificationMethod() {
        return notificationMethod;
    }

    public void setNotificationMethod(Integer notificationMethod) {
        this.notificationMethod = notificationMethod;
    }

}
