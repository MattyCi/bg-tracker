package org.bgtrack.utils;

import java.sql.Timestamp;
import java.time.Instant;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bgtrack.actions.season.SeasonDelete;
import org.bgtrack.models.Season;
import org.bgtrack.models.SeasonStanding;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

	private static final Logger LOG = LogManager.getLogger(HibernateUtil.class);
	
	private static final SessionFactory SESSION_FACTORY = buildSessionFactory();

	private static SessionFactory buildSessionFactory() {
		try {
			// Create the SessionFactory from hibernate.cfg.xml
			return new Configuration().configure().buildSessionFactory();
		} catch (Throwable ex) {
			// Make sure you log the exception, as it might be swallowed
			LOG.fatal("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static SessionFactory getSessionFactory() {
		return SESSION_FACTORY;
	}

	public static Timestamp getCurrentTimeStamp() {
		return Timestamp.from(Instant.now());
	}
	
	public static Boolean persistObject(Object obj) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Transaction tx = null;
		Boolean errorsOccured = false;
		
		try {
			tx = session.beginTransaction();
			EntityManagerFactory emf = session.getEntityManagerFactory();
			EntityManager em = emf.createEntityManager();
			em.getTransaction().begin();
			em.persist(obj);
			em.getTransaction().commit();
			em.close();
			tx.commit();
		} catch (HibernateException e) {
			tx.rollback();
			LOG.error("Hibernate error occured: "+e);
			errorsOccured = true;
			throw e;
		} finally {
			session.close();
		}
		return errorsOccured;
	}
	
	public static void updateObject(Object obj) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.update(obj);
			tx.commit();
		} catch (HibernateException e) {
			tx.rollback();
			LOG.error("Hibernate error occured: "+e);
			throw e;
		} finally {
			session.close();
		}
	}

	public static void deleteEntity(Object obj) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.delete(obj);
			tx.commit();
		} catch (HibernateException e) {
			tx.rollback();
			LOG.error("Hibernate error occured: "+e);
			throw e;
		} finally {
			session.close();
		}
	}
	
}