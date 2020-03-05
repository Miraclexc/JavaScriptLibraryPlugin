package xingchen.jslib.command;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandMap;

import xingchen.jslib.JavaScriptLibrary;

public class CommandLoader {
	public static CommandMap commandMap;
	
	public static JsCommand jsCommand;
	
	public static void init(JavaScriptLibrary plugin) {
		Server server = Bukkit.getServer();
		try {
			commandMap = (CommandMap) server.getClass().getMethod("getCommandMap").invoke(server);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		
		if(commandMap == null) {
			return;
		}
		
		jsCommand = new JsCommand();
		commandMap.register(plugin.getPluginName(), jsCommand);
	}
}
