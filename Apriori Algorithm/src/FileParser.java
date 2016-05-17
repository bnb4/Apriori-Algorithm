import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 *	用來格式化輸入的檔案
 */
public class FileParser {
	
	// 儲存資料
	private AttributeData [] attributeDatas;
	
	/**
	 * 建構子
	 * @param path 檔案路徑
	 */
	public FileParser(String path) {
		
		boolean isReadAttribute = false;
		try (Scanner scanner = new Scanner(new File(path))) {
			// 讀取第一行(屬性名稱)
			while (!isReadAttribute && scanner.hasNext()) {
				isReadAttribute = loadAttribute(scanner.nextLine());
			}
			// 讀取資料行
			while (scanner.hasNext()) {
				addData(scanner.nextLine());
			}
		} catch (IOException e) { 
			e.printStackTrace(); 
		}
	}
	
	/**
	 * 載入屬性名稱 (需要放在文件讀取第一行)
	 * @param data 讀取資料
	 * @return 是否成功讀取
	 */
	private boolean loadAttribute(String data) {
		// 跳過空行
		if (data.trim().length() == 0 ) return false;
		
		// 處理屬性名稱
		String [] raw = data.trim().split(" ");
		
		// 創建儲存物件
		attributeDatas = new AttributeData [raw.length];
		for (int i = 0; i < raw.length; i++) 
			attributeDatas[i] = new AttributeData(raw[i]);
		return true;
	}
	
	/**
	 * 載入一筆資料 (資料只能為 True False True...)
	 * @param data 資料
	 */
	public void addData(String data) {
		// 跳過空行
		if (data.trim().length() == 0 ) return;

		// 分割資料
		String[] raw = data.trim().split(" ");
		
		// 存入資料
		for (int i = 0; i < raw.length; i++) 
			attributeDatas[i].addData(Boolean.parseBoolean(raw[i]));
	}
	
	/**
	 * 取得目前所有資料
	 * @return 格式過的資料
	 */
	public AttributeData [] getAllData() {
		return attributeDatas;
	}
	
}
