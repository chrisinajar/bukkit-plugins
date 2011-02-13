package org.chrisinajar.CommandAlias;

import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Scanner;
import java.util.HashMap;
import java.util.ArrayList;
import org.bukkit.entity.Player;
import org.bukkit.Server;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.event.player.*;
import org.bukkit.event.block.*;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.nijiko.permissions.PermissionHandler;
import org.bukkit.plugin.Plugin;
import org.chrisinajar.JarPlugin;

// FUCK UR JAVA PACKAGES, I DON'T WANT THAT SHIT

/**
 * CommandAlias for Bukkit
 *
 * @author chrisinajar
 */
public class CommandAlias extends JarPlugin {
    private final PListener playerListener = new PListener(this);
    private final BListener blockListener = new BListener(this);
	public String configLocation = "aliases.conf";
	public ArrayList<Alias> aliases = new ArrayList<Alias>();
	public static int loopChecker = 0;
	public class Alias {
		public ArrayList<String> commands = new ArrayList<String>();
		public ArrayList<String> alias = new ArrayList<String>();
		public int minArgs = 0;
		public boolean allowAnyone = true;
		public ArrayList<String> allowedGroups = new ArrayList<String>();
		public ArrayList<String> allowedUsers = new ArrayList<String>();
		public ArrayList<String> allowedCommands = new ArrayList<String>();
		public ArrayList<String> options = new ArrayList<String>();
	}

    public CommandAlias(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader)
	{
        super(pluginLoader, instance, desc, folder, plugin, cLoader);
    }
	
    public void onEnable()
	{
		init("CommandAlias", blockListener, playerListener);
		this.reloadConfig();
		registerPlayerEvent(Event.Type.PLAYER_COMMAND, Event.Priority.Low);
    }
	
    public void onDisable()
	{
		debug("I'm going away now");
    }
	
	public class BListener extends BlockListener {
		private final CommandAlias plugin;

		public BListener(final CommandAlias plugin) {
			this.plugin = plugin;
		}
	}
	
	public class PListener extends PlayerListener
	{
		private final CommandAlias plugin;

		public PListener(CommandAlias instance)
		{
			plugin = instance;
		}

		public void onPlayerCommand(PlayerChatEvent event)
		{
			plugin.debug("I got a thing");
			Player player = event.getPlayer();
			if (!canUseCommand(player, "/commandalias"))
				return;
			if(plugin.loopChecker == 20)
			{
				//plugin.debug("Command alias recursed 20 times, aborting this command: " + split[0]);
				return;
			}
			plugin.debug("Format:" + event.getFormat());
			plugin.debug("Message:" + event.getMessage());
			
			String[] split = event.getMessage().split(" ");

			Alias matched = null;
			String allArgs = new String();
			for (int i = 1; i < split.length; ++i)
			{
				allArgs += split[i];
				if (i != (split.length - 1))
					allArgs += " ";
			}
			for (Alias a : plugin.aliases)
			{
				for (String s : a.commands)
				{
					if (!s.equalsIgnoreCase(split[0]))
					{
						// plugin.debug(s + " isn't " + split[0]);
						continue;
					}
					if (matched != null && a.minArgs < matched.minArgs)
					{
						// plugin.debug("We already matched an alias with " + matched.minArgs);
						continue;
					}
					if (a.minArgs >= split.length)
					{
						// plugin.debug(a.minArgs + " is greater than or equal to " + split.length);
						continue;
					}

					boolean hasPermission = a.allowAnyone;
					while (!hasPermission) // so that I can break out of it, i could use a function but I don't want to :3
					{
						if (a.allowedCommands.size() > 0)
						{
							boolean matchedCommands = true;
							for (String cmd : a.allowedCommands)
							{
								if (!canUseCommand(player, (cmd)))
								{
									matchedCommands = false;
									break;
								}
							}
							if (matchedCommands)
							{
								hasPermission = true;
								break;
							}
						}
						for (String name : a.allowedUsers)
						{
							if (player.getName().equalsIgnoreCase(name))
							{
								hasPermission = true;
								break;
							}
						}
						if (hasPermission)
							break;
						for (String group : a.allowedGroups)
						{
							if(isInGroup(player, (group)))
							{
								hasPermission = true;
								break;
							}
						}
						if (hasPermission)
							break;
						break;
					}
					if (!hasPermission) // This user cannot run this command
						continue;
					matched = a;
				}
			}
			if (matched == null)
			{
				return;
			}
			plugin.loopChecker++;
			String runAs = null;
			for (String option : matched.options)
			{
				if(option.startsWith("runas="))
				{
					runAs = option.substring(6);
				}
			}
			//@GROUPS
			// String[] oldGroups = player.getGroups();
			if (runAs != null)
			{
				// plugin.debug("Running alias as " + runAs);
				// player.addGroup(runAs);
			}
			for (String c : matched.alias)
			{
				String[] cmdToRun = c.split(" +", 0);
				for (int i = 0; i < cmdToRun.length; ++i)
				{
					if(cmdToRun[i].equals("[@]"))
					{
						cmdToRun[i] = allArgs;
						continue;
					}
					if (cmdToRun[i].matches("^\\[[0-9]+\\]$"))
					{
						int argNum = Integer.parseInt(cmdToRun[i].substring(1, cmdToRun[i].length() - 1));
						
						while (argNum >= split.length)
							argNum--;
						cmdToRun[i] = split[argNum];
					}
				}
				String strCmd = new String();
				for (int i = 0; i < cmdToRun.length; ++i)
				{
					strCmd += cmdToRun[i];
					if (i != (cmdToRun.length - 1))
						strCmd += " ";
				}
				player.performCommand(strCmd);
			}
			//@GROUPS
			// if (runAs != null)
			//	player.setGroups(oldGroups);
			plugin.loopChecker--;
			return;
		// */
		}
	}

