package xingchen.jslib.config;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.jar.JarInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import xingchen.jslib.JavaScriptLibrary;
import xingchen.jslib.export.PluginScripts;

/**
 * 配置管理器
 */
public class ConfigManager {
	/**脚本信息文件名*/
    public static final String INFOFILENAME = "info.json";
    public static List<String> MAINJSNAME = new ArrayList<>();
    
    private Plugin plugin;
    private Logger logger;
    
    private FileConfiguration configFile;
    
    /**run子命令是否开启*/
    private boolean commandRun;
    /**book子命令是否开启*/
    private boolean commandBook;
    /**是否开启和其他插件的联动*/
    private boolean linkage;
    
    /**支持库代码*/
    private PluginScripts libraries;
    /**用户代码*/
    private PluginScripts scripts;

    public ConfigManager(Plugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        if(!this.checkCongig("config.yml")) {
            this.logger.info("配置文件未创建，正在创建...");
            plugin.saveDefaultConfig();
        } else {
            this.logger.info("配置文件已经创建，开始加载...");
        }

        this.checkLibraries();
        this.configFile = this.plugin.getConfig();
        
        Object command = this.configFile.get("command");
        if(command instanceof ConfigurationSection) {
            ConfigurationSection commandCS = (ConfigurationSection) command;
            this.commandRun = commandCS.getBoolean("run", false);
            this.commandBook = commandCS.getBoolean("book", false);
        } else if(command instanceof Boolean) {
            this.setAllCommand(((Boolean)command).booleanValue());
        } else {
            this.setAllCommand(false);
        }

        //加载支持库和用户代码
        this.linkage = this.configFile.getBoolean("linkage", false);
        this.libraries = this.loadScript("lib");
        if(this.libraries != null) {
            this.scripts = this.loadScript("scripts");
        } else {
            this.logger.info("发现js支持库损坏。");
            this.logger.info("请删除原支持库后重新启动。插件将会自动修复。");
        }
    }
    
    /**
     * 读取文件中的内容
     * 
     * @param file 目标文件
     * 
     * @return 文件中的内容(如果文件不存在或者为文件夹，则返回null)
     * 
     * @exception IOException 如果文件读取时出现异常
     */
    public String readFrom(File file) throws IOException {
		if(file.exists() && !file.isDirectory()) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
			LinkedList<String> stringList = new LinkedList<>();

            String line;
            while((line = reader.readLine()) != null) {
            	stringList.add(line);
            }

            reader.close();
            return stringList.stream().collect(Collectors.joining("\n"));
		}
		
		return null;
	}

    /**
     * 根据{@link ConfigManager#MAINJSNAME}中的顺序，获取代码表中的主脚本名
     */
    public static String getMainJsName(Map<String, Object> map) {
        for(int i=0;i<MAINJSNAME.size();i++) {
            String name = MAINJSNAME.get(i);
            if(map.containsKey(name) && map.get(name) instanceof String) {
                return name;
            }
        }

        return null;
    }

    /**
     * 获取本插件jar路径
     */
    public URL getJarURL() {
    	return JavaScriptLibrary.class.getProtectionDomain().getCodeSource().getLocation();
    }
    
    /**
     * 检查数据文件夹里目标文件是否存在
     */
    public boolean checkCongig(String configName) {
        File file = new File(this.plugin.getDataFolder(), configName);
        return file.exists();
    }

    public boolean saveLibrary(File directory, String name) {
        File file = new File(directory, name);
        if(!file.exists()) {
            this.plugin.saveResource(name, false);
            return true;
        } else {
            return false;
        }
    }

    /*
     * 递归检查支持库文件
     * 
     * @param target 要保存的位置
     * @param dir 支持库原文件所在位置
     * @param name 支持库名
     *
    public void checkLibrary(File target, File dir, String name) {
    	File file = new File(dir, name);
    	if(file.exists()) {
    		if(file.isDirectory()) {
    			Arrays.stream(file.listFiles()).forEach(i -> checkLibrary(target, dir, name + File.separator + i.getName()));
    		} else {
    			this.saveLibrary(target, name);
    		}
    	}
    }*/
    
    /**
     * 检查并修复支持库文件
     */
    public void checkLibraries() {        
       try {
		   JarInputStream jarZip = new JarInputStream(new BufferedInputStream(this.getJarURL().openStream()));
		   ZipEntry entry;
		   while((entry = jarZip.getNextEntry()) != null) {
			   if(!entry.isDirectory() && entry.getName().startsWith("lib/")) {
				   this.saveLibrary(this.plugin.getDataFolder(), entry.getName());
			   }
		   }
		   jarZip.close();
       } catch (IOException e) {
    	   this.logger.log(Level.SEVERE, "Failed to check JavaScript Libraries.", e);
       }
    }

    /**
     * 加载用户插件
     */
    public PluginScripts loadScript(String fileName) {
        File file = new File(this.plugin.getDataFolder(), fileName);
        if(file.exists()) {
        	return new PluginScripts(this.plugin, new File[] {file});
        }
        
        return null;
    }

    /**
     * 解析脚本文件，并返回其代码
     * 
     * @param file 目标文件
     * @param suffix 脚本文件扩展名
     * 
     * @return 如果文件为文件夹，则返回{#Map}，否则返回{#String}
     */
    public Object parseScriptFile(File file, String suffix) {
        if(file.isDirectory()) {
            HashMap<String, Object> map = new HashMap<>();
            Arrays.stream(file.listFiles()).forEach(i -> {
                Object script = this.parseScriptFile(i, suffix);
                if(script != null && (!(script instanceof Map) || !((Map) script).isEmpty())) {
                    String fileName = i.getName();
                    if(i.isDirectory()) {
                        map.put(fileName, script);
                    } else {
                        int lastp = fileName.lastIndexOf(".");
                        if(suffix.equalsIgnoreCase(fileName.substring(lastp + 1))) {
                            String name = fileName.substring(0, lastp);
                            map.put(name, script);
                        }
                    }
                }
            });
            
            return map;
        } else {
            String name = file.getName();
            if(suffix.equalsIgnoreCase(name.substring(name.lastIndexOf(".") + 1))) {
                try {
                    return this.readFrom(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    public void setPlugin(Plugin plugin) {
        this.plugin = plugin;
    }

    public Logger getLogger() {
        return this.logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public FileConfiguration getConfigFile() {
        return this.configFile;
    }

    public void setConfigFile(FileConfiguration configFile) {
        this.configFile = configFile;
    }

    public void setAllCommand(boolean state) {
        this.commandRun = state;
        this.commandBook = state;
    }

    public boolean getCommandRunState() {
        return this.commandRun;
    }

    public void setCommandRunState(boolean commandRun) {
        this.commandRun = commandRun;
    }

    public boolean getCommandBookState() {
        return this.commandBook;
    }

    public void setCommandBookState(boolean commandBook) {
        this.commandBook = commandBook;
    }

    public boolean isLinkage() {
        return this.linkage;
    }

    public void setLinkage(boolean linkage) {
        this.linkage = linkage;
    }

    public PluginScripts getScripts() {
        return this.scripts;
    }

    public void setScripts(PluginScripts scripts) {
        this.scripts = scripts;
    }

    public PluginScripts getLib() {
        return this.libraries;
    }
    
    static {
        MAINJSNAME.add("main");
        MAINJSNAME.add("root");
    }
}
