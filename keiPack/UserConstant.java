package keiPack;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;




/**
 * ユーザー定数の管理用クラス
 * @author Keita
 * @version 1.0
 */
class UserConstant{

	private String version = "1.0";	//@versionと同値にする

	private HashMap<String, BigDecimal> data = new HashMap<String, BigDecimal>();

	public UserConstant() throws IOException {
		loadData();
	}

	private void loadData() throws IOException {
		URL dataUrl = this.getClass().getResource("data/UserConstant.txt");
		if (dataUrl != null){
			File file;
			try {
				file = new File(dataUrl.toURI());
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String line = reader.readLine();	//version情報の書いてある場所
				while ((line = reader.readLine()) != null) {
					int index = line.indexOf(",");
					String name = line.substring(0, index);
					BigDecimal value = new BigDecimal(line.substring(index+2));
					data.put(name, value);
				}
				reader.close();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 返す文字列は正常に登録できたのか同名のものがあるのかのメッセージ
	 * "完了" or "同名の定数があります"
	*/
	public String putNewData(String name, BigDecimal val) {
		if (!data.containsKey(name)) {
			data.put(name, val);
			return "完了";
		}
		return "同名の定数があります";
	}

	public void editData(String oldName, String newName, BigDecimal newVal) {
		if (oldName.equals(newName)) {
			data.replace(oldName, newVal);
		}else {
			data.remove(oldName);
			data.put(newName, newVal);
		}
	}

 	public void deleteData(String name) {
		data.remove(name);
	}

	public void deleteAllDatas() {
		data.clear();
	}

	public void saveDatas() throws IOException {
		URL dataUrl = this.getClass().getResource("data/UserConstant.txt");
		if (dataUrl != null) {
			File file;
			try {
				file = new File(dataUrl.toURI());
				PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
				Object[] arr = data.keySet().toArray();
				String[] keys = Arrays.copyOf(arr, arr.length, String[].class);
				writer.println("ver." + version);
				for (int i = 0; i < data.size(); i++) {
					writer.println(keys[i] + ", " + data.get(keys[i]));
				}
				writer.close();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
	}


	public HashMap<String, BigDecimal> searchNames(String str) {
		if (str.isEmpty()) {
			return data;
		}
		HashMap<String, BigDecimal> result = new HashMap<String, BigDecimal>();
		ArrayList<String> names = new ArrayList<>(data.keySet());
		for (int i = 0; i < names.size(); i++) {
			if (names.get(i).indexOf(str) != -1) {
				result.put(names.get(i), data.get(names.get(i)));
			}
		}
		return result;
	}

	public HashMap<String, BigDecimal> getDatas() {
		return data;
	}

}