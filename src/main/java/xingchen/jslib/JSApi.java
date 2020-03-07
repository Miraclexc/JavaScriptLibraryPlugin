package xingchen.jslib;

import java.io.File;

import org.bukkit.plugin.Plugin;

import xingchen.jslib.export.LinkageLoader;
import xingchen.jslib.js.ScriptFiles;
import xingchen.jslib.js.ScriptHolderBase;

public class JSApi {
    private static JSApi instance;

    /**
     * 快速注册js
     *
     * @param plugin       注册js的插件
     * @param pluginJsName 插件提供的js的名字（为null则不进行插件js注册）
     * @param pluginJsFile 插件提供的js所在的文件夹
     * @param userJsName   使用者的js的名字（为null则不进行使用者js注册）
     * @param userJsFile   使用者的js所在的文件夹
     */
    public void registerJavaScript(Plugin plugin, String pluginJsName, File pluginJsFile, String userJsName, File userJsFile) {
        if(pluginJsName != null) {
            LinkageLoader.instance.registerLinkage(pluginJsName, plugin, new ScriptHolderBase(new ScriptFiles[] {ScriptFiles.fromDirectory(pluginJsFile)}));
        }

        if(userJsName != null) {
            LinkageLoader.instance.registerLinkage(userJsName, plugin, new ScriptHolderBase(new ScriptFiles[] {ScriptFiles.fromDirectory(userJsFile)}));
        }
    }

    public static JSApi getApi() {
        return instance;
    }
}
