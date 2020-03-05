var load = null;
(function() {
	var File = java.io.File;
	
	global.load = function (dir, name) {
		if(dir instanceof java.io.File && name == undefined) {
			return eval(dir);
		}
		
		if(dir == undefined || name == undefined) {
			name = dir || name;
			dir = JsLibPlugin.JavaScriptLibrary.instance.getDataFolder().getParent();
		}
		
		if(typeof(dir) == "string") {
			dir = new File(dir);
		}
		if(typeof(name) != "string") {
			name = name.toString();
		}
		
		var path = new File(dir, name);
		if(path.exists()) {
			if(path.isDirectory()) {
				var files = path.listFiles();
				var result = [];
				for(var i in files) {
					result.push(global.load(files[i]));
				}
				return result;
			} else {
				return eval(path);
			}
		}
		
		return null;
	}
})();