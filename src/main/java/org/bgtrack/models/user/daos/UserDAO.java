package org.bgtrack.models.user.daos;

import org.hibernate.Session;

import java.util.List;

import org.bgtrack.models.user.AccountRedeemToken;
import org.bgtrack.models.user.Reguser;
import org.bgtrack.utils.HibernateUtil;

/**
 * This DAO contains methods for performing CRUD operations on users.
 * @author Matt
 */
public class UserDAO {
	public static Reguser getUserByUsername(String username) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		
		String query = "from Reguser where username=:username";
		Reguser user = (Reguser) session.createQuery(query)
				.setParameter("username", username).uniqueResult();
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
	
	/**
	 * TODO: Use a contains function instead and set up an index on these columns for much better performance...
	 */
	public static List<Reguser> getUsersByUsername(String username) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		
		String query = "from Reguser where username like :username";
		
		List<Reguser> matchedUsers = (List<Reguser>) session.createQuery(query)
				.setParameter("username", "%"+username+"%").list();
		
		session.getTransaction().commit();
		session.close();
		
		return matchedUsers;
	}
	
	public static List<AccountRedeemToken> getAccountRedeemTokensUserCreated(String userId) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		
		String query = "from AccountRedeemToken where CREATOR=:userId";
		
		List<AccountRedeemToken> matchedUsers = (List<AccountRedeemToken>) session.createQuery(query)
				.setParameter("userId", userId).list();
		
		session.getTransaction().commit();
		session.close();
		
		return matchedUsers;
	}
	
	public static AccountRedeemToken getAccountRedeemToken(String token) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		
		String query = "from AccountRedeemToken where redeemToken=:token";
		
		AccountRedeemToken tokenResult = (AccountRedeemToken) session.createQuery(query)
				.setParameter("token", token).uniqueResult();
		
		session.getTransaction().commit();
		session.close();
		
		return tokenResult;
	}
	
}
