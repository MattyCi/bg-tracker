package org.bgtrack.models.daos;

import java.util.List;

import org.bgtrack.models.Game;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This DAO contains methods for performing CRUD operations on games.
 * @author Matt
 */
public class GameDAO {
	
	private static final Logger LOG = LoggerFactory.getLogger(GameDAO.class);
	
	public static List<Game> getAllGames() {
		Session session = BaseDAO.getCurrentSession();
		
		List<Game> listOfGames;
		
		try {
			
			String query = "from Game order by GAME_NAME ASC";
			listOfGames = (List<Game>) session.createQuery(query).list();
			session.getTransaction().commit();
			
		} catch (Exception e) {
			
			LOG.error("Unexpected error occurred ", e);
			session.getTransaction().rollback();
			return null;
			
		} finally {
			session.close();
		}
		
		return listOfGames;
	}

	public static Game getGameById(int gameID) {
		Session session = BaseDAO.getCurrentSession();
		
		Game game = null;
		
		try {
		
			String query = "from Game where GAME_ID=:gameID";
			game = (Game) session.createQuery(query).setParameter("gameID", gameID).uniqueResult();
			session.getTransaction().commit();
		
		} catch (Exception e) {
			
			LOG.error("Unexpected error occurred ", e);
			session.getTransaction().rollback();
			return null;
			
		} finally {
			session.close();
		}
		
		return game;
	}
	
}