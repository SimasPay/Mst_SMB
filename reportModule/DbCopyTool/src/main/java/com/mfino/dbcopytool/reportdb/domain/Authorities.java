package com.mfino.dbcopytool.reportdb.domain;

import org.hibernate.Session;

import com.mfino.dbcopytool.persistence.ReportDbHibernateUtil;

public class Authorities extends TableRow implements java.io.Serializable {

	// Fields

	private String username;
	private String authority;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

	// Constructors

	/** default constructor */
	public Authorities() {
	}

	/** minimal constructor */
	public Authorities(String username) {
		this.username = username;
	}

	/** full constructor */
	public Authorities(String username, String authority) {
		this.username = username;
		this.authority = authority;
	}

	@Override
	public void initialiseRow(Object[] m) {
		int i = -1;
		username = m[++i].toString();
		transfromRow();
	}

	@Override
	public void transfromRow() {
		authority = "ROLE_USER";
	}

	@Override
	public void insertRow() {
		Session session = ReportDbHibernateUtil.getSessionFactory()
				.openSession();
		session.beginTransaction();
		session.saveOrUpdate(this);
		session.getTransaction().commit();
		session.close();
	}

	@Override
	public void printRow() {
		System.out.println(username + "," + authority);
	}

}
