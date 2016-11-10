package com.zk.zkconfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class FileUtils {	

	public static byte[] readFile(File file) throws IOException {
		if (file != null && file.exists() && file.canRead()) {
			FileReader reader = new FileReader(file);
			char[] bytes = new char[1024];
			StringBuffer buffer = new StringBuffer();
			int len;
			while ((len = reader.read(bytes)) != -1) {
				buffer.append(bytes, 0, len);
			};
			reader.close();
			return buffer.toString().getBytes();
		}
		return null;
	}

	public static int writeFile(byte[] data, String filePath) {
		File file = new File(filePath);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		}
		try {
			FileOutputStream outputStream = new FileOutputStream(file);
			outputStream.write(data, 0, data.length);
			try {
				outputStream.close();
			} catch (Exception e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
			return 0;
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}		
		return -1;
	}	

}
