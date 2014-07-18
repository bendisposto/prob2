package de.prob.bmotion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BMotionStudioUtil {

	public static void writeStringToFile(String str, File file) {
		try {
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			FileOutputStream fop = new FileOutputStream(file);
			// get the content in bytes
			byte[] contentInBytes = str.getBytes();
			fop.write(contentInBytes);
			fop.flush();
			fop.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String readFile(final String filename) {
		String content = null;
		File file = new File(filename);
		try {
			FileReader reader = new FileReader(file);
			char[] chars = new char[(int) file.length()];
			reader.read(chars);
			content = new String(chars);
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	public static void fixSvgImageTags(Document template) {
		for (Element e : template.getElementsByTag("svg")) {
			// Workaround, since jsoup renames svg image tags to img
			// tags ...
			Elements imgTags = e.getElementsByTag("img");
			imgTags.tagName("image");
		}
	}

}
