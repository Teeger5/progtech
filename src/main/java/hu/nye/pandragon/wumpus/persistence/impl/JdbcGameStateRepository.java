package hu.nye.pandragon.wumpus.persistence.impl;

import hu.nye.pandragon.wumpus.model.LevelVO;
import hu.nye.pandragon.wumpus.model.PlayernameVO;
import hu.nye.pandragon.wumpus.persistence.GameStateRepository;
import hu.nye.pandragon.wumpus.service.util.DotenvUtil;
import hu.nye.pandragon.wumpus.xml.XmlLevelConverter;
import jakarta.xml.bind.JAXBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Ez az osztály egy pálya adatbázisban való tárolsását írja le
 * Van lehetőség
 *  - mentésre,
 *   - lekérdezésre, és
 *    - a toplistához szükséges adat lekérdezésére
 */
public class JdbcGameStateRepository implements GameStateRepository {

	private static final Logger LOGGER = LoggerFactory.getLogger(JdbcGameStateRepository.class);

	/**
	 * Játékállás mentéséhez szükséges
	 * A pálya XML-formátumú reprezentációja kerül az adatbázisba,
	 * hogy könnyebb legyen a feldolgozása és a tárolás is
	 * Ha már van a felhasználónak mentett játékállása,
	 * akkor felülírja azt
	 */
	static final String INSERT_GAMESTATE_STATEMENT = "INSERT INTO gamestates (player, level) VALUES (?, ?) ON DUPLICATE KEY UPDATE level = ?;";
	/**
	 * A pályák a felhasználók ID nevű azonosítójához társítva vannak az adatbázisban,
	 * ezért ezt a könnyebbség miatt érdemes külön lekérdezni mentéshez
	 * Egy triggerrel is megoldható lenne ez igazából,
	 * ahol a trigger behelyettesítené a lekérdezésbe a helyes azonosítót a megadott név alapján,
	 * habár biztonsági problémák felmerülhetnek ekkor
	 * De érthetőbb lehet, ha itt van leírva minden a folyamat menetéhez
	 */
	static final String SELECT_USERID_STATEMENT = "SELECT id FROM players WHERE name = ?;";
	/**
	 * A toplista összeállításához szükség van a játékosok nevére és pontszámára
	 */
	static final String SELECT_PLAYERS_SCORE_STATEMENT = "SELECT name, score FROM players;";
	/**
	 * A felhasználó játékállásának lekérdezése
	 */
	private static final String SELECT_USER_GAMESTATE = "SELECT level FROM gamestates WHERE player = ?;";
	/**
	 * Új játékos hozzáadása az adatbázishoz
	 * Nem módosítja, ha már létezik ilyen nevű
	 */
	private static final String INSERT_NEW_USER_STATEMENT = "INSERT IGNORE INTO players (name) VALUES (?);";

	private Connection connection;

	public JdbcGameStateRepository() throws SQLException {
		var dotenv = new DotenvUtil();
		this.connection = DriverManager.getConnection(dotenv.get("DBLINK"), dotenv.get("DBUSER"), dotenv.get("DBPASS"));
	}

	@Override
	public void save(PlayernameVO playername, LevelVO levelVO) {
		try {
			insertGameState(playername, levelVO);
		} catch (SQLException | JAXBException e) {
			e.printStackTrace();
			LOGGER.error("Hiba a játékiállás mentésekor '{}' felhasználónak: {}", playername, e.getMessage());
		}
	}

	public Map<PlayernameVO, Integer> getScoreboard() {
		var map = new LinkedHashMap<PlayernameVO, Integer>();
		try (var statement = connection.prepareStatement(SELECT_PLAYERS_SCORE_STATEMENT)) {
			var resultSet = statement.executeQuery();
			while (resultSet.next()) {
				map.put(new PlayernameVO(resultSet.getString(1)), resultSet.getInt(2));
			}
			resultSet.close();
		} catch (SQLException e) {
			e.printStackTrace();
			LOGGER.error("Hiba a pontszámok lekérdezésekor: " + e.getMessage());
		}
		return map;
	}


	@Override
	public LevelVO load(PlayernameVO playername) {
		try (var statement = connection.prepareStatement(SELECT_USER_GAMESTATE)) {
			statement.setString(1, playername.toString());
			var resultSet = statement.executeQuery();
			resultSet.next();
			var level = resultSet.getString(1);
			var levelVO = XmlLevelConverter.toLevelVO(level);
			return levelVO;
		} catch (SQLException | JAXBException e) {
			var msg = "Hiba történt a pálya betöltésekor";
			LOGGER.error("{}: {}", msg, e.getMessage());
			throw new RuntimeException(msg);
		}
	}

	@Override
	public void close() {
		try {
			connection.close();
		} catch (SQLException e) {
			LOGGER.error("Hiba az erőforrások bezárásakor: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void insertGameState(PlayernameVO playername, LevelVO levelVO) throws SQLException, JAXBException {
		try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_GAMESTATE_STATEMENT)) {
			var playerID = getPlayerID(playername);
			var levelXml = XmlLevelConverter.toXML(levelVO);
			preparedStatement.setInt(1, playerID);
			preparedStatement.setString(2, levelXml);
			preparedStatement.setString(3, levelXml);
			preparedStatement.executeUpdate();
		}
	}

	private int getPlayerID (PlayernameVO playername) throws SQLException {
		try (PreparedStatement statement = connection.prepareStatement(SELECT_USERID_STATEMENT)) {
			insertNewPlayer(playername);
			statement.setString(1, playername.toString());
			var resultSet = statement.executeQuery();
//			if (!resultSet.next()) {}
			resultSet.next();
			return resultSet.getInt(1);
		}
	}

	/**
	 * Játékos hozzáadása az adatbázishoz, ha még nem létezik
	 * Ha már létezik, akkor nem változik
	 * @param playername
	 * @throws SQLException
	 */
	private void insertNewPlayer (PlayernameVO playername) throws SQLException {
		try (var statement = connection.prepareStatement(INSERT_NEW_USER_STATEMENT)) {
			statement.setString(1, playername.toString());
			statement.executeQuery();
		}
	}
}
