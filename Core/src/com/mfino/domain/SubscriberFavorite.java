package com.mfino.domain;

// Generated Sep 27, 2016 5:23:21 PM by Hibernate Tools 3.4.0.CR1

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import com.mfino.hibernate.Timestamp;

/**
 * SubscriberFavorite generated by hbm2java
 */
@Entity
@Table(name = "SUBSCRIBER_FAVORITE", uniqueConstraints = @UniqueConstraint(columnNames = {
		"SUBSCRIBERID", "FAVORITECATEGORYID", "FAVORITEVALUE" }))
public class SubscriberFavorite extends Base implements java.io.Serializable {

	
	private FavoriteCategory favoriteCategory;
	private Subscriber subscriber;
	private String favoritelabel;
	private String favoritevalue;
	private String favoritecode;

	public SubscriberFavorite() {
	}

	public SubscriberFavorite(BigDecimal id, FavoriteCategory favoriteCategory,
			Subscriber subscriber, Timestamp lastupdatetime,
			String updatedby, Timestamp createtime, String createdby,
			String favoritelabel, String favoritevalue) {
		this.id = id;
		this.favoriteCategory = favoriteCategory;
		this.subscriber = subscriber;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.favoritelabel = favoritelabel;
		this.favoritevalue = favoritevalue;
	}

	public SubscriberFavorite(BigDecimal id, FavoriteCategory favoriteCategory,
			Subscriber subscriber, Timestamp lastupdatetime,
			String updatedby, Timestamp createtime, String createdby,
			String favoritelabel, String favoritevalue, String favoritecode) {
		this.id = id;
		this.favoriteCategory = favoriteCategory;
		this.subscriber = subscriber;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.favoritelabel = favoritelabel;
		this.favoritevalue = favoritevalue;
		this.favoritecode = favoritecode;
	}

	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FAVORITECATEGORYID", nullable = false)
	public FavoriteCategory getFavoriteCategory() {
		return this.favoriteCategory;
	}

	public void setFavoriteCategory(FavoriteCategory favoriteCategory) {
		this.favoriteCategory = favoriteCategory;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SUBSCRIBERID", nullable = false)
	public Subscriber getSubscriber() {
		return this.subscriber;
	}

	public void setSubscriber(Subscriber subscriber) {
		this.subscriber = subscriber;
	}

	

	@Column(name = "FAVORITELABEL", nullable = false)
	public String getFavoritelabel() {
		return this.favoritelabel;
	}

	public void setFavoritelabel(String favoritelabel) {
		this.favoritelabel = favoritelabel;
	}

	@Column(name = "FAVORITEVALUE", nullable = false)
	public String getFavoritevalue() {
		return this.favoritevalue;
	}

	public void setFavoritevalue(String favoritevalue) {
		this.favoritevalue = favoritevalue;
	}

	@Column(name = "FAVORITECODE", length = 45)
	public String getFavoritecode() {
		return this.favoritecode;
	}

	public void setFavoritecode(String favoritecode) {
		this.favoritecode = favoritecode;
	}

}
