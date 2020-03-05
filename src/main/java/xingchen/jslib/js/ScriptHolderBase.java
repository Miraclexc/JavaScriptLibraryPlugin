package xingchen.jslib.js;

public class ScriptHolderBase implements IScriptHolder {
	protected ScriptFiles[] scriptFiles;
	
	public ScriptHolderBase(ScriptFiles[] scriptFiles) {
		this.scriptFiles = scriptFiles;
	}

	@Override
	public ScriptFiles[] getScriptFiles() {
		return this.scriptFiles;
	}
}
