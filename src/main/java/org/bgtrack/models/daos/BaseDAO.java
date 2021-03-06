package org.bgtrack.models.daos;

import org.bgtrack.utils.HibernateUtil;
import org.hibernate.Session;

public class BaseDAO {
	
	public static Session getCurrentSession() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
			
		return session;
	}
	
	public static Session getNewSession() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		
		return session;
	}
	
}