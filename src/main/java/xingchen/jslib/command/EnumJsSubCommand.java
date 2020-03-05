package xingchen.jslib.command;

import java.util.HashMap;
import java.util.Map;

/**js命令的子命令*/
public enum EnumJsSubCommand {
	HELP("help", "jslib.command.help"),
	RUN("run", "jslib.command.run"),
	BOOK("book", "jslib.command.book");
	
	public static Map<String, EnumJsSubCommand> lookup;
	
	private String name;
	private String permission;
	
	private EnumJsSubCommand(String name, String permission) {
		this.name = name;
		this.permission = permission;
	}
	
	public boolean isSubCommand(String arg) {
		return this.name.equalsIgnoreCase(arg);
	}
	
	public String getName() {
		return this.name;
	}

	public String getPermission() {
		return this.permission;
	}
	
	static {
		lookup = new HashMap<>();
		for(EnumJsSubCommand subCommand : values()) {
			lookup.put(subCommand.getName(), subCommand);
		}
	}
}
