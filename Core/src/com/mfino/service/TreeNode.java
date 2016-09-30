package com.mfino.service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author Sasi
 *
 */
public class TreeNode {
	
	private Long id;//unique
	private Long objectId;
	private Integer nodeType;
	private Integer level;
	private String text;
	private String icon;
	private boolean expandable;
	private Long dctId;
	private Long partnerId;
	private Integer permissionType;
	private boolean disabled;
	private boolean selected;
	private Long serviceId;
	private String mdn;
	private Long subscriberId;
	private Integer businessPartnerType;
	private Integer levels;
	private BigDecimal balance;
	
	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	private Set<TreeNode> children = new HashSet<TreeNode>();
	
	public void addParent(TreeNode parent){
		parent.getChildren().add(this);
	}
	
	public void addChild(TreeNode distributionTreeNode){
		this.getChildren().add(distributionTreeNode);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getObjectId() {
		return objectId;
	}

	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public boolean isExpandable() {
		return expandable;
	}

	public void setExpandable(boolean expandable) {
		this.expandable = expandable;
	}

	public Set<TreeNode> getChildren() {
		return children;
	}

	public void setChildren(Set<TreeNode> children) {
		this.children = children;
	}

	public Integer getNodeType() {
		return nodeType;
	}

	public void setNodeType(Integer nodeType) {
		this.nodeType = nodeType;
	}

	public Long getDctId() {
		return dctId;
	}

	public void setDctId(Long dctId) {
		this.dctId = dctId;
	}

	public Long getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(Long partnerId) {
		this.partnerId = partnerId;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public Integer getPermissionType() {
		return permissionType;
	}

	public void setPermissionType(Integer permissionType) {
		this.permissionType = permissionType;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public Long getServiceId() {
		return serviceId;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}

	public String getMdn() {
		return mdn;
	}

	public void setMdn(String mdn) {
		this.mdn = mdn;
	}

	public Long getSubscriberId() {
		return subscriberId;
	}

	public void setSubscriberId(Long subscriberId) {
		this.subscriberId = subscriberId;
	}

	public Integer getBusinessPartnerType() {
		return businessPartnerType;
	}

	public void setBusinessPartnerType(Integer businessPartnerType) {
		this.businessPartnerType = businessPartnerType;
	}

	public Integer getLevels() {
		return levels;
	}

	public void setLevels(Integer levels) {
		this.levels = levels;
	}
	
	
}
