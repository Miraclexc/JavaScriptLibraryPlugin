package xingchen.jslib.event.register;

import org.bukkit.event.Event;
import xingchen.jslib.export.LinkageLoader;

public abstract class AbstractJavaScriptPluginEvent extends Event {
	protected LinkageLoader linkageLoader;
	protected String scriptName;
	
	public AbstractJavaScriptPluginEvent(LinkageLoader linkageLoader, String scriptName) {
		this.linkageLoader = linkageLoader;
		this.scriptName = scriptName;
	}

	public LinkageLoader getLinkageLoader() {
		return this.linkageLoader;
	}

	public void setLinkageLoader(LinkageLoader linkageLoader) {
		this.linkageLoader = linkageLoader;
	}

	public String getScriptName() {
		return this.scriptName;
	}
}
