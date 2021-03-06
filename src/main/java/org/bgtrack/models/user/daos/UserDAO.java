package org.bgtrack.models.user.daos;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import org.bgtrack.models.daos.BaseDAO;
import org.bgtrack.models.user.AccountRedeemToken;
import org.bgtrack.models.user.Reguser;
import org.bgtrack.utils.HibernateUtil;

/**
 * This DAO contains methods for performing CRUD operations on users.
 * @author Matt
 */
public class UserDAO {
	
	private static final Logger LOG = LoggerFactory.getLogger(UserDAO.class);
	
	public static Reguser getUserByUsername(String username) {
		Session session = BaseDAO.getNewSession();
		
		Reguser user;
		
		try {
			
			String query = "from Reguser where username=:username";
			user = (Reguser) session.createQuery(query)
					.setParameter("username", username).uniqueResult();
			session.getTransaction().commit();
			
		} catch (Exception e) {
			
			LOG.error("Unexpected error occurred ", e);
			session.getTransaction().rollback();
			throw e;
			
		} finally {
			session.close();
		}
		
		return user;
	}
	
	public static Reguser getUserByID(String userId) {
		Session session = BaseDAO.getCurrentSession();
		
		Reguser user;
		
		try {
			
			String query = "from Reguser where userId=:userId";
			user = (Reguser) session.createQuery(query)
					.setParameter("userId", userId).uniqueResult();
			session.getTransaction().commit();
			
		} catch (Exception e) {
			
			LOG.error("Unexpected error occurred ", e);
			session.getTransaction().rollback();
			throw e;
			
		} finally {
			session.close();
		}

		return user;
	}
	
	public static List<Reguser> getAllUsers() {
		Session session = BaseDAO.getCurrentSession();
		
		List<Reguser> listOfUsers;
		
		try {
			
			String query = "from Reguser order by FIRST_NAME ASC";
			
			listOfUsers = (List<Reguser>) session.createQuery(query).list();
			session.getTransaction().commit();
			
		} catch (Exception e) {
			
			LOG.error("Unexpected error occurred ", e);
			session.getTransaction().rollback();
			throw e;
			
		} finally {
			session.close();
		}

		return listOfUsers;
	}
	
	/**
	 * TODO: Use a contains function instead and set up an index on these columns for much better performance...
	 */
	public static List<Reguser> getUsersByUsername(String username) {
		Session session = BaseDAO.getCurrentSession();
		
		List<Reguser> matchedUsers;
		
		try {
			
			String query = "from Reguser where username like :username";
			
			matchedUsers = (List<Reguser>) session.createQuery(query)
					.setParameter("username", "%"+username+"%").list();
			
			session.getTransaction().commit();
			
		} catch (Exception e) {
			
			LOG.error("Unexpected error occurred ", e);
			session.getTransaction().rollback();
			throw e;
			
		} finally {
			session.close();
		}
		
		return matchedUsers;
	}
	
	public static List<AccountRedeemToken> getAccountRedeemTokensUserCreated(String userId) {
		Session session = BaseDAO.getCurrentSession();
		
		List<AccountRedeemToken> matchedUsers;
		
		try {
			
			String query = "from AccountRedeemToken where CREATOR=:userId";
			
			matchedUsers = (List<AccountRedeemToken>) session.createQuery(query)
					.setParameter("userId", userId).list();
			
			session.getTransaction().commit();
			
		} catch (Exception e) {
			
			LOG.error("Unexpected error occurred ", e);
			session.getTransaction().rollback();
			throw e;
			
		} finally {
			session.close();
		}
		
		return matchedUsers;
	}
	
	public static AccountRedeemToken getAccountRedeemToken(String token) {
		Session session = BaseDAO.getCurrentSession();
		
		AccountRedeemToken tokenResult;
		
		try {
			
			String query = "from AccountRedeemToken where redeemToken=:token";
			
			tokenResult = (AccountRedeemToken) session.createQuery(query)
					.setParameter("token", token).uniqueResult();
			
			session.getTransaction().commit();
			
		} catch (Exception e) {
			
			LOG.error("Unexpected error occurred ", e);
			session.getTransaction().rollback();
			throw e;
			
		} finally {
			session.close();
		}
		
		return tokenResult;
	}
	
}
