package xingchen.jslib.js;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.script.ScriptException;

import org.bukkit.plugin.Plugin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import xingchen.jslib.JavaScriptLibrary;

/**
 * 为null时为根据默认主JavaScript名调用代码
 * 为EMPTY对象时不调用代码
 * 当名字非空时根据给定的顺序进行调用
 * 
 * 例(info.json)：
 * {
 *     name:"名字",
 *     order:[
 *         "代码文件1",
 *         "代码文件2"
 *     ]
 * }
 */
public class JavaScriptInfo {
	public static final JavaScriptInfo EMPTY = new JavaScriptInfo(null, null);
	
	protected String name;
	protected List<String> invokeOrder;
	
	public JavaScriptInfo(String name, List<String> invokeOrder) {
		this.name = name;
		this.invokeOrder = invokeOrder;
	}

	/*@Override
	public boolean equals(Object object) {
		if(this == object) {
			return true;
		}
		
		if(object instanceof JavaScriptInfo) {
			JavaScriptInfo info = (JavaScriptInfo) object;
			if(this.getName() == info.getName()) {
				return true;
			}
			if(this.getName() == null) {
				return false;
			}
			
			return this.getName().equals(info.getName());
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		if(this.name == null) {
			return 0;
		}
		return this.name.hashCode();
	}*/
	
	/**
	 * 解析指定文件夹中的脚本信息文件
	 * 
	 * @param plugin 调用该方法的插件
	 * @param infoFile 信息文件所在的文件夹
	 * 
	 * @return 脚本信息对象
	 */
	public static JavaScriptInfo parse(Plugin plugin, File infoFile) throws UnsupportedEncodingException, FileNotFoundException {
		if(infoFile == null || !infoFile.exists() || infoFile.isDirectory()) {
			return EMPTY;
		}
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(infoFile), "utf-8"));
		
		JsonParser parse = new JsonParser();
		JsonElement json = parse.parse(reader);
		if(!json.isJsonObject()) {
			return EMPTY;
		}
		JsonObject object = json.getAsJsonObject();
		
		String name;
		if(object.has("name")) {
			name = object.get("name").getAsString();
		} else {
			name = plugin.getName();
		}
		
		List<String> invokeOrder = new ArrayList<>();
		if(object.has("order") && object.get("order").isJsonArray()) {
			JsonArray array = object.get("order").getAsJsonArray();
			for(int i=0;i<array.size();i++) {
				invokeOrder.add(array.get(i).getAsString());
			}
		}
		
		return new JavaScriptInfo(name, invokeOrder);
	}
	
	/**
	 * 按照信息对象上的顺序调用JavaScript代码
	 * 
	 * @param map JavaScript代码表
	 */
	public void invokeByOrder(Map<String, Object> map) {
		if(this == EMPTY) {
			return;
		}
		
		this.invokeOrder.stream().forEach(i -> {
			String code = null;
			if(i.lastIndexOf("/") >= 0) {
				Object object = JavaScriptLoader.instance.getMapCode(map, i.split("/"));
				if(object != null) {
					code = (String) object;
				}
			} else if(map.get(i) instanceof String) {
				code = (String) map.get(i);
			}
			
			if(code != null) {
				try {
					JavaScriptLoader.instance.getEngine().eval(code);
				} catch (ScriptException e2) {
					JavaScriptLibrary.instance.getConfigManager().getLogger().log(Level.WARNING, "Unable to eval " + i + ".js", e2);
				}
			}
		});
	}

	public String getName() {
		return this.name;
	}

	public List<String> getInvokeOrder() {
		return this.invokeOrder;
	}
}
