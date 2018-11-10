package xingchen.jslib.js.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * 由于在JavaScript中继承时不能为类添加超类以外的方法，所以创建类这个类以在JavaScript中实现自定义事件
 */
public class JavaScriptEvent extends Event {
	private static HandlerList handlerList = new HandlerList();
	
	/**事件唯一标识符(最好不要重复)*/
	protected String name;
	/**数据对象(一般是JavaScript对象)*/
	protected Object object;
	
	public JavaScriptEvent(String name, Object object) {
		this.name = name;
		this.object = object;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlerList;
	}
	
	public String getName() {
		return this.name;
	}

	public Object getObject() {
		return this.object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public static HandlerList getHandlerList() {
		return handlerList;
	}
}
