package org.bgtrack.models.daos;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.subject.Subject;
import org.bgtrack.models.Round;
import org.bgtrack.models.Season;
import org.bgtrack.models.SeasonStanding;
import org.bgtrack.models.user.Reguser;
import org.bgtrack.utils.HibernateUtil;
import org.bgtrack.utils.PropertiesLoader;
import org.bgtrack.utils.UserUtils;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;

import PropertiesSelection.PropertiesSelector;

/**
 * This DAO contains methods for performing CRUD operations on seasons.
 * @author Matt
 */
public class SeasonDAO {
	public static List<Season> getAllSeasons() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
			
		String query = "from Season order by START_DATE DESC";
		@SuppressWarnings("unchecked")
		List<Season> listOfSeasons = (List<Season>) session.createQuery(query).list();
		session.getTransaction().commit();

		return listOfSeasons;
	}
	
	public static Long getCountOfAllSeasons() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
			
		Query query = session.createQuery(
		        "select count(*) from Season");
		Long count = (Long)query.uniqueResult();
		
		session.getTransaction().commit();

		return count;
	}
	
	public static List<Season> getAllSeasonsUserIsIn() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		
		String currentUserId = UserUtils.getCurrentUserId();
		
		String query = "from Season as season where season.seasonId in "
				+ "(select ss.season.seasonId from SeasonStanding as ss where ss.reguser.userId=:currentUserId) "
				+ "or season.creator.userId=:currentUserId "
				+ "order by NAME ASC";
		@SuppressWarnings("unchecked")
		List<Season> listOfSeasons = (List<Season>) session.createQuery(query).setParameter("currentUserId", currentUserId).list();
		session.getTransaction().commit();

		return listOfSeasons;
	}
	
	public static Long getCountOfAllSeasonsUserIsIn() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		
		String currentUserId = UserUtils.getCurrentUserId();
		
		Query query = session.createQuery(
		        "select count(*) from Season as season where season.seasonId in "
		        + "(select ss.season.seasonId from SeasonStanding as ss where ss.reguser.userId=:currentUserId) "
		        + "or season.creator.userId=:currentUserId");
		query.setString("currentUserId", currentUserId);
		Long count = (Long)query.uniqueResult();

		session.getTransaction().commit();
		
		return count;
	}

	/**
	 * retrieves season by id
	 * @param seasonId
	 * @param eagerLoad - pass true to load all of the season's round data as well
	 * @return
	 */
	public static Season getSeasonById(int seasonId, Boolean eagerLoad) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		String query = "from Season where SEASON_ID=:seasonId";
		Season season = (Season) session.createQuery(query).setParameter("seasonId", seasonId).uniqueResult();
		
		if (eagerLoad) {
			Hibernate.initialize(season.getRounds()); // load the rounds for the season as well
			Hibernate.initialize(season.getSeasonStandings());
			for (Round round : season.getRounds()) {
				Hibernate.initialize(round.getRoundResults());
			}
		}
				
		session.getTransaction().commit();
		
		session.close();
		
		return season;
	}
	
	public static List<Reguser> getAllUsersInSeason(BigInteger seasonId) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		
		//TODO: Refactor... you can use season standing table now
		String query = "from Reguser as user where user.userId in "
				+ "(select rr.reguser.userId from RoundResult as rr where rr.round.roundId in"
				+ "(select r.roundId from Round as r where r.season.seasonId=:seasonId))";
		@SuppressWarnings("unchecked")
		List<Reguser> listOfUsersInSeason = (List<Reguser>) session.createQuery(query)
			.setParameter("seasonId", seasonId).list();
		
		session.getTransaction().commit();

		return listOfUsersInSeason;
	}
	
	public static List<SeasonStanding> getSeasonStandingForUserInSeason(BigInteger seasonId, String userId) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		
		String query = "from SeasonStanding where USER_ID=:userId AND SEASON_ID=:seasonId";

		List<SeasonStanding> seasonStandings = (List<SeasonStanding>) session.createQuery(query)
			.setParameter("userId", userId).setParameter("seasonId", seasonId).list();
		
		session.getTransaction().commit();

		return seasonStandings;
	}
	
	public static Long  getSumOfGamesPlayedForAllPlayers(BigInteger seasonId) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		
		String query = "select sum(ss.gamesPlayed) from SeasonStanding ss where SEASON_ID=:seasonId";
		
		Query sumQuery = session.createQuery(query).setParameter("seasonId", seasonId);
		
		Long  sumOfGamesPlayedForAllPlayers = (Long) sumQuery.list().get(0);
		
		if (sumOfGamesPlayedForAllPlayers == null) {
			sumOfGamesPlayedForAllPlayers = Long.valueOf(0);
		}
		
		session.getTransaction().commit();
		
		return sumOfGamesPlayedForAllPlayers;
	}
	
	public static void markSeasonAsInactive(int seasonId) {

		Season seasonToDeactivate = SeasonDAO.getSeasonById(seasonId, false);
		
		seasonToDeactivate.setStatus("I");
		
		HibernateUtil.updateObject(seasonToDeactivate);
		
	}
	
	public static List<Season> getPaginatedSeasonList(int page, boolean isUserSeasonsOnly) throws Exception {
		
		int resultsPerPage = Integer.parseInt(PropertiesLoader.getPropertyValue("NUM_SEASONS_PER_PAGE", PropertiesSelector.SEASON));
		int offset = page * resultsPerPage;
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		
		String query;
		
		if (isUserSeasonsOnly) {
			
			Subject currentUser = SecurityUtils.getSubject();
			
			if (!currentUser.isAuthenticated()) {
				throw new AuthorizationException("You must be logged in to perform this action.");
			}
			
			query = "from Season as season where season.seasonId in "
				+ "(select ss.season.seasonId from SeasonStanding as ss where ss.reguser.userId=:currentUserId) "
				+ "or season.creator.userId=:currentUserId "
				+ "order by NAME ASC";
		} else {
			query = "from Season order by START_DATE DESC";
		}
		
		@SuppressWarnings("unchecked")
		 Query finalQuery = session.createQuery(query).setFirstResult(offset).setMaxResults(resultsPerPage);
		
		if (isUserSeasonsOnly) {
			finalQuery.setParameter("currentUserId", UserUtils.getCurrentUserId());
		}
		
		 List<Season> listOfSeasons = (List<Season>) finalQuery.list();
		
		session.getTransaction().commit();

		return listOfSeasons;
		
	}

}