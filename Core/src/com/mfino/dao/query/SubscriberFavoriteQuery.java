package com.mfino.dao.query;

/**
 * @author Srikanth
 * 
 */
public class SubscriberFavoriteQuery extends BaseQuery {
	private Long subscriberID;
	private Long favoriteCategoryID;
	private String favoriteLabel;
	private String favoriteValue;
	private String favoriteCode;

	public Long getSubscriberID() {
		return subscriberID;
	}

	public void setSubscriberID(Long subscriberID) {
		this.subscriberID = subscriberID;
	}

	public Long getFavoriteCategoryID() {
		return favoriteCategoryID;
	}

	public void setFavoriteCategoryID(Long favoriteCategoryID) {
		this.favoriteCategoryID = favoriteCategoryID;
	}

	public String getFavoriteLabel() {
		return favoriteLabel;
	}

	public void setFavoriteLabel(String favoriteLabel) {
		this.favoriteLabel = favoriteLabel;
	}

	public String getFavoriteValue() {
		return favoriteValue;
	}

	public void setFavoriteValue(String favoriteValue) {
		this.favoriteValue = favoriteValue;
	}

	public String getFavoriteCode() {
		return favoriteCode;
	}

	public void setFavoriteCode(String favoriteCode) {
		this.favoriteCode = favoriteCode;
	}
	
}
