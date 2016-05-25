import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

public class Gui extends JFrame implements ActionListener{

	private static final long serialVersionUID = 1L;
	
	private int height = 600;
	private int width = 1000;
	
	/* gui 元件 */
	private JPanel filePanel = new JPanel();
	private JLabel filePath = new JLabel("選擇檔案");
	private JButton btnChooseFile = new JButton("選擇檔案");
	
	private JTextArea attributeArea = new JTextArea();
	
	private JLabel labelCoverage = new JLabel("涵蓋率(%)：");
	private JTextField textCoverage = new JTextField();
	private JButton btnStart = new JButton("開始");
	
	private DefaultTableModel dataTableModel = new readOnlyTableModel(); 
	private JTable dataTable = new JTable(dataTableModel);
	
	private DefaultTableModel resultTableModel = new readOnlyTableModel(); 
	private JTable resultTable = new JTable(resultTableModel);
	
	private DefaultTableModel ruleTableModel = new readOnlyTableModel(); 
	private JTable ruleTable = new JTable(ruleTableModel);
	
	/* 資料 */
	private String[] dataColumns = {};
	private List<Map<String, String>> data = new ArrayList<>();
	
	private String[] resultColumns = {"項目", "涵蓋率"};
	private Map<Map<String, String>, Double> resultData = new HashMap<Map<String,String>, Double>();
	
	private String[] ruleColumns = {"法則", "涵蓋率", "正確率"};
	private AssociationRule[] ruleData;
	
	private double coverage = 0;

	public Gui() {
		super("Apriori Algorithm");

		this.setSize(width, height);
		this.setPreferredSize(new Dimension(width, height));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);          
		this.getContentPane().setBackground(Color.white);
		this.setResizable(false);
		this.setLayout(null);
		
		
		filePanel.setBounds(30, 20, 450, 50);
		filePanel.setLayout(null);
		
		filePath.setBounds(115, 10, 335, 30);
		btnChooseFile.setBounds(5, 10, 100, 30);
	
		filePanel.add(btnChooseFile);
		filePanel.add(filePath);
		
		
		attributeArea.setOpaque(true);
		attributeArea.setBounds(30, 85, 450, 200);
		attributeArea.setBackground(new Color(255, 255, 158));
		attributeArea.setFont(new Font("Serif", 0, 14));
		attributeArea.setEditable(false); 
		Border border = BorderFactory.createDashedBorder(Color.BLACK, 5, 2);
		attributeArea.setBorder(BorderFactory.createCompoundBorder(border, 
		            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		attributeArea.setLineWrap(true);
		attributeArea.setWrapStyleWord(true);
		
		
		JScrollPane scrollPane = new JScrollPane(dataTable);
	    scrollPane.setBounds(30, 300, 450, 200);

	    labelCoverage.setBounds(60, 510, 100, 50);
	    textCoverage.setBounds(140, 520, 100, 30);
	    btnStart.setBounds(350, 520, 100, 30);
	    btnStart.setEnabled(false);
	    
	    JScrollPane resultPanel = new JScrollPane(resultTable);
	    resultPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	    resultPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	    resultPanel.setBounds(520, 20, 440, 250);
	    
		JScrollPane rulePanel = new JScrollPane(ruleTable);
		rulePanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		rulePanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		rulePanel.setBounds(520, 280, 440, 250);
		
		buildDataTable();
		buildResultTable();
		buildRuleTable();
		
		btnChooseFile.addActionListener(this);
	    btnStart.addActionListener(this);
	    
	    add(resultPanel);
	    add(rulePanel);
	    add(labelCoverage);
	    add(textCoverage);
	    add(btnStart);
	    add(attributeArea);
		add(scrollPane);
		add(filePanel);
		
		this.pack();
		this.setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnChooseFile) {
			JFileChooser chooser = new JFileChooser();
			//設定chooser的預設路徑
			chooser.setCurrentDirectory(new java.io.File("."));
			//限定選取檔案格式
		    FileNameExtensionFilter filter = new FileNameExtensionFilter("txt", "txt");
		    chooser.setFileFilter(filter);
		    
		    int returnVal = chooser.showOpenDialog(this);
		    
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		    	String path = chooser.getSelectedFile().getPath();
		    	filePath.setText(path);
		    	boolean isValidFile = FileParser.setPath(path);
		    	
		    	//判斷檔案是否正確解析
		    	if (isValidFile) {
		    		//將檔案顯示於gui
		    		clearAllTable();
		    		setAttribute(FileParser.getAttributes());
		    		setData(FileParser.getAllData());
		    		showAttribute(FileParser.getAttributeInfo());
		    		
		    		//將檔案資料傳送至AprioriAlogorithm
		    		AprioriAlgorithm.get().setAttributes(FileParser.getAttributeInfo());
					AprioriAlgorithm.get().setDatas(FileParser.getAllData());
					
					//開啟Start按鈕
		    		btnStart.setEnabled(true);
		    	} else {
		    		JFrame frame = new JFrame();
					JOptionPane.showMessageDialog(frame,
					    "輸入檔案格式有誤",
					    "Error",
					    JOptionPane.ERROR_MESSAGE);
		    	}
		    }
		}
		
