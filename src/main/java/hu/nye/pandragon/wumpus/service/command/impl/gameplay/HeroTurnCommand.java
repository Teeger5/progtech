package hu.nye.pandragon.wumpus.service.command.impl.gameplay;

import hu.nye.pandragon.wumpus.service.game.EntityController;
import hu.nye.pandragon.wumpus.service.game.Level;
import hu.nye.pandragon.wumpus.model.TurnDirections;
import hu.nye.pandragon.wumpus.service.command.CommandMatcherResult;
import hu.nye.pandragon.wumpus.service.command.Command;
import hu.nye.pandragon.wumpus.model.GameplayCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Command used to write a number to a given field of the map.
 */
public class HeroTurnCommand implements Command {

	private static final Logger LOGGER = LoggerFactory.getLogger(HeroTurnCommand.class);
	private final Level level;

	public HeroTurnCommand(Level level) {
		this.level = level;
	}

	@Override
	public CommandMatcherResult match(String input) {
		return GameplayCommands.Turn.matches(input);
	}

	@Override
	public void process(String input) {
		var args = Command.getCommandArgs(input);
		LOGGER.info("A hős forgatása " + args[0]);
		var direction = TurnDirections.parse(args[0]);
		var hero = level.getHero();
		var controller = new EntityController(level, hero);
		controller.turn(direction);
		LOGGER.info("Hős elforgatva {}, nézési irány: {}", direction.name(), hero.getDirection());
	}
}
