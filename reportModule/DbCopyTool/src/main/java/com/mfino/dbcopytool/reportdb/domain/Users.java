package com.mfino.dbcopytool.reportdb.domain;

import org.hibernate.Session;

import com.mfino.dbcopytool.persistence.ReportDbHibernateUtil;

public class Users extends TableRow implements java.io.Serializable {

	// Fields

	private String username;
	private String password;
	private Boolean enabled;

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	// Constructors

	/** default constructor */
	public Users() {
	}

	/** minimal constructor */
	public Users(String username) {
		this.username = username;
	}

	/** full constructor */
	public Users(String username, String password, Boolean enabled) {
		this.username = username;
		this.password = password;
		this.enabled = enabled;
	}

	@Override
	public void initialiseRow(Object[] m) {
		int i = -1;
		username = m[++i].toString();
		transfromRow();
	}

	@Override
	public void transfromRow() {
		password = generatePassword();
		enabled = true;
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
		System.out.println(username + "," + password + "," + enabled);
	}

	public String generatePassword() {
		int passwordLength = 14;
		StringBuffer sb = new StringBuffer();
		for (int x = 0; x < passwordLength; x++) {
			sb.append((char) ((int) (Math.random() * 26) + 97));
		}
		return sb.toString();
	}

}
