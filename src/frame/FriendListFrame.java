package frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class FriendListFrame extends BaseFrame{
	JTextField nameField = new JTextField(15);
	JCheckBox cbAll = new JCheckBox("전체 선택");
	JScrollPane scrollPane;
		
	DefaultTableModel model = new DefaultTableModel(null," ,이름,생년월일,전화번호".split(",")) {
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return columnIndex == 0 ? Boolean.class : String.class;
		}
	};
	JTable table = new JTable(model) {
        public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
            JComponent comp = (JComponent) super.prepareRenderer(renderer, row, column);
            if (row == table.getSelectedRow()) {
                comp.setBackground(Color.yellow);
            } else
                comp.setBackground(Color.white);
            return comp;
        };
	};
	
	public FriendListFrame() {
		super("친구목록", 600, 500);
		
		table.getColumn("이름").setPreferredWidth(200);
		table.getColumn("생년월일").setPreferredWidth(200);
		table.getColumn("전화번호").setPreferredWidth(350);
		
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setHorizontalAlignment(SwingConstants.CENTER);
		
		setTable("select * from user;");
		
		for(int i=1;i<table.getColumnCount();i++) {
			table.getColumnModel().getColumn(i).setCellRenderer(renderer);
		}
		scrollPane = new JScrollPane(table);
		scrollPane.setBorder(BorderFactory.createLineBorder(Color.black));
		scrollPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5,5,5,5), BorderFactory.createLineBorder(Color.black))); 
										//createCompoundBorder 안쪽 Border와 바깥쪽 Border 따로 지정 가능
		table.setAutoCreateRowSorter(true);
		
		cbAll.addActionListener(e->cbAct());
		cbAll.setBorder(BorderFactory.createEmptyBorder(0,160,0,0));
		
		JPanel northPanel = new JPanel(new FlowLayout());
		northPanel.add(createLabel("이름"));
		northPanel.add(nameField);
		northPanel.add(createButton("검색", e->searchBtnAct()));
		northPanel.add(cbAll);
		northPanel.add(createButton("초기화", e->resetBtnAct()));
		
		JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		southPanel.add(createButton("보내기", e->openFrame(new MainFrame())));
		
		add(northPanel,BorderLayout.NORTH);
		add(scrollPane,BorderLayout.CENTER);
		add(southPanel,BorderLayout.SOUTH);
		
	}
	
	private void searchBtnAct() {
		setTable("select * from user where u_Name like concat('%',?,'%')",nameField.getText());
	}
	
	
	private void cbAct() {
		if(cbAll.isSelected()) {
			for(int i=0;i<model.getRowCount();i++) {
				table.setValueAt(true, i, 0);
			}
		}else {
			for(int i=0;i<model.getRowCount();i++) {
				table.setValueAt(false, i, 0);
			}
		}
	}
	
	private void resetBtnAct() {
		setTable("select * from user");
		nameField.setText("");
		cbAll.setSelected(false);
	}
	
	
	private void setTable(String sql,Object...obj) {
		model.setNumRows(0);
		try (PreparedStatement pst = conn.prepareStatement(sql)){
			for(int i=0;i<obj.length;i++)
				pst.setObject(i+1, obj[i]);
			ResultSet rs = pst.executeQuery();
			while(rs.next()) {
				if(rs.getInt("u_No") == user_No)
					continue;
				model.addRow(new Object[] {false,rs.getString("u_Name"),rs.getString("u_Date"),rs.getString("u_Phone")});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public static void main(String[] args) {
		new FriendListFrame().setVisible(true);
	}
}
