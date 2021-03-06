package org.bgtrack.models.daos;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import PropertiesSelection.PropertiesSelector;

/**
 * This DAO contains methods for performing CRUD operations on seasons.
 * @author Matt
 */
public class SeasonDAO extends BaseDAO {
	
	private static final Logger LOG = LoggerFactory.getLogger(SeasonDAO.class);
	
	public static List<Season> getAllSeasons() {
		
		Session session = BaseDAO.getCurrentSession();
		List<Season> listOfSeasons;
		
		try {
			
			String query = "from Season order by START_DATE DESC";
			listOfSeasons = (List<Season>) session.createQuery(query).list();
			session.getTransaction().commit();
			
		} catch (Exception e) {
			
			session.getTransaction().rollback();
			throw e;
			
		} finally {
			session.close();
		}

		return listOfSeasons;
	}
	
	public static Long getCountOfAllSeasons() {
		Session session = BaseDAO.getCurrentSession();
		
		Long count;
		
		try {
			Query query = session.createQuery(
			        "select count(*) from Season");
			count = (Long)query.uniqueResult();
			
			session.getTransaction().commit();
		} catch (Exception e) {
			
			LOG.error("Unexpected error occurred ", e);
			session.getTransaction().rollback();
			throw e;
			
		} finally {
			session.close();
		}

		return count;
	}
	
	public static List<Season> getAllSeasonsUserIsIn() {
		Session session = BaseDAO.getCurrentSession();
		
		List<Season> listOfSeasons;
		
		try {
			
			String currentUserId = UserUtils.getCurrentUserId();
			
			String query = "from Season as season where season.seasonId in "
					+ "(select ss.season.seasonId from SeasonStanding as ss where ss.reguser.userId=:currentUserId) "
					+ "or season.creator.userId=:currentUserId "
					+ "order by NAME ASC";
			listOfSeasons = (List<Season>) session.createQuery(query).setParameter("currentUserId", currentUserId).list();
			session.getTransaction().commit();
			
		} catch (Exception e) {
			
			LOG.error("Unexpected error occurred ", e);
			session.getTransaction().rollback();
			throw e;
			
		} finally {
			session.close();
		}

		return listOfSeasons;
	}
	
	public static Long getCountOfAllSeasonsUserIsIn() {
		Session session = BaseDAO.getCurrentSession();
		
		Long count;
		
		try {
			
			String currentUserId = UserUtils.getCurrentUserId();
			
			Query query = session.createQuery(
			        "select count(*) from Season as season where season.seasonId in "
			        + "(select ss.season.seasonId from SeasonStanding as ss where ss.reguser.userId=:currentUserId) "
			        + "or season.creator.userId=:currentUserId");
			query.setString("currentUserId", currentUserId);
			count = (Long)query.uniqueResult();

			session.getTransaction().commit();
			
		} catch (Exception e) {
			
			LOG.error("Unexpected error occurred ", e);
			session.getTransaction().rollback();
			throw e;
			
		} finally {
			session.close();
		}
		
		return count;
	}

	/**
	 * retrieves season by id
	 * @param seasonId
	 * @param eagerLoad - pass true to load all of the season's round data as well
	 * @return
	 */
	public static Season getSeasonById(int seasonId, Boolean eagerLoad) {
		Session session = BaseDAO.getCurrentSession();
		
		Season season;
		
		try {
			
			String query = "from Season where SEASON_ID=:seasonId";
			season = (Season) session.createQuery(query).setParameter("seasonId", seasonId).uniqueResult();
			
			if (eagerLoad) {
				Hibernate.initialize(season.getRounds()); // load the rounds for the season as well
				Hibernate.initialize(season.getSeasonStandings());
				for (Round round : season.getRounds()) {
					Hibernate.initialize(round.getRoundResults());
				}
			}
					
			session.getTransaction().commit();
			
		} catch (Exception e) {
			
			LOG.error("Unexpected error occurred ", e);
			session.getTransaction().rollback();
			throw e;
			
		} finally {
			session.close();
		}
				
		return season;
	}
	
	public static List<Reguser> getAllUsersInSeason(BigInteger seasonId) {
		Session session = BaseDAO.getCurrentSession();
		
		List<Reguser> listOfUsersInSeason;
		
		try {
			
			//TODO: Refactor... you can use season standing table now
			String query = "from Reguser as user where user.userId in "
					+ "(select rr.reguser.userId from RoundResult as rr where rr.round.roundId in"
					+ "(select r.roundId from Round as r where r.season.seasonId=:seasonId))";

			listOfUsersInSeason = (List<Reguser>) session.createQuery(query)
				.setParameter("seasonId", seasonId).list();
			
			session.getTransaction().commit();
			
		} catch (Exception e) {
			
			LOG.error("Unexpected error occurred ", e);
			session.getTransaction().rollback();
			throw e;
			
		} finally {
			session.close();
		}

		return listOfUsersInSeason;
	}
	
	public static List<SeasonStanding> getSeasonStandingForUserInSeason(BigInteger seasonId, String userId) {
		Session session = BaseDAO.getCurrentSession();
		
		List<SeasonStanding> seasonStandings;
		
		try {
			
			String query = "from SeasonStanding where USER_ID=:userId AND SEASON_ID=:seasonId";

			seasonStandings = (List<SeasonStanding>) session.createQuery(query)
				.setParameter("userId", userId).setParameter("seasonId", seasonId).list();
			
			session.getTransaction().commit();
			
		} catch (Exception e) {
			
			LOG.error("Unexpected error occurred ", e);
			session.getTransaction().rollback();
			throw e;
			
		} finally {
			session.close();
		}
		
		return seasonStandings;
	}
	
	public static Long  getSumOfGamesPlayedForAllPlayers(BigInteger seasonId) {
		Session session = BaseDAO.getCurrentSession();
		
		Long  sumOfGamesPlayedForAllPlayers;
		
		try {
			
			String query = "select sum(ss.gamesPlayed) from SeasonStanding ss where SEASON_ID=:seasonId";
			
			Query sumQuery = session.createQuery(query).setParameter("seasonId", seasonId);
			
			sumOfGamesPlayedForAllPlayers = (Long) sumQuery.list().get(0);
			
			if (sumOfGamesPlayedForAllPlayers == null) {
				sumOfGamesPlayedForAllPlayers = Long.valueOf(0);
			}
			
			session.getTransaction().commit();
			
		} catch (Exception e) {
			
			LOG.error("Unexpected error occurred ", e);
			session.getTransaction().rollback();
			throw e;
			
		} finally {
			session.close();
		}
		
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
		
		Session session = BaseDAO.getCurrentSession();
		
		List<Season> listOfSeasons;
		
		try {
			
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
			
			listOfSeasons = (List<Season>) finalQuery.list();
			
			session.getTransaction().commit();
			
		} catch (Exception e) {
			
			LOG.error("Unexpected error occurred ", e);
			session.getTransaction().rollback();
			throw e;
			
		} finally {
			session.close();
		}

		return listOfSeasons;
		
	}

}