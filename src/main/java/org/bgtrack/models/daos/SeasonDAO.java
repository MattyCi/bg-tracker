package org.bgtrack.models.daos;

import java.util.List;

import org.bgtrack.models.Round;
import org.bgtrack.models.Season;
import org.bgtrack.utils.HibernateUtil;
import org.hibernate.Hibernate;
import org.hibernate.Session;

/**
 * This DAO contains methods for performing CRUD operations on seasons.
 * @author Matt
 */
public class SeasonDAO {
	public static List<Season> getAllSeasons() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
			
		String query = "from Season order by NAME ASC";
		@SuppressWarnings("unchecked")
		List<Season> listOfSeasons = (List<Season>) session.createQuery(query).list();
		session.getTransaction().commit();

		return listOfSeasons;
	}

	/**
	 * retrieves season by id
	 * @param seasonId
	 * @param eagerLoad - pass true to load all of the season's round data as well
	 * @return
	 */
	public static Season getSeasonById(int seasonId, Boolean eagerLoad) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		String query = "from Season where SEASON_ID=:seasonId";
		Season season = (Season) session.createQuery(query).setParameter("seasonId", seasonId).uniqueResult();
		
		if (eagerLoad) {
			Hibernate.initialize(season.getRounds()); // load the rounds for the season as well
			for (Round round : season.getRounds()) {
				Hibernate.initialize(round.getRoundResults());
			}
		}
				
		session.getTransaction().commit();
		
		return season;
	}
	
}