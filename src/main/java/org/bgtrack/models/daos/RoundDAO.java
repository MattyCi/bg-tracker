package org.bgtrack.models.daos;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import org.bgtrack.models.Round;
import org.bgtrack.models.RoundResult;
import org.bgtrack.models.user.Reguser;
import org.bgtrack.utils.PropertiesLoader;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import PropertiesSelection.PropertiesSelector;

/**
 * This DAO contains methods for performing CRUD operations on rounds.
 * @author Matt
 */
public class RoundDAO {
	
	private static final Logger LOG = LoggerFactory.getLogger(RoundDAO.class);
	
	public static List<Round> getRoundsBySeasonId(int seasonId, Boolean eagerLoad) {
		Session session = BaseDAO.getCurrentSession();
		
		List<Round> rounds;
		
		try {
			
			String query = "from Round where SEASON_ID=:seasonId order by ROUND_DATE asc";
			rounds = (List<Round>) session.createQuery(query).setParameter("seasonId", seasonId).list();

			if (eagerLoad) {
				for (Round round : rounds) {
					Hibernate.initialize(round.getRoundResults()); // load the rounds results as well
				}
			}
			
			session.getTransaction().commit();
			
		} catch (Exception e) {
			
			LOG.error("Unexpected error occurred ", e);
			session.getTransaction().rollback();
			return null;
			
		} finally {
			session.close();
		}

		return rounds;
	}
	
	/*
	 * Gets the victors for a specified round. It is possible for there to be
	 * more than one victor per round, ie. in the event of a tie.
	 */
	public static List<Reguser> getVictorsForRound(int roundId) {
		Session session = BaseDAO.getCurrentSession();
		
		List<Reguser> victors;
		
		try {
			
			String query = "from Reguser as user where user.id in"
					+ "(select rr.reguser.id from RoundResult as rr where rr.place = 1 and rr.round.roundId=:roundId)";
			victors = (List<Reguser>) session.createQuery(query).setParameter("roundId", roundId).list();
			
			session.getTransaction().commit();
			
		} catch (Exception e) {
			
			LOG.error("Unexpected error occurred ", e);
			session.getTransaction().rollback();
			return null;
			
		} finally {
			session.close();
		}
		
		return victors;
	}
	
	public static List<RoundResult> getRoundResultsForUserBySeasonId(BigInteger bigInteger, String userId) {
		Session session = BaseDAO.getCurrentSession();
		
		List<RoundResult> rounds;
		
		try {
			
			String query = "from RoundResult as rr where rr.round.roundId in"
					+ "(select r.roundId from Round as r where r.season.seasonId=:seasonId) and rr.reguser.userId=:userId";		

			rounds = (List<RoundResult>) session.createQuery(query).setParameter("seasonId", bigInteger)
					.setParameter("userId", userId).list();
			
			session.getTransaction().commit();
			
		} catch (Exception e) {
			
			LOG.error("Unexpected error occurred ", e);
			session.getTransaction().rollback();
			return null;
			
		} finally {
			session.close();
		}
		
		return rounds;
	}
	
	public static List<Round> getRoundsForUserBySeasonId(BigInteger seasonId, String userId) {
		Session session = BaseDAO.getCurrentSession();
		
		List<Round> rounds;
		
		try {
			
			String query = "from Round as r where r.roundId in "
					+ "(select rr.round.roundId from RoundResult as rr where rr.reguser.userId=:userId) and r.season.seasonId=:seasonId";

			rounds = (List<Round>) session.createQuery(query).setParameter("seasonId", seasonId)
					.setParameter("userId", userId).list();
			
			for (Round round : rounds) {
				Hibernate.initialize(round.getRoundResults());
			}
			
			session.getTransaction().commit();
			
		} catch (Exception e) {
			
			LOG.error("Unexpected error occurred ", e);
			session.getTransaction().rollback();
			return null;
			
		} finally {
			session.close();
		}
		
		return rounds;
	}
	
	public static Round getRoundById(String roundId) {
		Session session = BaseDAO.getCurrentSession();
		
		Round round;
		
		try {
			
			String query = "from Round where ROUND_ID=:roundId";
			round = (Round) session.createQuery(query).setParameter("roundId", roundId).uniqueResult();
			
			Hibernate.initialize(round.getSeason().getRounds());
			Hibernate.initialize(round.getSeason().getSeasonStandings());
			
			session.getTransaction().commit();
			
		} catch (Exception e) {
			
			LOG.error("Unexpected error occurred ", e);
			session.getTransaction().rollback();
			return null;
			
		} finally {
			session.close();
		}
		
		return round;
	}
	
	public static List<Round> getPaginatedRoundsList(int page, String seasonId) throws NumberFormatException, IOException {
		
		int resultsPerPage = Integer.parseInt(PropertiesLoader.getPropertyValue("NUM_ROUNDS_PER_PAGE", PropertiesSelector.ROUND));
		int offset = page * resultsPerPage;
		
		Session session = BaseDAO.getCurrentSession();
		
		List<Round> listOfRounds;
		
		try {
			
			String query = "from Round where SEASON_ID=:seasonId order by ROUND_DATE asc";
			
			@SuppressWarnings("unchecked")
			 Query finalQuery = session.createQuery(query).setParameter("seasonId", seasonId).setFirstResult(offset).setMaxResults(resultsPerPage);
			
			listOfRounds = (List<Round>) finalQuery.list();
			 
			 for (Round round : listOfRounds) {
				 Hibernate.initialize(round.getRoundResults());
			}
			
			session.getTransaction().commit();
			
		} catch (Exception e) {
			
			LOG.error("Unexpected error occurred ", e);
			session.getTransaction().rollback();
			return null;
			
		} finally {
			session.close();
		}

		return listOfRounds;
		
	}
	
}