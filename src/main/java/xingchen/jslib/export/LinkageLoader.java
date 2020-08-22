package xingchen.jslib.export;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.plugin.Plugin;

import xingchen.jslib.event.register.JavaScriptPreRegisterEvent;
import xingchen.jslib.js.IScriptHolder;
import xingchen.jslib.event.register.JavaScriptFilesEvalledEvent;

public class LinkageLoader {
	public static LinkageLoader instance;
	
	private Plugin plugin;
	
	private Map<String, PluginScripts> linkages;
	
	public LinkageLoader(Plugin plugin) {
		this.plugin = plugin;
		this.linkages = new HashMap<>();
	}
	
	/*public static void create(Plugin plugin) {
		instance = new LinkageLoader(plugin);
		for(Plugin other : plugin.getServer().getPluginManager().getPlugins()) {
			Method method = null;
			try {
				method = other.getClass().getMethod("getJsHolder");
			} catch (NoSuchMethodException | SecurityException e) {
				continue;
			}
			try {
				Object object = method.invoke(other);
				if(object instanceof IJsHolder) {
					instance.addLinkage(other, (IJsHolder) object);
				}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	public void evalAll() {
		this.linkages.entrySet().stream().forEach(i -> i.getValue().evalAll());
	}*/

	/**
	 * 添加路径
	 * {#IScriptHolder}实现类：{#ScriptHolderBase}
	 * 
	 * @reture 注册是否成功
	 */
	public boolean registerLinkage(String name, Plugin plugin, IScriptHolder jsHolder, ClassLoader classLoader) {
		if(this.getLinkages().containsKey(name)) {
			return false;
		}
		
		JavaScriptPreRegisterEvent preEvent = new JavaScriptPreRegisterEvent(this, name);
		this.plugin.getServer().getPluginManager().callEvent(preEvent);
		if(preEvent.isCancelled()) {
			return false;
		}

		PluginScripts scripts = new PluginScripts(plugin, jsHolder);
		scripts.setClassLoader(classLoader);
		this.linkages.put(name, scripts);
		scripts.loadScripts();
		scripts.evalAll();
		
		this.plugin.getServer().getPluginManager().callEvent(new JavaScriptFilesEvalledEvent(this, name));
		
		return true;
	}

	public boolean registerLinkage(String name, Plugin plugin, IScriptHolder jsHolder) {
		return registerLinkage(name, plugin, jsHolder, null);
	}

	public Plugin getPlugin() {
		return this.plugin;
	}
	
	public Map<String, PluginScripts> getLinkages() {
		return this.linkages;
	}
}
