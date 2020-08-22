/************************************************************
provide some function to simply the development

variable:
  global: points the global scope
  Lib: contains function of this library
  eval: eval the code or the file contents
  Lib.getClassloader: get ClassLoader of target plugin
  Lib.loadType: load a class by classloader and return a JavaPackage
************************************************************/

var global = this;
var Lib = {};
var JsLibPlugin = Packages.xingchen.jslib;
var eval = null;
(function() {
	var File = Packages.java.io.File;
	var JavaPlugin = Packages.org.bukkit.plugin.java.JavaPlugin;
	
	Lib.engine = JsLibPlugin.js.JavaScriptLoader.instance.getEngine();
	global.eval = function(code) {
		if(code instanceof File) {
			JsLibPlugin.js.JavaScriptLoader.instance["eval(java.io.File)"](code);
		} else {
			JsLibPlugin.js.JavaScriptLoader.instance["eval(java.lang.String)"](code);
		}
	};
	Lib.getClassloader = function(name, force) {
		var linkages = JsLibPlugin.export.LinkageLoader.instance.getLinkages();
		if(linkages.containsKey(name) && linkages.get(name).getClassloader() != null) {
			return linkages.get(name).getClassloader();
		}
		if(force == true) {
		    var plugins = Packages.org.bukkit.Bukkit.getServer().getPluginManager().getPlugins();
		    for(var i in plugins) {
		        if(plugins[i].getName() == name) {
		            if(plugins[i] instanceof JavaPlugin) {
		                var method = JavaPlugin.class.getDeclaredMethod("getClassLoader");
		                method.setAccessible(true);
		                return method.invoke(plugins[i]);
		            }
		        }
		    }
		}
		return null;
	}
	Lib.loadType = function(name, classloader) {
		if(classloader == undefined) {
			return Java.type(name);
		}
		
		var class_ = undefined;
		try {
			class_ = classloader.loadClass(name);
		} catch(e) {
			class_ = undefined;
		}
		if(class_ == undefined) {
			return Java.type(name);
		} else {
			return class_.static;
		}
	}
})();