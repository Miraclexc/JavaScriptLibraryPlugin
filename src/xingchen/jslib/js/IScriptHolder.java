package xingchen.jslib.js;

/**
 * 本地脚本接口
 * 可标记本地脚本的文件/文件夹
 */
public interface IScriptHolder {
	/**脚本文件/文件夹(可有多个)*/
	public ScriptFiles[] getScriptFiles();
}
