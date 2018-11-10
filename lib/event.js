Lib.Event = {};
(function() {
	Lib.Event.priority = Packages.org.bukkit.event.EventPriority;
	Lib.Event.createKey = function() {
		return new JsLibPlugin.event.EventHolder();
	};
	Lib.Event.defaultKey = Lib.Event.createKey();
	Lib.Event.register = function(event, callback, priority_, ignoreCancelled, key, plugin) {
		if(priority_ == undefined) {
			priority_ = Lib.Event.priority.NORMAL;
		}
		if(ignoreCancelled == undefined) {
			ignoreCancelled = false;
		}
		if(key == undefined) {
			key = Lib.Event.defaultKey;
		}
		if(plugin == undefined) {
			plugin = JsLibPlugin.JavaScriptLibrary.instance;
		}
		
		var result = {};
		var handlerList = event.getHandlerList();
		result["listener"] = key;
		result["executor"] = JsLibPlugin.js.JavaScriptUtil.registerEvent(handlerList, callback, priority_, ignoreCancelled, key, plugin);
		result["unregister"] = function() {
			JsLibPlugin.js.JavaScriptUtil.removeEvent(handlerList, result["listener"]);
		}
		
		return result;
	};
	Lib.Event.unregister = function(event, key) {
		if(key == undefined) {
			key = Lib.Event.defaultKey;
		}
		JsLibPlugin.js.JavaScriptUtil.removeEvent(event.getHandlerList(), key);
	};
	Lib.Event.events = {};
	Lib.Event.addDepend = function(scriptNames, callback) {
		var linkages = JsLibPlugin.export.LinkageLoader.instance.getLinkages();
		var need = scriptNames;
		if(typeof(scriptNames) == "string") {
			var need = [scriptNames];
		}
		
		for (var i in need) {
			if(linkages.containsKey(i) && linkages.get(i).isEvalled()) {
				need.splice(i, 1);
			}
		}
		if(need.length <= 0) {
			callback();
		} else {
			if(!("jsEvalled" in Lib.Event.events)) {
				Lib.Event.events["jsEvalled"] = {};
				Lib.Event.events["jsEvalled"].evalList = [];
				Lib.Event.events["jsEvalled"].event = Lib.Event.register(JsLibPlugin.event.register.JavaScriptFilesEvalledEvent, function(evt) {
					for(var i=0;i<Lib.Event.events["jsEvalled"].evalList.length;i++) {
						var list = Lib.Event.events["jsEvalled"].evalList[i];
						var index = list.need.indexOf(evt.getScriptName());
						if(index != -1) {
							list.need.splice(index, 1);
						}
						if(list.need.length <= 0) {
							(list.callback)();
							Lib.Event.events["jsEvalled"].evalList.splice(i, 1);
							i--;
						}
					}
					if(Lib.Event.events["jsEvalled"].evalList.length <= 0) {
						Lib.Event.events["jsEvalled"].event.unregister();
						delete Lib.Event.events["jsEvalled"];
					}
				});
			}
			
			Lib.Event.events["jsEvalled"].evalList.push({
				"need": need,
				"callback": callback
			});
		}
	};
})();