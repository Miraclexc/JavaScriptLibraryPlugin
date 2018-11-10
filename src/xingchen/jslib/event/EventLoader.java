package xingchen.jslib.event;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

/**
 * 事件监听器(暂时无用)
 */
public class EventLoader implements Listener {
	private Plugin plugin;
	
	public EventLoader(Plugin plugin) {
		this.plugin = plugin;
	}
	
	public static void init(Plugin plugin) {
		plugin.getServer().getPluginManager().registerEvents(new EventLoader(plugin), plugin);
	}

	public Plugin getPlugin() {
		return this.plugin;
	}
}
