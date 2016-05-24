import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;


public class Gui extends JFrame{

	private static final long serialVersionUID = 1L;
	
	private int height = 600;
	private int width = 1000;
	
	/* gui 元件 */
	private JFileChooser fileChooser = new JFileChooser();
	private JTextArea attributeArea = new JTextArea();
	private JTextArea filePath = new JTextArea("選擇檔案");
	
	private JButton btnChooseFile = new JButton("選擇檔案");
	
	private DefaultTableModel dataTableModel = new DefaultTableModel(); 
	private JTable dataTable = new JTable(dataTableModel);
	
	/* 資料 */
	private String[] columns = {"a1", "a2", "a2", "a2", "a2", "a2"};
	private String[][] data = {{"0ssa","0sa","0dasd","0sda","0sda","0sda"},{"0","1","0","0","0","0"}};
	private double coverage = 0;

	public Gui() {
		super("Apriori Algorithm");

		this.setSize(width, height);
		this.setPreferredSize(new Dimension(width, height));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);             
		this.setAlwaysOnTop(true);
		this.setResizable(false);
		this.setLayout(null);
		
		filePath.setBounds(30, 5, 150, 20);
		
		attributeArea.setOpaque(true);
		attributeArea.setBounds(30, 70, 350, 200);
		attributeArea.setBackground(new Color(255, 255, 158));
		attributeArea.setFont(new Font("Serif", 0, 14));
		attributeArea.setEditable(false); 
		Border border = BorderFactory.createDashedBorder(Color.BLACK, 5, 2);
		attributeArea.setBorder(BorderFactory.createCompoundBorder(border, 
		            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		
		JScrollPane scrollPane = new JScrollPane(dataTable);
	    scrollPane.setBounds(30, 300, 350, 200);
	    
	    buildTable();

	    add(filePath);
		add(attributeArea);
		add(scrollPane);
		
		this.pack();
		this.setVisible(true);
	}
	
	private void buildTable() {
		
		if (columns == null) {
			return;
		}
		
		dataTableModel = new DefaultTableModel();
		dataTable.setModel(dataTableModel);
		
		for (String column : columns) {
			dataTableModel.addColumn(column); 
		}
		
		for (String[] d : data) {
			dataTableModel.addRow(d);
		}
	}
	
	/*
	 * 設定屬性
	 */
	public void setAttribute(String[] attributes) {
		columns = attributes;
		buildTable();
	}
	
	/*
	 * 設定資料 
	 */
	public void setTestSet(String[][] data) {
		this.data = data;
		buildTable();
	}
	
	/*
	 * 設定涵蓋率
	 */
	public void setAccuracy(double coverage) {
		this.coverage = coverage;
		
		
	}
	
	
}
