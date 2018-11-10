package xingchen.jslib.event.register;

import org.bukkit.event.HandlerList;

import xingchen.jslib.export.LinkageLoader;

public class JavaScriptFilesEvalledEvent extends AbstractJavaScriptPluginEvent {
	private static HandlerList handlerList = new HandlerList();
	
	public JavaScriptFilesEvalledEvent(LinkageLoader linkageLoader, String scriptName) {
		super(linkageLoader, scriptName);
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlerList;
	}
	
	public static HandlerList getHandlerList() {
		return handlerList;
	}
}
