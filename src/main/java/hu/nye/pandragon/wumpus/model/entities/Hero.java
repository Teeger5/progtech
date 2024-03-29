package hu.nye.pandragon.wumpus.model.entities;

import hu.nye.pandragon.wumpus.model.Directions;
import hu.nye.pandragon.wumpus.model.Items;
import hu.nye.pandragon.wumpus.service.traits.CanShoot;
import hu.nye.pandragon.wumpus.service.traits.HasInventory;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

/**
 * Ez az osztály a játékost, mint a főhőst írja le
 */
@EqualsAndHashCode
public class Hero extends LivingEntity implements CanShoot, HasInventory {

	/**
	 * A játékos nyilainak száma
	 */
	private int arrows;
	private final Map<Items, Integer> inventory;

	public Hero() {
		super("Hős", 'H', true);
		arrows = 0;
		direction = Directions.North;
		inventory = new HashMap<>();
	}

	public void decreaseArrows () {
		arrows = Math.max(0, arrows - 1);
	}

	@Override
	public Map<Items, Integer> getInventory() {
		return inventory;
	}

	@Override
	public char getDisplaySymbol() {
		return direction.getDisplaySymbol();
	}

	@Override
	public Entity clone() {
		var clone = new Hero();
		clone.setAmmoAmount(arrows);
		clone.setPosition(position.x, position.y);
		clone.setDirection(direction);
		clone.setAlive(alive);
		for (Map.Entry<Items, Integer> item : inventory.entrySet()) {
			clone.addItem(item.getKey(), item.getValue());
		}
		return clone;
	}

	@Override
	public void onShoot() {
		decreaseArrows();
	}

	@Override
	public int getAmmoAmount() {
		return arrows;
	}

	@Override
	public void setAmmoAmount(int count) {
		arrows = count;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Hero {\n");
		sb.append("\tarrows = ").append(arrows + ",\n");
		sb.append("\tposition = ").append(position + ",\n");
		sb.append("\tinventory = ").append(inventory + ",\n");
		sb.append("\talive = ").append(alive + ",\n");
		sb.append("\tposition = ").append(position + ",\n");
		sb.append("\tdirection = ").append(direction + ",\n");
		sb.append("\tdisplaySymbol = ").append(displaySymbol + ",\n");
		sb.append("}");
		return sb.toString();
	}
}
