import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
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
	private JTextArea attributeArea = new JTextArea();
	private JLabel filePath = new JLabel("選擇檔案");
	private JLabel labelCoverage = new JLabel("涵蓋率(%)：");
	private JTextField textCoverage = new JTextField();
	
	private JButton btnChooseFile = new JButton("選擇檔案");
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
	private String[] ruleColumns = {"法則", "涵蓋率", "正確率"};
	private String[][] resultData = {};
	private double coverage = 0;

	public Gui() {
		super("Apriori Algorithm");

		this.setSize(width, height);
		this.setPreferredSize(new Dimension(width, height));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);          
		this.getContentPane().setBackground(Color.white);
		//this.setAlwaysOnTop(true);
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
		
		JScrollPane scrollPane = new JScrollPane(dataTable);
	    scrollPane.setBounds(30, 300, 450, 200);
	    
	    
	    //buildDataTable();
	    
	    labelCoverage.setBounds(60, 510, 100, 50);
	    textCoverage.setBounds(140, 520, 100, 30);
	    btnStart.setBounds(350, 520, 100, 30);
	    btnStart.setEnabled(false);

	    btnChooseFile.addActionListener(this);
	    btnStart.addActionListener(this);
	    
	    
	    JScrollPane resultPanel = new JScrollPane(resultTable);
	    resultPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	    
	    
		JScrollPane rulePanel = new JScrollPane(ruleTable);
		rulePanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		resultPanel.setBounds(520, 20, 440, 250);
		rulePanel.setBounds(520, 280, 440, 250);
	    
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
		    FileNameExtensionFilter filter = new FileNameExtensionFilter("txt", "txt");
		    chooser.setFileFilter(filter);
		    
		    int returnVal = chooser.showOpenDialog(this);
		    
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		    	String path = chooser.getSelectedFile().getPath();
		    	filePath.setText(path);
		    	boolean isValidFile = FileParser.setPath(path);
		    	if (isValidFile) {
		    		setAttribute(FileParser.getAttributes());
		    		setData(FileParser.getAllData());
		    		btnStart.setEnabled(true);
		    	}
		    }
		    
		}
	}

	
	private void buildDataTable() {
		
		if (dataColumns == null) {
			return;
		}
		
		dataTableModel = new readOnlyTableModel();
		dataTable.setModel(dataTableModel);
		
		for (String column : dataColumns) {
			dataTableModel.addColumn(column); 
		}
		
		
		for (Map<String, String> map : data) {
			String[] row = new String[map.keySet().size()];
			int index = 0;
			for (String key : dataColumns) {
				row[index] = map.get(key);
				System.out.print(row[index]);
				index++;
			}
			dataTableModel.addRow(row);
		}
		
		resultTableModel = new readOnlyTableModel();
		resultTable.setModel(resultTableModel);
		
		for (String column : resultColumns) {
			resultTableModel.addColumn(column); 
		}
		
		resultTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	    resultTable.getColumnModel().getColumn(0).setPreferredWidth(250);
	    resultTable.getColumnModel().getColumn(1).setPreferredWidth(195);
	    
	    ruleTableModel = new readOnlyTableModel();
	    ruleTable.setModel(ruleTableModel);
		
		for (String column : ruleColumns) {
			ruleTableModel.addColumn(column); 
		}
		
		ruleTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		ruleTable.getColumnModel().getColumn(0).setPreferredWidth(250);
		ruleTable.getColumnModel().getColumn(1).setPreferredWidth(95);
		ruleTable.getColumnModel().getColumn(2).setPreferredWidth(95);
	}
	
	class readOnlyTableModel extends DefaultTableModel {
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	}

	/*
	 * 設定屬性
	 */
	public void setAttribute(List<String> attributes) {
		dataColumns = attributes.toArray(dataColumns);
		buildDataTable();
	}
	
	/*
	 * 設定資料 
	 */
	public void setData(List<Map<String, String>> list) {
		data = list;
		buildDataTable();
	}
	
	/*
	 * 設定涵蓋率
	 */
	public void setAccuracy(double coverage) {
		this.coverage = coverage;
		//Todo: 轉給演算法，要弄成0.多
	}
	
}
