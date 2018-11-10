package xingchen.jslib.command;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.script.ScriptContext;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import xingchen.jslib.JavaScriptLibrary;
import xingchen.jslib.js.JavaScriptLoader;

public class JsCommand extends Command {
	public static final Pattern UNCOLOR = Pattern.compile("§.");
	
	public static final String COMMANDTIP = "§2请使用  §6/js help  §2来查看帮助";
	public static final String ONLYPLAYER = "§4只有玩家才能使用此命令";
	public static final String UNABLE = "§4此命令未开启";
	
	public JsCommand() {
		super("javascriptlib", "Command of JavaScriptLibrary plugin", "/js help", Arrays.asList("javascript", "js"));
	}
	
	@Override
	public boolean execute(CommandSender sender, String capital, String[] args) {
		if(args.length <= 0 || "help".equalsIgnoreCase(args[0])) {
			this.sendHelp(sender);
		} else {
			String subcommand = args[0].toLowerCase();
			if(EnumJsSubCommand.RUN.isSubCommand(subcommand)) {
				if(!JavaScriptLibrary.instance.getConfigManager().getCommandRunState()) {
					sender.sendMessage(UNABLE);
					return true;
				}
				if(args.length >= 2) {
					String code = IntStream.range(1, args.length).mapToObj(i -> args[i]).collect(Collectors.joining(" "));
					this.eval(sender, code);
				}
			} else if(EnumJsSubCommand.BOOK.isSubCommand(subcommand)) {
				if(!JavaScriptLibrary.instance.getConfigManager().getCommandBookState()) {
					sender.sendMessage(UNABLE);
					return true;
				}
				if(sender instanceof Player) {
					Player player = (Player) sender;
					ItemStack itemStack = player.getEquipment().getItemInMainHand();
					if(itemStack != null && itemStack.hasItemMeta()) {
						ItemMeta meta = itemStack.getItemMeta();
						if(meta instanceof BookMeta) {
							BookMeta bookmeta = (BookMeta) meta;
							StringBuffer sb = new StringBuffer();
							bookmeta.getPages().stream().forEach(i -> sb.append(i));
							String code = sb.toString();
							if(code != null && !code.isEmpty()) {
								code = UNCOLOR.matcher(code).replaceAll("");
								this.eval(player, code);
							}
						} else {
							sender.sendMessage("§4手上的物品不是代码书");
						}
					} else {
						sender.sendMessage("§4手上的物品不是代码书");
					}
				} else {
					sender.sendMessage(ONLYPLAYER);
				}
			} else {
				sender.sendMessage(COMMANDTIP);
			}
		}
		
		return true;
	}
	
	public void eval(CommandSender sender, String code) {
		try {
			this.buildCommandJsLib(sender, JavaScriptLoader.instance.getEngine().getContext());
			Object back = JavaScriptLoader.instance.getEngine().eval(code);
			if(back != null && back instanceof String) {
				sender.sendMessage(back.toString());
			}
		} catch (ScriptException e) {
			JavaScriptLibrary.instance.getConfigManager().getLogger().log(Level.WARNING, "Unable to eval javascript code: " + code, e);
			sender.sendMessage("§4在执行此js代码时出错");
			sender.sendMessage(e.getMessage());
		}
	}
	
	public void buildCommandJsLib(CommandSender sender, ScriptContext context) {
		context.setAttribute("me", sender, SimpleScriptContext.ENGINE_SCOPE);
	}
	
	public void sendHelp(CommandSender sender) {
		if(sender.hasPermission(EnumJsSubCommand.HELP.getPermission())) {
			sender.sendMessage(new String[] {
				" ",
				"§2§l/js run <jscode>  §6运行一段js代码",
				"§2§l/js book  §6运行主手上拿着的书里的js代码",
				" "
			});
		}
	}
}
