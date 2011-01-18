import java.io.File;
import java.util.HashMap;
import org.bukkit.entity.Player;
import org.bukkit.Server;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

// FUCK UR JAVA PACKAGES, I DON'T WANT THAT SHIT

/**
 * CommandAlias for Bukkit
 *
 * @author chrisinajar
 */
public class CommandAlias extends JavaPlugin {
    private final PListener playerListener = new PListener(this);
    private final BListener blockListener = new BListener(this);

    public CommandAlias(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader)
	{
        super(pluginLoader, instance, desc, folder, plugin, cLoader);
    }
	
    public void onEnable()
	{
        PluginManager pm = getServer().getPluginManager();
    }
	
    public void onDisable()
	{
    }
	
	public class PListener extends PlayerListener
	{
		private final CommandAlias plugin;

		public PListener(CommandAlias instance)
		{
			plugin = instance;
		}
	}
	
	public class BListener extends BlockListener {
		private final CommandAlias plugin;

		public BListener(final CommandAlias plugin) {
			this.plugin = plugin;
		}
	}
}

