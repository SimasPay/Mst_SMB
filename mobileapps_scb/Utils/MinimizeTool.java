import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;

public class MinimizeTool {
	public static final String SCRIPT_ELEMENT_START = "<script";
	public static final String JSDIR = "js/";
	public static final String JSFILE_SUFFIX = ".js";
	public static final String LINE_END = "\n";
	public static final String COMMENT_MULTI_START = "/*";
	public static final String COMMENT_MULTI_END = "*/";
	public static final String COMMENT_SINGLE = "//";

	public static void main(String[] args) {
		String indexHtmPath = args[0] + "/index.html";
		String outputFile = args[1];
		File indexHtm = new File(indexHtmPath);
		try {
			FileReader fileReader = new FileReader(indexHtm);
			BufferedReader br = new BufferedReader(fileReader);
			String tmp;
			tmp = br.readLine();
			ArrayList<File> retList = new ArrayList<File>();
			while (tmp != null) {
				String retValue = processLine(tmp);
				if (retValue != null) {
					retList.add(new File(args[0] + "/" + retValue));
				}
				tmp = br.readLine();
			}

			fileReader.close();
			br.close();
			processFilesList(retList, outputFile);

		} catch (Exception e) {
			System.out.print(e);
		}
	}

	private static String processLine(String line) throws Exception {
		String fileName = null;

		if (line.contains(SCRIPT_ELEMENT_START) && line.contains(JSDIR)) {
			int indexStart = line.indexOf(JSDIR);
			int endIndex = line.indexOf(JSFILE_SUFFIX) + 3;
			fileName = line.substring(indexStart, endIndex);
			System.out.println(fileName);
		}
		return fileName;
	}

	private static void processFilesList(ArrayList<File> jsFileList,String outputFile) throws Exception {

		File outputFileObj = new File(outputFile);
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileObj));

		Iterator<File> it = jsFileList.iterator();

		while (it.hasNext()) {

			File file = it.next();
			System.out.println(file.getName());
			FileReader fileReader = new FileReader(file);
			BufferedReader br = new BufferedReader(fileReader);
			String tmp;
			tmp = br.readLine();
			while (tmp != null) {
				tmp = tmp.trim();
				if (tmp.contains(COMMENT_SINGLE)|| tmp.contains(COMMENT_MULTI_START)) {
					if (tmp.contains(COMMENT_SINGLE)) {
						tmp = tmp.substring(0, tmp.indexOf(COMMENT_SINGLE));
						if (!tmp.isEmpty()) {
							writer.write(tmp);
						}
					}
					if (tmp.contains(COMMENT_MULTI_START)) {
						String beforeComment = tmp.substring(0,tmp.indexOf(COMMENT_MULTI_START));
						if (!beforeComment.isEmpty()) {
							writer.write(beforeComment);
						}
						while (tmp != null) {
							tmp.trim();
							if (tmp.contains(COMMENT_MULTI_END)) {
								String afterComment = tmp.substring(tmp.indexOf(COMMENT_MULTI_END)+ COMMENT_MULTI_END.length());
								if (!afterComment.isEmpty()) {
									writer.write(afterComment);
								}
								break;
							}
							tmp = br.readLine();
						}
					}
				} else {
					if (!tmp.isEmpty()) {
						writer.write(tmp);
					}
				}
				tmp = br.readLine();
			}
			fileReader.close();
			br.close();

		}

		writer.flush();
		writer.close();
	}
}