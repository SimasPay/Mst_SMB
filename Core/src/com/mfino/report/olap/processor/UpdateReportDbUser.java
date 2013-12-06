package com.mfino.report.olap.processor;

import org.hibernate.Session;

import com.mfino.report.olap.domain.Users;

public class UpdateReportDbUser {
	
	public String update(String username){
		String password = new PasswordGenerator().generate();
		Users urs = new Users(username);
        urs.setPassword(password);
		Session session = ReportDbHibernateUtil.getSessionFactory()
				.openSession();
    	session.beginTransaction();
		session.update(urs);
		session.getTransaction().commit();
        session.close();
        return password;
	}

	public static void main(String args[]){
		System.out.println("Updating User");
		UpdateReportDbUser ub = new UpdateReportDbUser();
		ub.update("user");
		System.out.println("Successfully Updated User");
	}
}
