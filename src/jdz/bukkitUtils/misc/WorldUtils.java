
package jdz.bukkitUtils.misc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import jdz.bukkitUtils.JonosBukkitUtils;
import jdz.bukkitUtils.fileIO.FileLogger;

/**
 * Methods to do with worlds and locations. Also methods for relating blocks /
 * entities in the world
 * based on their locations.
 *
 * @author Jaiden Baker
 */
public final class WorldUtils {

	/**
	 * Converts a location to the format "{worldName},{x},{y},{z},{pitch},{yaw}"
	 * 
	 * @param l
	 * @return
	 */
	public static String locationToString(Location l) {
		return l.getWorld().getName() + "," + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ() + ","
				+ l.getPitch() + "," + l.getYaw();
	}

	/**
	 * Converts a location to the format "{worldName},{x},{y},{z}" in a readable
	 * form
	 * 
	 * @param l
	 * @return
	 */
	public static String locationToLegibleString(Location l) {
		return l.getWorld().getName() + " (x:" + l.getBlockX() + ", y:" + l.getBlockY() + ", z:" + l.getBlockZ() + ")";
	}

	/**
	 * Creates a location from a string generated by WorldUtils.locationToString
	 * 
	 * @param s
	 * @return
	 */
	public static Location locationFromString(String s) {
		String[] args = s.split(",");
		try {
			World world = Bukkit.getWorld(args[0]);
			return new Location(world, Integer.parseInt(args[1]) + 0.5, Integer.parseInt(args[2]),
					Integer.parseInt(args[3]) + 0.5, Float.parseFloat(args[4]), Float.parseFloat(args[5]));
		}
		catch (Exception e) {
			new FileLogger(JonosBukkitUtils.instance).createErrorLog(e, "Error parsing location with args: " + args);
			return null;
		}
	}

	/**
	 * Fetches the nearest solid block under the given location
	 * 
	 * @param l
	 * @return
	 */
	public static Block getNearestBlockUnder(Location l) {
		return l.getWorld().getBlockAt(getNearestLocationUnder(l));
	}

	@Deprecated
	public static Location getNearestLocationUnder(Location l) {
		Location location = new Location(l.getWorld(), l.getBlockX() + 0.5, l.getBlockY(), l.getBlockZ() + 0.5);
		while (!location.getBlock().getType().isSolid()) {
			location = location.add(0, -1, 0);
			if (location.getY() < 0) {
				return null;
			}
		}
		return location;
	}

	/**
	 * Fetches the nearest block, above or below the current block from bedrock to
	 * sky limit,
	 * whose material and data match the desired type
	 * 
	 * @param block
	 * @param blockType
	 * @param blockData
	 * @return the block, or null if not found
	 */
	public static Block getBlockAboveOrBelow(Block block, Material blockType, byte blockData) {
		return getBlockAboveOrBelow(block, blockType, blockData, 1);
	}

	@SuppressWarnings("deprecation")
	private static Block getBlockAboveOrBelow(Block block, Material blockType, byte blockData, int distance) {
		boolean maxHeightReached = block.getLocation().getBlockY() + distance > block.getWorld().getMaxHeight() - 1;
		boolean minHeightReached = block.getLocation().getBlockY() - distance < 1;

		if (maxHeightReached && minHeightReached)
			return null;

		if (!maxHeightReached) {
			Block blockAbove = block.getWorld().getBlockAt(block.getLocation().add(0, distance, 0));
			if (blockAbove.getType() == blockType && blockAbove.getData() == blockData)
				return blockAbove;
		}

		if (!minHeightReached) {
			Block blockBelow = block.getWorld().getBlockAt(block.getLocation().subtract(0, distance, 0));
			if (blockBelow.getType() == blockType && blockBelow.getData() == blockData)
				return blockBelow;
		}

		return getBlockAboveOrBelow(block, blockType, blockData, distance + 1);
	}

