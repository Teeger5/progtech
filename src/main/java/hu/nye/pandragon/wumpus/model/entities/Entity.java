package hu.nye.pandragon.wumpus.model.entities;


import lombok.Getter;

public abstract class Entity {
	/**
	 * A pályaelem neve
	 */
	@Getter
	protected final String name;
	/**
	 * Az a szimbólum, amely a pályán jelképezi ezt a pályaelemet
	 */
	@Getter
	protected char displaySymbol;
	/**
	 * Ez egy egységes szimbólum, ami mindenhol ezt a típusú pályaelemet azonosítja
	 * Hasznos például fájlból való beolvasááskor
	 */
	@Getter
	protected final char compatibilitySymbol;
	/**
	 * Át lehet-e menni rajta, vagy beleütközünk-e?
	 */
	@Getter
	protected final boolean blocking;
	/**
	 * Csak egy lehet belőle a pályán?
	 */
	@Getter
	protected final boolean unique;
	/**
	 * Kirajzoláskor az egész cellát ki kell-e törltenie,
	 * vagy egyszer jelenjen meg középen a szimbóluma?
	 */
	protected final boolean extendsInCell;

	public Entity(boolean blocking, String name, char compatibilitySymbol, boolean unique, boolean extendsInCell) {
		this.blocking = blocking;
		this.name = name;
		this.compatibilitySymbol = compatibilitySymbol;
		this.unique = unique;
		this.extendsInCell = extendsInCell;
		this.displaySymbol = compatibilitySymbol;
	}

	public boolean shouldExtendInCell() {
		return extendsInCell;
	}

	/**
	 * Létrehoz egy új objektumot ebből a pályaelemből,
	 * és beállítja neki a jelenlegi példány tulajdonságait, hogy ugyanolyan legyen
	 * @return az új példány, ami a jelenlegi hasonmása
	 */
	public abstract Entity clone ();

	public String toString () {
		return name;
	}
}
