package hu.nye.pandragon.wumpus.service.command.impl.editor;

import hu.nye.pandragon.wumpus.lovel.Entities;
import hu.nye.pandragon.wumpus.lovel.Level;
import hu.nye.pandragon.wumpus.lovel.entities.Wumpus;
import hu.nye.pandragon.wumpus.service.command.CanProcessResult;
import hu.nye.pandragon.wumpus.service.command.Command;
import hu.nye.pandragon.wumpus.service.command.LevelEditorCommands;
import hu.nye.pandragon.wumpus.service.util.CommandUtils;
import hu.nye.pandragon.wumpus.ui.LevelPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Ez a parancs új pályaelem létrehozására szolgál
 */
public class EditorPlaceEntityCommand implements Command {

	private static final Logger LOGGER = LoggerFactory.getLogger(EditorPlaceEntityCommand.class);
	private final Level level;

	public EditorPlaceEntityCommand(Level level) {
		this.level = level;
	}

	@Override
	public Optional<CanProcessResult> canProcess(String input) {
		return LevelEditorCommands.Place.matches(input);
	}

	@Override
	public void process(String input) {
		var args = Command.getCommandArgs(input);
		LOGGER.info("Új pályaelem létrehozása: " + args);
		var entity = Entities.parse(args[0]).createNewInstance();
		if (entity instanceof Wumpus && level.getEntityCount(entity) >= level.getMaxWumpus()) {
			var error = String.format("Wumpusból max %d lehet", level.getMaxWumpus());
			LOGGER.error(error);
			throw new RuntimeException(error);
		}
		var position = CommandUtils.getCoordinates(args[1], args[2], level.getSize());
		level.placeEntity(position.x, position.y, entity);
		LOGGER.info("Új pályaelem: {} -> {}, {}", entity.getName(), position.x, position.y);
	}
}