package xingchen.jslib;

import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import xingchen.jslib.command.CommandLoader;
import xingchen.jslib.config.ConfigManager;
import xingchen.jslib.export.LinkageLoader;
import xingchen.jslib.js.JavaScriptLoader;

public class JavaScriptLibrary extends JavaPlugin {
	public static JavaScriptLibrary instance;
	
	/**插件名*/
	private String pluginName;
	/**配置管理器对象*/
	private ConfigManager config;
	
	@Override
	public void onEnable() {
		instance = this;
		this.pluginName = "javaScriptLibrary";
        
		this.config = new ConfigManager(this);
		//EventLoader.init(this);
		CommandLoader.init(this);
		
		Thread currentThread = Thread.currentThread();
        ClassLoader previousClassLoader = currentThread.getContextClassLoader();
        currentThread.setContextClassLoader(getClassLoader());
        
        LinkageLoader.instance = new LinkageLoader(this);
		JavaScriptLoader.init();
		
		if(this.config.isLinkage()) {
			this.getServer().getServicesManager().register(LinkageLoader.class, LinkageLoader.instance, this, ServicePriority.Normal);
		}
		
		currentThread.setContextClassLoader(previousClassLoader);
	}
	
	@Override
	public void onDisable() {
		this.getServer().getServicesManager().unregisterAll(this);
	}

	public String getPluginName() {
		return this.pluginName;
	}

	public ConfigManager getConfigManager() {
		return this.config;
	}
}
