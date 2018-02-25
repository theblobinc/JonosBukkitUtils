
package jdz.bukkitUtils.events.custom;

import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;

import jdz.bukkitUtils.events.Cancellable;
import jdz.bukkitUtils.events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@SuppressWarnings("deprecation")
@AllArgsConstructor
public class PotionDrinkEvent extends Event implements Cancellable {
	@Getter private final Collection<PotionEffect> effects;
	@Getter private final Player player;
	private final PlayerItemConsumeEvent parent;

	@Override
	public void setCancelled(boolean cancel) {
		parent.setCancelled(cancel);
	}

	public static HandlerList getHandlerList() {
		return getHandlers(PotionDrinkEvent.class);
	}

	static class PotionDrinkEventListener implements Listener {
		@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
		public void onDrink(PlayerItemConsumeEvent event) {
			if (event.getItem().getType() == Material.POTION) {
				Potion potion = Potion.fromItemStack(event.getItem());
				new PotionDrinkEvent(potion.getEffects(), event.getPlayer(), event).call();
			}
		}
	}
}
