package hu.nye.pandragon.wumpus.persistence;

import hu.nye.pandragon.wumpus.model.LevelVO;
import hu.nye.pandragon.wumpus.model.PlayernameVO;

public interface GameStateRepository {
	/**
	 * Játékállás mentése egy felhaszálóhoz társítva
	 * @param playername a játékos neve
	 * @param levelVO a pálya
	 */
	void save (PlayernameVO playername, LevelVO levelVO);

	/**
	 * Játékállás betöltése
	 * @param playername a játékos neve
	 * @return a pálya, ami mentve volt
	 */
	LevelVO load (PlayernameVO playername);

	/**
	 * Erőforrások bezárása
	 */
	void close ();
}
