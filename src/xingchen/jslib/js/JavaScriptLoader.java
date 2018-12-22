package xingchen.jslib.js;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import xingchen.jslib.JavaScriptLibrary;
import xingchen.jslib.config.ConfigManager;

/**
 * JavaScript管理器
 */
public class JavaScriptLoader {
	public static JavaScriptLoader instance;
	
	private ScriptEngineManager manager;
	/**JavaScript(nashorn)引擎*/
	private ScriptEngine engine;
	
	public JavaScriptLoader() {
		this.manager = new ScriptEngineManager();
		this.engine = this.manager.getEngineByName("nashorn");
	}
	
	/**
	 * 执行map里主js(根据{@link ConfigManager#MAINJSNAME}获取)的代码
	 */
	public void evalMap(Map<String, Object> map) {
		ConfigManager config = JavaScriptLibrary.instance.getConfigManager();
		String name = ConfigManager.getMainJsName(map);
		
		if(name != null) {
			String script = (String) map.get(name);
			try {
				this.engine.eval(script);
			} catch (ScriptException e2) {
				config.getLogger().log(Level.WARNING, "Unable to eval " + name + ".js", e2);
			}
		}
	}
	
	/**
	 * 调用文件里的代码
	 * 
	 * @param file 目标文件
	 * 
	 * @return 引擎eval方法的返回值
	 * 
	 * @exception IOException 读取文件时发生异常
	 * @exception RuntimeException {#ScriptException}
	 */
	public Object eval(File file) throws IOException {
		String code = JavaScriptLibrary.instance.getConfigManager().readFrom(file);
		if(code != null) {
			return this.eval(code);
		}
		
		return null;
	}
	
	/**
	 * 通过引擎编译并执行代码
	 * 
	 * @param code JavaScript代码
	 * 
	 * @return 引擎eval方法的返回值
	 * 
	 * @exception RuntimeException 执行代码时出现的异常(包装了{#ScriptException})
	 */
	public Object eval(String code) {
		try {
			return this.engine.eval(code);
		} catch (ScriptException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 根据列表获取指定层的代码
	 * 
	 * @param map JavaScript表
	 * @param path 层列表
	 * 
	 * @return Object（可能为代码）
	 */
	public Object getMapCode(Map<String, Object> map, String[] path) {
		Object object = map;
		for(int i=0;i<path.length;i++) {
			if(object instanceof Map) {
				object = ((Map) object).get(path[i]);
			} else {
				return null;
			}
		}
		return object;
	} 
	
	public ScriptEngine getEngine() {
		return this.engine;
	}

	public static void init() {
		instance = new JavaScriptLoader();
		
		ConfigManager config = JavaScriptLibrary.instance.getConfigManager();
		
		if(config.getLib() != null) {
			config.getLib().loadScripts();
			config.getLib().evalAll();
		}
		
		if(config.getScripts() != null) {
			config.getScripts().loadScripts();
			config.getScripts().evalAll();
		}
	}
}
