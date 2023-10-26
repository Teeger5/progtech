package hu.nye.pandragon.wumpus.service.command;

import hu.nye.pandragon.wumpus.lovel.Level;
import hu.nye.pandragon.wumpus.service.command.impl.gameplay.HeroMoveCommand;
import hu.nye.pandragon.wumpus.service.command.impl.gameplay.HeroTurnCommand;
import hu.nye.pandragon.wumpus.ui.LevelPrinter;

import java.util.EnumMap;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Ez az enum a hős számára a játékban elérhető parancsokat tartalmazza
 */
public enum GameplayCommands {
	Turn ("fordul jobbra|balra", "^fordul\\s+(jobbra|balra)$"),
	PickUpGold ("aranyat felszed", "^aranyat felszed$"),
	Shoot ("lő", "^lő$"),
	MoveForward ("lép", "^lép$"),
	GiveUp ("felad", "^felad$");

	private final String usage;
	private final String regex;
	private final String base;

	/**
	 * Ez a metódus eldönti egy bemenetről, hogy erre a parancsra vonatkozik-e, vagy sem
	 * Ha üres Optional-t ad vissza, akkor ez nem az a parancs,
	 * ha nem üres, akkor a benne lévő CanprocessResult dönti el, hogy végre lehet-e hajtani.
	 * Ha abban van üzenet, akkor valamilyen hiba van a parancsban (pl. hiányzó argumentum),
	 * ha nincs, akkor formailag nincs probléma a paranccsal
	 * @param input a parancs
	 * @return ez-e az a parancs, illetve helyesen használják-e
	 */
	public Optional<CanProcessResult> matches (String input) {
		if (!input.startsWith(base)) {
			return Optional.empty();
		}
		if (!Pattern.matches(regex, input)) {
			return Optional.of(new CanProcessResult("A parancs használata: " + usage));
		}
		return Optional.of(new CanProcessResult());
	}

	public Command getCommand (Level level, LevelPrinter levelPrinter) {
		switch (this) {
			case Turn -> new HeroTurnCommand(level, levelPrinter);
			case MoveForward -> new HeroMoveCommand(level, levelPrinter);
		}
		return null;
	}

	GameplayCommands(String usage, String regex) {
		this.usage = usage;
		this.regex = regex;
		if (!usage.contains(" ")) {
			base = usage;
		}
		else {
			base = usage.substring(0, usage.indexOf(' '));
		}
	}

	public static EnumMap<GameplayCommands, Command> toCommandMap (Level level, LevelPrinter levelPrinter) {
		var commands = new EnumMap<GameplayCommands, Command>(GameplayCommands.class);
		for (GameplayCommands command : GameplayCommands.values()) {
			commands.put(command, command.getCommand(level, levelPrinter));
		}
		return commands;
	}
}
