package com.untamedears.jukealert.commands;

import static com.untamedears.jukealert.util.JAUtility.findLookingAtOrClosestSnitch;


import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Syntax;
import com.untamedears.jukealert.JukeAlert;
import com.untamedears.jukealert.model.Snitch;
import com.untamedears.jukealert.util.JAUtility;
import com.untamedears.jukealert.util.JukeAlertPermissionHandler;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import vg.civcraft.mc.namelayer.permission.PermissionType;

public class NameCommand extends BaseCommand {
	@CommandAlias("janame")
	@Syntax("<name>")
	@Description("Name a snitch")
	public void execute(Player player, String snitchName) {
		Snitch snitch = findLookingAtOrClosestSnitch(player, getPermission());
		if (snitch == null) {
			player.sendMessage(
					ChatColor.RED + "You do not own any snitches nearby or lack permission to view their logs!");
			return;
		}

		renameSnitch(player, snitchName, snitch);
	}

	@CommandAlias("janame")
	@Syntax("<world> <x> <y> <z> <name>")
	@Description("Name a snitch")
	public void execute(Player player, String worldName, int x, int y, int z, String snitchName) {
		World world = Bukkit.getWorld(worldName);
		if (world == null) {
			player.sendMessage(ChatColor.RED + "Invalid world.");
			return;
		}

		Location location = new Location(world, x, y, z);
		renameSnitch(player, snitchName, location);
	}

	@CommandAlias("janame")
	@Syntax("<x> <y> <z> <name>")
	@Description("Name a snitch")
	public void execute(Player player, int x, int y, int z, String snitchName) {
		World world = player.getLocation().getWorld();
		Location location = new Location(world, x, y, z);
		renameSnitch(player, snitchName, location);
	}

	private static void renameSnitch(Player player, String name, Location location) {
		Snitch snitch = JukeAlert.getInstance().getSnitchManager().getSnitchAt(location);
		if (snitch == null || !snitch.hasPermission(player, getPermission())) {
			player.sendMessage(
					ChatColor.RED + "You do not own a snitch at those coordinates or lack permission to rename it!");
			return;
		}

		renameSnitch(player, name, snitch);
	}

	private static void renameSnitch(Player player, String name, Snitch snitch) {
		String newName = name.length() > 40
			? name.substring(0, 40)
			: name;

		String prevName = snitch.getName();
		JukeAlert.getInstance().getSnitchManager().renameSnitch(snitch, newName);
		TextComponent lineText = new TextComponent(ChatColor.AQUA + " Changed snitch name to ");
		lineText.addExtra(JAUtility.genTextComponent(snitch));
		lineText.addExtra(ChatColor.AQUA + " from " + ChatColor.GOLD + prevName);
		player.spigot().sendMessage(lineText);
	}

	private static PermissionType getPermission() {
		return JukeAlertPermissionHandler.getRenameSnitch();
	}
}
