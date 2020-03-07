package xingchen.jslib.js;

import java.io.File;

import javax.annotation.Nullable;

import xingchen.jslib.config.ConfigManager;

/**
 * 脚本文件信息
 * 包含了信息文件和代码文件
 */
public class ScriptFiles {
	/**信息文件*/
	@Nullable
	protected File info;
	protected File jsFile;

	public ScriptFiles(File jsFile) {
		this(null, jsFile);
	}

	public ScriptFiles(File info, File jsFile) {
		this.info = info;
		this.jsFile = jsFile;
	}

	public File getInfo() {
		return this.info;
	}

	public File getJsFile() {
		return this.jsFile;
	}

	/**
	 * 寻找文件夹中的信息文件，并标记
	 *
	 * @param directory 目标文件夹
	 * @return 可能包含信息文件的{#ScriptFiles}
	 */
	public static ScriptFiles fromDirectory(File directory) {
		if(directory.exists() && directory.isDirectory()) {
			File info = new File(directory, ConfigManager.INFOFILENAME);
			if(!info.exists() || info.isDirectory()) {
				info = null;
			}
			return new ScriptFiles(info, directory);
		}

		return null;
	}
}