	public void reloadConfig() {
		if (new File(configLocation).exists()) {
			try {
				Scanner scanner = new Scanner(new File(configLocation));
				Alias alias = new Alias();
				boolean inAlias = false;
				boolean inOptions = false;
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					if (line.startsWith("#") || line.length() == 0)
						continue;
					String[] parts = line.split(" ");
					if (inAlias) {
						int minArgs = 0;
						String aliasStr = new String();
						for (String p : parts) {
							if (p.length() == 0)
								continue;
							if (p.equalsIgnoreCase("}")) {
								if(aliasStr.length() > 0) {
									alias.alias.add(aliasStr);
									alias.minArgs = (alias.minArgs < alias.minArgs ? minArgs : alias.minArgs);
								}
								debug("I found an alias!");
								aliases.add(alias);
								alias = new Alias();
								inAlias = false;
								break;
							}
							if (p.equalsIgnoreCase("{"))
								throw new Exception("Misplaced '{' character");
							if (p.matches("^\\[[0-9]+\\]$")) {
								int argNum = Integer.parseInt(p.substring(1, p.length() - 1));
								minArgs = (argNum > minArgs ? argNum : minArgs);
							}
							if (aliasStr.length() > 0)
								aliasStr = aliasStr + " " + p;
							else
								aliasStr = p;
						}
						if(aliasStr.length() > 0) {
							alias.alias.add(aliasStr);
							alias.minArgs = (alias.minArgs < minArgs ? minArgs : alias.minArgs);
						}
					}
					else {
						for (String p : parts) {
							if (p.length() == 0)
								continue;
							if (p.equalsIgnoreCase("{")) {
								inAlias = true;
								break;
							}
							if (p.equalsIgnoreCase("}"))
								throw new Exception("Misplaced '}' character");
							if (p.charAt(0) == '[' || p.charAt(p.length() - 1) == ']')
							{
								if (p.charAt(0) != '[' || p.charAt(p.length() - 1) != ']')
									throw new Exception("Malformed options list:" + p);
								String[] options = p.substring(1, p.length() - 1).split(",");
								for (String o : options)
								{
									if (o.charAt(1) != ':')
										throw new Exception("Malformed option: " + o + "... '" + o.charAt(1) + "' is not ':'");
									if (o.charAt(0) == 'g') // group allowed list
									{
										alias.allowedGroups.add(o.substring(2));
										alias.allowAnyone = false;
									}
									else if (o.charAt(0) == 'u') // users allowed list
									{
										alias.allowedUsers.add(o.substring(2));
										alias.allowAnyone = false;
									}
									else if (o.charAt(0) == 'c') // command allowed list
									{
										alias.allowedCommands.add(o.substring(2));
										alias.allowAnyone = false;
									}
									else if (o.charAt(0) == 'o') // generic option
									{
										alias.options.add(o.substring(2));
									}
									else
									{
										debug("Unknown option type found while reading aliases: " + o);
									}
								}
							}
							else
							{
								alias.commands.add(p);
							}
						}
					}
				}
			} catch (Exception e) {
				debug("Exception while reading " + configLocation + ", please paste this to chrisinajar", e);
			}
		}
		else
		{
			debug("no aliases found");
		}
	}
}