		if (e.getSource() == btnStart) {
			
			//未輸入案概率
			if (textCoverage.getText().isEmpty()){
				JFrame frame = new JFrame();
				JOptionPane.showMessageDialog(frame,
				    "請設定涵蓋率",
				    "Warning",
				    JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			coverage = Double.parseDouble(textCoverage.getText()) / 100;
			//涵蓋率範圍0~1
			if (coverage > 0 && coverage < 1) {
				clearAllTable();
				//設定AprioriAlgorithm涵蓋率
				AprioriAlgorithm.get().setMinSupport(coverage);
				//開始執行
				AprioriAlgorithm.get().start(this);
			}
		}
	}
	
	/**
	 * 清除table資料
	 */
	private void clearAllTable() {
		//將資料全部設為空值
		data = new ArrayList<>();
		resultData = new HashMap<Map<String,String>, Double>();		
		ruleData = null;
		
		resultTableModel = new readOnlyTableModel();
		resultTable.setModel(resultTableModel);
		
		ruleTableModel = new readOnlyTableModel();
		ruleTable.setModel(ruleTableModel);
		
		//重建table
		buildResultTable();
		buildRuleTable();
	}

	/**
	 * 建立資料table
	 */
	private void buildDataTable() {
		
		//如果data欄位為空，不做事
		if (dataColumns == null) {
			return;
		}
		
		//清空現有資料
		dataTableModel = new readOnlyTableModel();
		dataTable.setModel(dataTableModel);
		
		//塞欄位
		for (String column : dataColumns) {
			dataTableModel.addColumn(column); 
		}
		
		//塞資料
		for (Map<String, String> map : data) {
			String[] row = new String[map.keySet().size()];
			int index = 0;
			for (String key : dataColumns) {
				row[index] = map.get(key);
				index++;
			}
			dataTableModel.addRow(row);
		}
	}
	
	/**
	 * 建立過程結果table
	 */
	private void buildResultTable() {

		//是否已經建立欄位
		if (resultTableModel.getColumnCount() == 0) {		
			for (String column : resultColumns) {
				resultTableModel.addColumn(column); 
			}
			
			//設定欄位寬度
			resultTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		    resultTable.getColumnModel().getColumn(0).setPreferredWidth(370);
		    resultTable.getColumnModel().getColumn(1).setPreferredWidth(50);
	    }
	   
		//加入資料
		for (Map<String, String> keyMap : resultData.keySet()) {
			String[] row = new String[2];
			String resultText = "";
			for (String key : keyMap.keySet()) {
				resultText += key + " = " + keyMap.get(key) + " , ";
			}
			//去除多餘的逗號
			resultText = resultText.substring(0, resultText.length()-3);
			row[0] = resultText;
			row[1] = String.valueOf(resultData.get(keyMap));
			resultTableModel.addRow(row);
		}
	}
	
	/**
	 * 建立法則table
	 */
	private void buildRuleTable() {

		//是否已經建立欄位
		if (ruleTableModel.getColumnCount() == 0) {		
			for (String column : ruleColumns) {
				ruleTableModel.addColumn(column); 
			}
			
			//設定欄位寬度
			ruleTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			ruleTable.getColumnModel().getColumn(0).setPreferredWidth(320);
			ruleTable.getColumnModel().getColumn(1).setPreferredWidth(50);
			ruleTable.getColumnModel().getColumn(2).setPreferredWidth(50);
	    }
		
		//若法則資料為空，不做事
		if (ruleData == null) {
			return;
		}
		
		//塞資料
		for (AssociationRule a : ruleData) {
			String[] row = new String[3];
			row[0] = a.getRule();
			row[1] = String.valueOf(a.getCoverage());
			row[2] = String.valueOf(a.getAccuracy());
			ruleTableModel.addRow(row);
		}	
	}
	
	/**
	 * 繼承 DefaultTableModel class
	 * 將可修改方法改為false
	 */
	class readOnlyTableModel extends DefaultTableModel {
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	}

	/**
	 * 設定屬性欄位
	 * @param attributes 屬性字串
	 */
	public void setAttribute(List<String> attributes) {
		dataColumns = attributes.toArray(dataColumns);
		buildDataTable();
	}
	
	/**
	 * 顯示屬性詳細資料
	 * @param attribuesMap 屬性名稱對應其範圍之Map
	 */
	public void showAttribute(Map<String, String[]> attribuesMap) {
		String text = "";
		for (String key : attribuesMap.keySet()) {
			text += key + " : \t";
			for (String value : attribuesMap.get(key)) {
				text += value + ", ";
			}
			text.substring(0, text.length()-1);
			text += "\n";
		}
		attributeArea.setText(text);
	}
	
	/**
	 * 設定所有資料
	 * @param list 所有資料的List
	 */
	public void setData(List<Map<String, String>> list) {
		data = list;
		buildDataTable();
	}
	
	/**
	 * 設定過程結果資料
	 * @param data 過程結果配對與涵蓋率的map
	 */
	public void setResultData(Map<Map<String, String>, Double> data) {
		resultData = data;
		buildResultTable();
	}
	
	/**
	 * 設定法則資料
	 * @param data AssociationRule陣列
	 */
	public void setResultData(AssociationRule[] data) {
		ruleData = data;
		buildRuleTable();
	}
	
	
}