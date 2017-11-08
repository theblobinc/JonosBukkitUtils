
package jdz.jbu.guiMenu.itemStacks;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import jdz.jbu.guiMenu.guis.GuiMenu;

public abstract class ClickableStack extends ItemStack{	
	
	public ClickableStack() {super();};
	
	public ClickableStack(ItemStack i) {
		super(i);
	}
	
	public abstract void onClick(GuiMenu menu, InventoryClickEvent event);
}
