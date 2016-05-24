import java.util.List;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 *	用來格式化輸入的檔案
 */
public class FileParser {
	
	// 儲存資料
	private static Map<String, String[]> attibutesMap = new HashMap<String, String[]>();
	private static List<Map<String, String>> data = new ArrayList<Map<String, String>>();
	private static List<String> attribute = new ArrayList<String>();

	public static String PATH = "";
	
	private static boolean readFile() {
		
		if (PATH.equals("")){
			return false;
		}
		
		boolean isReadAttribute = false;
		try (Scanner scanner = new Scanner(new File(PATH))) {
			// 讀取第一行(屬性名稱)
			
			while (scanner.hasNext()) {
				String line = scanner.nextLine().trim();

				if (line.equals("﻿@Attribute")) {
					isReadAttribute = true;
					continue;
				}
				if (line.equals("@Data")) {
					isReadAttribute = false;
					continue;
				}
				if (isReadAttribute) {
					loadAttribute(line);
				}
				
				if (!isReadAttribute) {
					addData(line);
				}
			}
			
			return true;
			
		} catch (IOException e) { 
			e.printStackTrace(); 
			return false;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * 載入屬性名稱與定義
	 * @param line 讀取資料
	 */
	private static void loadAttribute(String line) {
		
		// 跳過空行
		if (line.trim().length() == 0 ) {
			return;
		}
		
		// 處理屬性名稱與定義
		String[] att = line.split(":");
		String[] values = att[1].trim().split("\\s*,\\s*");
		
		attibutesMap.put(att[0].trim(), values);
		attribute.add(att[0].trim());
	}
	
	/**
	 * 載入一筆資料
	 * @param line 資料
	 */
	public static void addData(String line) {
		
		// 跳過空行
		if (line.trim().length() == 0 ) {
			return;
		}
		
		// 分割資料
		String[] values = line.trim().split("\\s*\t\\s*");
		Map<String, String> map = new HashMap<>();
		
		int index = 0;
		//比對資料是否符合定義
		for (String key : attribute) {
			boolean isExist = Arrays.asList(attibutesMap.get(key)).contains(values[index]);
			if (isExist) {
				map.put(key, values[index]);
			}
			index++;
		}
		
		// 存入資料
		if (map.size() == attibutesMap.keySet().size()){
			data.add(map);
		}

	}
	
	/**
	 * 取得屬性定義
	 * @return 格式過的屬性資料
	 */
	public static Map<String, String[]> getAttributeInfo() {
		return attibutesMap;
	}
	
	/**
	 * 取得屬性定義
	 * @return 格式過的屬性資料
	 */
	public static List<String> getAttributes() {
		return attribute;
	}
	
	
	/**
	 * 取得所有資料
	 * @return 格式過的資料
	 */
	public static List<Map<String, String>> getAllData() {
		return data;
	}
	
	/**
	 * 設定檔案路徑
	 * @param 路徑
	 */
	public static boolean setPath(String path) {
		PATH = path;
		return readFile();
	}
	
}
