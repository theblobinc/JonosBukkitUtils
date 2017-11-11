
package jdz.bukkitUtils.guiMenu.itemStacks;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import jdz.bukkitUtils.guiMenu.guis.GuiMenu;

public abstract class ClickableStack extends ItemStack{	
	protected boolean closeOnClick = false;
	
	public ClickableStack() {super();};
	
	public ClickableStack(ItemStack i) {
		super(i);
	}
	
	public void closeOnClick() {
		closeOnClick = true;
	}
	
	public abstract void onClick(GuiMenu menu, InventoryClickEvent event);
}
