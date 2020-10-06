package org.bgtrack.models.daos;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;

import org.bgtrack.models.Round;
import org.bgtrack.models.RoundResult;
import org.bgtrack.models.Season;
import org.bgtrack.models.user.Reguser;
import org.bgtrack.utils.HibernateUtil;
import org.bgtrack.utils.PropertiesLoader;
import org.bgtrack.utils.UserUtils;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;

import PropertiesSelection.PropertiesSelector;

/**
 * This DAO contains methods for performing CRUD operations on rounds.
 * @author Matt
 */
public class RoundDAO {
	public static List<Round> getRoundsBySeasonId(int seasonId, Boolean eagerLoad) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		String query = "from Round where SEASON_ID=:seasonId order by ROUND_DATE asc";
		@SuppressWarnings("unchecked")
		List<Round> rounds = (List<Round>) session.createQuery(query).setParameter("seasonId", seasonId).list();

		if (eagerLoad) {
			for (Round round : rounds) {
				Hibernate.initialize(round.getRoundResults()); // load the rounds results as well
			}
		}
		
		session.getTransaction().commit();
		
		return rounds;
	}
	
	/*
	 * Gets the victors for a specified round. It is possible for there to be
	 * more than one victor per round, ie. in the event of a tie.
	 */
	public static List<Reguser> getVictorsForRound(int roundId) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		
		String query = "from Reguser as user where user.id in"
				+ "(select rr.reguser.id from RoundResult as rr where rr.place = 1 and rr.round.roundId=:roundId)";
		@SuppressWarnings("unchecked")
		List<Reguser> victors = (List<Reguser>) session.createQuery(query).setParameter("roundId", roundId).list();
		
		session.getTransaction().commit();
		
		return victors;
	}
	
	public static List<RoundResult> getRoundResultsForUserBySeasonId(BigInteger bigInteger, String userId) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		String query = "from RoundResult as rr where rr.round.roundId in"
				+ "(select r.roundId from Round as r where r.season.seasonId=:seasonId) and rr.reguser.userId=:userId";		

		@SuppressWarnings("unchecked")
		List<RoundResult> rounds = (List<RoundResult>) session.createQuery(query).setParameter("seasonId", bigInteger)
				.setParameter("userId", userId).list();
		
		session.getTransaction().commit();
		
		return rounds;
	}
	
	public static List<Round> getRoundsForUserBySeasonId(BigInteger seasonId, String userId) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		
		String query = "from Round as r where r.roundId in "
				+ "(select rr.round.roundId from RoundResult as rr where rr.reguser.userId=:userId) and r.season.seasonId=:seasonId";

		@SuppressWarnings("unchecked")
		List<Round> rounds = (List<Round>) session.createQuery(query).setParameter("seasonId", seasonId)
				.setParameter("userId", userId).list();
		
		for (Round round : rounds) {
			Hibernate.initialize(round.getRoundResults());
		}
		
		session.getTransaction().commit();
		
		return rounds;
	}
	
	public static Round getRoundById(String roundId) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		String query = "from Round where ROUND_ID=:roundId";
		Round round = (Round) session.createQuery(query).setParameter("roundId", roundId).uniqueResult();
		
		Hibernate.initialize(round.getSeason().getRounds());
		Hibernate.initialize(round.getSeason().getSeasonStandings());
		
		session.getTransaction().commit();
		
		return round;
	}
	
	public static List<Round> getPaginatedRoundsList(int page, String seasonId) throws NumberFormatException, IOException {
		
		int resultsPerPage = Integer.parseInt(PropertiesLoader.getPropertyValue("NUM_ROUNDS_PER_PAGE", PropertiesSelector.ROUND));
		int offset = page * resultsPerPage;
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		
		String query = "from Round where SEASON_ID=:seasonId order by ROUND_DATE asc";
		
		@SuppressWarnings("unchecked")
		 Query finalQuery = session.createQuery(query).setParameter("seasonId", seasonId).setFirstResult(offset).setMaxResults(resultsPerPage);
		
		 List<Round> listOfRounds = (List<Round>) finalQuery.list();
		 
		 for (Round round : listOfRounds) {
			 Hibernate.initialize(round.getRoundResults());
		}
		
		session.getTransaction().commit();

		return listOfRounds;
		
	}
	
}