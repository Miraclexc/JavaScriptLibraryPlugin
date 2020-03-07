package xingchen.jslib.js;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

/**
 * JavaScript工具库
 * 为JavaScript的使用添加了一些便捷的方法
 */
public class JavaScriptUtil {
    /**
     * 注册事件
     *
     * @param eventClass      handlerList 可通过事件类的getHandlerList()方法获取
     * @param callback        当事件触发时回调的JavaScript函数
     * @param priority        事件优先级(低优先级的代码先执行)
     * @param ignoreCancelled 代码是否被已经取消的事件触发(如果事件被低优先级的取消，那高优先级的代码将不会执行)
     * @param key             事件监听器(每种事件只能被每个监听器注册一次)
     * @param plugin          注册事件的插件
     * @return {#EventExecutor}
     */
    public static EventExecutor registerEvent(HandlerList handlerList, ScriptObjectMirror callback, EventPriority priority, boolean ignoreCancelled, Listener key, Plugin plugin) {
        EventExecutor eventExecutor = new EventExecutor() {
            public void execute(Listener listener, Event event) {
                callback.call(callback, event);
            }
        };
        handlerList.register(new RegisteredListener(key, eventExecutor, priority, plugin, ignoreCancelled));
        return eventExecutor;
    }

    /**
     * 注销事件
     *
     * @param handlerList 可通过事件类的getHandlerList()方法获取
     * @param key         事件监听器
     */
    public static void removeEvent(HandlerList handlerList, Listener key) {
        handlerList.unregister(key);
    }
}
