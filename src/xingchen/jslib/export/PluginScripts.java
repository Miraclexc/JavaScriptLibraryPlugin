package xingchen.jslib.export;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.plugin.Plugin;

import xingchen.jslib.JavaScriptLibrary;
import xingchen.jslib.js.IScriptHolder;
import xingchen.jslib.js.JavaScriptInfo;
import xingchen.jslib.js.JavaScriptLoader;
import xingchen.jslib.js.ScriptHolderBase;
import xingchen.jslib.js.ScriptFiles;

/**
 * 插件全部的JavaScript代码
 */
public class PluginScripts {
	protected Plugin plugin;
	protected IScriptHolder jsHolder;
	
	/**
	 * 结构：
	 * [
	 *     "KeyValue": {
	 *         "key": {#JavaScriptInfo},
	 *         "value": {#String} / {#Map}
	 *           if value instanceof {#Map}:
	 *             "value": {
	 *                 "codeName": {#String} / {#Map}
	 *                   if value instanceof {#Map}:......
	 *             }
	 *     }
	 * ]
	 */
	protected List<KeyValue<JavaScriptInfo, Object>> scripts;
	protected boolean isEvalled;
	
	public PluginScripts(Plugin plugin, File... directory) {
		this(plugin, new ScriptHolderBase(Arrays.stream(directory).map(i -> ScriptFiles.fromDirectory(i)).filter(i -> i != null).toArray(ScriptFiles[]::new)));
	}
	
	public PluginScripts(Plugin plugin, IScriptHolder jsHolder) {
		this.plugin = plugin;
		this.jsHolder = jsHolder;
		this.scripts = new ArrayList<>();
		this.isEvalled = false;
	}
	
	/**
	 * 将JavaScript代码读取到内存({@link #scripts})
	 */
	public void loadScripts() {
		Arrays.stream(this.jsHolder.getScriptFiles()).forEach(script -> {
			File file = script.getJsFile();
			if(file.exists()) {
				Object object = JavaScriptLibrary.instance.getConfigManager().parseScriptFile(file, "js");
				JavaScriptInfo info = null;
				if(script.getInfo() != null) {
					try {
						info = JavaScriptInfo.parse(this.plugin, script.getInfo());
					} catch (UnsupportedEncodingException | FileNotFoundException e) {
						JavaScriptLibrary.instance.getConfigManager().getLogger().log(Level.WARNING, "Unable to parse the js info belonging to the plugin: " + this.plugin.getName(), e);
						return;
					}
				}
				
				scripts.add(new KeyValue<>(info, object));
			}
		});
	}
	
	/**
	 * 编译并执行当前对象内存中的JavaScript代码(必须先通过{@link #loadScripts()}读取代码)
	 */
	public void evalAll() {
		this.scripts.stream().forEach(i -> {
			Object value = i.getValue();
			if(value instanceof String) {
				JavaScriptLoader.instance.eval(value.toString());
			} else if(value instanceof Map) {
				Map<String, Object> map = (Map<String, Object>) value;
				if(i.getKey() == null) {
					JavaScriptLoader.instance.evalMap(map);
				} else {
					i.getKey().invokeByOrder(map);
				}
			}
		});
		this.isEvalled = true;
	}

	public Plugin getPlugin() {
		return this.plugin;
	}

	public void setPlugin(Plugin plugin) {
		this.plugin = plugin;
	}

	public IScriptHolder getJsHolder() {
		return this.jsHolder;
	}

	public void setJsHolder(IScriptHolder jsHolder) {
		this.jsHolder = jsHolder;
	}

	public List<KeyValue<JavaScriptInfo, Object>> getScripts() {
		return this.scripts;
	}

	public void setScripts(List<KeyValue<JavaScriptInfo, Object>> scripts) {
		this.scripts = scripts;
	}

	public boolean isEvalled() {
		return this.isEvalled;
	}
}