	/**
	 * Get a set of players who are near a given location spherically
	 * 
	 * @param location
	 * @param range
	 * @return
	 */
	public static Set<Player> getNearbyPlayers(Location location, double range) {
		Set<Player> nearbyPlayers = new HashSet<Player>();
		for (Player player : Bukkit.getServer().getOnlinePlayers())
			if (player.getWorld().equals(location.getWorld()) && player.getLocation().distance(location) < range)
				nearbyPlayers.add(player);
		return nearbyPlayers;
	}

	/**
	 * Get a set of players who are within a given cuboid region
	 * 
	 * @param location
	 * @param range
	 * @return
	 */
	public static Set<Player> getPlayersInCuboid(Location origin, double width, double height, double depth) {
		if (width < 0) {
			origin.setX(origin.getX() - width);
			width *= -1;
		}
		if (height < 0) {
			origin.setY(origin.getY() - height);
			height *= -1;
		}
		if (depth < 0) {
			origin.setZ(origin.getZ() - depth);
			depth *= -1;
		}

		Set<Player> nearbyPlayers = new HashSet<Player>();
		World world = origin.getWorld();
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			if (!player.getWorld().equals(world))
				continue;
			Location ploc = player.getLocation();
			if (ploc.getX() > origin.getX() && ploc.getX() < origin.getBlockX() + width)
				if (ploc.getY() > origin.getY() && ploc.getY() < origin.getY() + height)
					if (ploc.getZ() > origin.getZ() && ploc.getZ() < origin.getZ() + depth)
						nearbyPlayers.add(player);
		}
		return nearbyPlayers;
	}

	public static void flingPlayer(Player player, Location destination, double heightGain) {
		flingPlayer(player, destination.toVector(), heightGain);
	}

	public static Vector flingPlayer(Player player, Vector destination, double heightGain) {
		Vector from = player.getLocation().toVector();

		// Gravity of a player
		double gravity = 0.306;

		// Block locations
		int endGain = destination.getBlockY() - from.getBlockY();
		double horizDist = Math.sqrt(distanceSquared(from, destination));

		// Height gain
		double gain = heightGain;

		double maxGain = gain > (endGain + gain) ? gain : (endGain + gain);

		// Solve quadratic equation for velocity
		double a = -horizDist * horizDist / (4 * maxGain);
		double b = horizDist;
		double c = -endGain;

		double slope = (-b - Math.sqrt(b * b - 4 * a * c)) / (2 * a);

		// Vertical velocity
		double vy = Math.sqrt(maxGain * gravity);

		// Horizontal velocity
		double vh = vy / slope;

		// Calculate horizontal direction
		int dx = destination.getBlockX() - from.getBlockX();
		int dz = destination.getBlockZ() - from.getBlockZ();
		double mag = Math.sqrt(dx * dx + dz * dz);
		double dirx = dx / mag;
		double dirz = dz / mag;

		// Horizontal velocity components
		double vx = vh * dirx;
		double vz = vh * dirz;

		return new Vector(vx, vy, vz);
	}

	private static double distanceSquared(Vector from, Vector to) {
		double dx = to.getBlockX() - from.getBlockX();
		double dz = to.getBlockZ() - from.getBlockZ();

		return dx * dx + dz * dz;
	}

	public static List<Location> getCircle(Location center, double radius, int amount) {
		World world = center.getWorld();
		double increment = (2 * Math.PI) / amount;
		List<Location> locations = new ArrayList<Location>();
		for (int i = 0; i < amount; i++) {
			double angle = i * increment;
			double x = center.getX() + (radius * Math.cos(angle));
			double z = center.getZ() + (radius * Math.sin(angle));
			locations.add(new Location(world, x, center.getY(), z));
		}
		return locations;
	}

	public static Vector getVector(double pitch, double yaw) {
		double x = Math.sin(pitch) * Math.cos(yaw);
		double y = Math.sin(pitch) * Math.sin(yaw);
		double z = Math.cos(pitch);
		return new Vector(x, y, z);
	}
}
