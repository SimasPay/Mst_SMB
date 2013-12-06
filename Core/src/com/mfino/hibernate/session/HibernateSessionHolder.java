package com.mfino.hibernate.session;


import org.hibernate.Session;

public class HibernateSessionHolder 
{
	private ThreadLocal<Session> _session = new ThreadLocal<Session>();

	public Session getSession() {
		return _session.get();
	}

	public void setSession(Session session) {
		_session.set(session);
	}
	
}
