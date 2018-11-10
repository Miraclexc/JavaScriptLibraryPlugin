package xingchen.jslib.event.register;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import xingchen.jslib.export.LinkageLoader;

public class JavaScriptPreRegisterEvent extends AbstractJavaScriptPluginEvent implements Cancellable {
	private static HandlerList handlerList = new HandlerList();
	
	protected boolean isCancelled;
	
	public JavaScriptPreRegisterEvent(LinkageLoader linkageLoader, String scriptName) {
		super(linkageLoader, scriptName);
	}
	
	@Override
	public boolean isCancelled() {
		return this.isCancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.isCancelled = cancel;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlerList;
	}
	
	public static HandlerList getHandlerList() {
		return handlerList;
	}
}
