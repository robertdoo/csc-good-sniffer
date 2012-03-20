package database;

import java.util.HashMap;

public class FilePreprocess {

	public static String replace(String line) {

		HashMap<String,String> map = new HashMap<String,String>();
		map.put(",", "£¬");
		map.put(".", "¡£");
		map.put("<", "¡´");
		map.put(">", "¡µ");
		map.put("|", "¡¬");
		map.put("<", "¡¶");
		map.put(">", "¡·");
		map.put("[", "¡²");
		map.put("]", "¡³");
		map.put("?", "©t");
		map.put("?", "£¿");
		map.put("\"", "¡°");
		map.put("\"", "¡±");
		map.put(":", "£º");
		map.put(",", "¡¢");
		map.put("(", "£¨");
		map.put(")", "£©");
		map.put("[", "¡¾");
		map.put("]", "¡¿");
		map.put("-", "¡ª");
		map.put("~", "¡«");
		map.put("!", "£¡");
		map.put("'", "¨F");

		int length = line.length();
		for (int i = 0; i < length; i++) {
			String charat = line.substring(i, i + 1);
			if (map.get(charat) != null) {
				line = line.replace(charat, (String) map.get(charat));
			}
		}

		return line;
	}
	
	/*
	public static File charactorProcess(File file, String destFile) throws Exception {
		BufferedWriter writer = new BufferedWriter(new FileWriter(destFile));

		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = reader.readLine();

		while (line != null) {
				String newline = replace(line);
				writer.write(newline);
				writer.newLine();
			line = reader.readLine();
		}

		reader.close();
		writer.close();

		return new File(destFile);
	}
	*/
}
