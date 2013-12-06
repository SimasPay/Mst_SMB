/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao.query;

/**
 *
 * @author ADMIN
 */
public class ChannelCodeQuery extends BaseQuery {

    private String channelCode;
    private String channelName;
    private Integer sourceApplication;
    private String channelNameLike;

    public String getChannelNameLike() {
        return channelNameLike;
    }

    public void setChannelNameLike(String channelNameLike) {
        this.channelNameLike = channelNameLike;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public Integer getSourceApplication() {
        return sourceApplication;
    }

    public void setSourceApplication(Integer sourceApplication) {
        this.sourceApplication = sourceApplication;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

}
