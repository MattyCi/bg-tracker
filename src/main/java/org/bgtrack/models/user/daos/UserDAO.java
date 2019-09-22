package org.bgtrack.models.user.daos;

import org.hibernate.Session;

import java.util.List;

import org.bgtrack.models.user.Reguser;
import org.bgtrack.utils.HibernateUtil;

/**
 * This DAO contains methods for performing CRUD operations on users.
 * @author Matt
 */
public class UserDAO {
	public static Reguser getUserByEmail(String email) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		
		String query = "from Reguser where email=:email";
		Reguser user = (Reguser) session.createQuery(query)
				.setParameter("email", email).uniqueResult();
		session.getTransaction().commit();
		session.close();
		return user;
	}
	
	public static Reguser getUserByID(String userId) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		
		String query = "from Reguser where userId=:userId";
		Reguser user = (Reguser) session.createQuery(query)
				.setParameter("userId", userId).uniqueResult();
		session.getTransaction().commit();
		return user;
	}
	
	public static List<Reguser> getAllUsers() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
			
		String query = "from Reguser order by FIRST_NAME ASC";
		@SuppressWarnings("unchecked")
		List<Reguser> listOfUsers = (List<Reguser>) session.createQuery(query).list();
		session.getTransaction().commit();

		return listOfUsers;
	}
	
}
