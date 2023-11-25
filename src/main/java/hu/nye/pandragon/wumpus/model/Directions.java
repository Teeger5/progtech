package hu.nye.pandragon.wumpus.model;

public enum Directions {
	North('N', '▲'),
	East('E', '▶'),
	South('S', '▼'),
	West('W', '◀'),
	Unknown('-', '-');

	public char getCompatibilitySymbol() {
		return compatibilitySymbol;
	}

	public char getDisplaySymbol() {
		return displaySymbol;
	}

	public Directions getClockwiseNext () {
		return switch (this) {
			case North -> East;
			case East -> South;
			case South -> West;
			case West -> North;
			case Unknown -> Unknown;
		};
	}
	
	public Directions getClockwisePrevious () {
		return switch (this) {
			case North -> West;
			case East -> North;
			case South -> East;
			case West -> South;
			case Unknown -> Unknown;
		};
	}

	private final char compatibilitySymbol;
	private final char displaySymbol;
	Directions(char compatibilitySymbol, char displaySymbol) {
		this.compatibilitySymbol = compatibilitySymbol;
		this.displaySymbol = displaySymbol;
	}

	/**
	 * Megkeresi a megadott jelhez tartozó irányt
	 * (N, E, S, W)
	 * @param c
	 * @return
	 */
	public static Directions parseSymbol (char c) {
		return switch (c) {
			case 'N' -> North;
			case 'E' -> East;
			case 'S' -> South;
			case 'W' -> West;
			default -> Unknown;
		};
	}
}
