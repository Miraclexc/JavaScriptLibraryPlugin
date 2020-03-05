var global = this;
var Lib = {};
var JsLibPlugin = Packages.xingchen.jslib;
var eval = null;
(function() {
	var File = java.io.File;
	
	Lib.engine = JsLibPlugin.js.JavaScriptLoader.instance.getEngine();
	global.eval = function(code) {
		if(code instanceof File) {
			JsLibPlugin.js.JavaScriptLoader.instance["eval(java.io.File)"](code);
		} else {
			JsLibPlugin.js.JavaScriptLoader.instance["eval(java.lang.String)"](code);
		}
	};
})();