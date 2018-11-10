package xingchen.jslib.js.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * 实现了{#Cancellable}的事件
 * 见：{#JavaScriptEvent}
 */
public class JavaScriptCancellableEvent extends JavaScriptEvent implements Cancellable {
	private static HandlerList handlerList = new HandlerList();
	
	private boolean isCancelled;
	
	public JavaScriptCancellableEvent(String name, Object object) {
		super(name, object);
		this.isCancelled = false;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlerList;
	}
	
	@Override
	public boolean isCancelled() {
		return this.isCancelled;
	}

	@Override
	public void setCancelled(boolean isCancelled) {
		this.isCancelled = isCancelled;
	}

	public static HandlerList getHandlerList() {
		return handlerList;
	}
}
