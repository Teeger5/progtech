package hu.nye.pandragon.wumpus.service.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

/**
 * Ez az interface egy felhasznűló számára használható
 * parancs metódusait írja le
 */
public interface Command {

	/**
	 * Ez a metódus eldönti egy bemenetről, hogy erre a parancsra vonatkozik-e, vagy sem
	 * Ha üres Optional-t ad vissza, akkor ez nem az a parancs,
	 * ha nem üres, akkor a benne lévő CanprocessResult dönti el, hogy végre lehet-e hajtani.
	 * Ha abban van üzenet, akkor valamilyen hiba van a parancsban (pl. hiányzó argumentum),
	 * ha nincs, akkor formailag nincs probléma a paranccsal
	 * @param input a parancs
	 * @return ez-e az a parancs, illetve helyesen használják-e
	 */
	Optional<CanProcessResult> canProcess (String input);

	/**
	 * A parancs végrehajtása
	 * @param input a parancs
	 */
	void process (String input);

	/**
	 * Ez a metódus a parancs argumentumait adja vissza
	 * Például ha a parancs 'fordul jobbra',
	 * akkor a visszaadott tömb ['jobbra'] lesz
	 * @param input felhasználói bemenet, azaz a parancs
	 * @return a parancs argumentumai
	 */
	static String[] getCommandArgs (String input) {
		var list = new ArrayList<>(Arrays.asList(input.split("\\s+")));
		list.remove(0);
		return list.toArray(new String[0]);
	}
}