package org.chrisinajar;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.event.player.*;
import org.bukkit.event.block.*;
import org.bukkit.event.Event;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import java.io.File;

// Permissions
import com.nijikokun.bukkit.Permissions.Permissions;
import com.nijiko.permissions.PermissionHandler;

// Convenient functions because I hate repeating myself
public abstract class JarPlugin extends JavaPlugin
{
	public static PermissionHandler Permissions = null;
	public String pluginName = null;
	public BlockListener baseBlockListener = null;
	public PlayerListener basePlayerListener = null;
	
    public JarPlugin(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader)
	{
        super(pluginLoader, instance, desc, folder, plugin, cLoader);
    }
	
	public void init(String name, BlockListener bl, PlayerListener pl)
	{
		pluginName = name;
		baseBlockListener = bl;
		basePlayerListener = pl;
		debug(" * " + pluginName + " is starting up...");
	}
	
	public void registerPlayerEvent(Event.Type type, Event.Priority pri)
	{
		debug(" -> " + type);
        getServer().getPluginManager().registerEvent(type, basePlayerListener, pri, this);
	}
	
	public void debug(String str)
	{
		System.out.println(" * " + pluginName + " " + str);
	}
	public void debug(String str, Throwable e)
	{
		System.out.println(" * " + pluginName + " " + str);
	}
	
	public void disablePlugin()
	{
		debug("disabling...");
		this.getServer().getPluginManager().disablePlugin(this);
	}
	
	public boolean setupPermissions(JavaPlugin pl)
	{
		Plugin test = this.getServer().getPluginManager().getPlugin("Permissions");

		if(this.Permissions == null) {
			if(test != null) {
				this.Permissions = ((Permissions)test).getHandler();
				debug("successfuly enabled Permissions support!");
				return true;
			} else {
				debug("failed to find Permission support!");
				return false;
			}
		}
		// initializing again...
		return true;
    }
	
	public boolean canUseCommand(Player player, String cmd)
	{
		if (this.Permissions == null)
			return false;
		return this.Permissions.has(player, cmd);
	}
	
	public boolean isInGroup(Player player, String g)
	{
		return (g.equalsIgnoreCase(this.Permissions.getGroup(player.getName())));
		
	}

}











