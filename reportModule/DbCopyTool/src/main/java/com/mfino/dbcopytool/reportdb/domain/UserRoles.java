package com.mfino.dbcopytool.reportdb.domain;

import java.util.Date;

import org.hibernate.Session;

import com.mfino.dbcopytool.persistence.ReportDbHibernateUtil;

public class UserRoles extends TableRow implements java.io.Serializable {

	private String userId;
	private String userName;
	private String roleId;
	private String role;
	private String status;
	private Date lastUpdateTime;
	private String updatedBy;
	private Date createTime;
	
	//
	public UserRoles(){
	}
	
	/** minimal constructor */
	public UserRoles(String userId) {
		this.userId = userId;
	}

	/** full constructor */
	public UserRoles(String userId, String userName, String roleId,
			String role, String status, Date lastUpdateTime, String updatedBy) {
		this.userId = userId;
		this.userName = userName;
		this.roleId = roleId;
		this.role = role;
		this.status = status;
		this.lastUpdateTime = lastUpdateTime;
		this.updatedBy = updatedBy;
	}

	@Override
	public void initialiseRow(Object[] m) {
		int i = -1;
		
		this.userId = m[++i].toString();
		this.userName = m[++i].toString();
		this.role = m[++i].toString();
		this.status = m[++i].toString();
		this.lastUpdateTime = (Date) m[++i];
		this.updatedBy = m[++i].toString();
		this.createTime = (Date) m[++i];
	}

	@Override
	public void insertRow() {
		Session session = ReportDbHibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		session.saveOrUpdate(this);
		session.getTransaction().commit();
		session.close();
	}

	@Override
	public void printRow() {
		System.out.println(this.userId + "," + this.userName + ","
				+ this.roleId + "," + this.role + "," + this.status + ","
				+ this.lastUpdateTime + "," + this.updatedBy);
	}

	// Property accessors

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRoleId() {
		return this.roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getRole() {
		return this.role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getLastUpdateTime() {
		return this.lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public String getUpdatedBy() {
		return this.updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
	
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}


	@Override
	public void transfromRow() {
		// TODO Auto-generated method stub
		
	}


}
