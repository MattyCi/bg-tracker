package org.bgtrack.models.daos;

import java.util.List;

import org.bgtrack.models.Game;
import org.bgtrack.utils.HibernateUtil;
import org.hibernate.Session;

/**
 * This DAO contains methods for performing CRUD operations on games.
 * @author Matt
 */
public class GameDAO {
	public static List<Game> getAllGames() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
			
		String query = "from Game order by GAME_NAME ASC";
		@SuppressWarnings("unchecked")
		List<Game> listOfGames = (List<Game>) session.createQuery(query).list();
		session.getTransaction().commit();
		
		return listOfGames;
	}

	public static Game getGameById(int gameID) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		String query = "from Game where GAME_ID=:gameID";
		Game game = (Game) session.createQuery(query).setParameter("gameID", gameID).uniqueResult();
		session.getTransaction().commit();
		
		return game;
	}
	
}