package frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import db.CM;

public class PopularHallFrame extends BaseFrame{
	
	JPanel graphPanel[] = new JPanel[3];
	JLabel graphLabel[] = new JLabel[3];
	JPanel blockPanel[] = new JPanel[3];
	JLabel blockLabel[] = new JLabel[3];
	
	JComboBox<String> comboBox = createComponent(new JComboBox<String>(),250,30);
	
	JButton previousBtn = createButton("◀" ,e->btnAct(e.getActionCommand())), nextBtn = createButton("▶", e->btnAct(e.getActionCommand()));
	
	DefaultTableModel model = new DefaultTableModel(null,new Object[] {"이름","주소","홀사용료"});
	JTable table = new JTable(model);
	JScrollPane scrollPane = createComponent(new JScrollPane(table),350,250);
	JPanel scrollPanel = new JPanel();
	
	Color colors[] = {Color.black,Color.blue,Color.red};
	
	ArrayList<Integer> cntList = new ArrayList<Integer>();
	ArrayList<String> nameList = new ArrayList<String>();
	
	int max;
	int index = 0;
	int x[] = {25,95,165};
	
	public PopularHallFrame() {
		super("인기 웨딩홀", 400,400);
		
		JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		northPanel.add(comboBox);
		comboBox.addItem("인기 웨딩 종류");
		comboBox.addItem("인기 식사 종류");
		comboBox.addActionListener(e->comboBoxAct());
		JPanel centerPaenl = new JPanel(null);
		
		int h = 10;
		for (int i = 0; i < blockLabel.length; i++) {
			int j=i;
			graphPanel[i] = createComponent(new JPanel(), i, i);
			graphPanel[i].setBackground(colors[i]);
			graphPanel[i].setBorder(new LineBorder(Color.black));
			graphLabel[i] = createComponent(new JLabel(), x[i],260,65,20);
			blockPanel[i] = createComponent(new JPanel(), 250,120+h,10,10);
			blockPanel[i].setBackground(colors[i]);
			blockPanel[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					blockPanelAct(j);
				}
			});
			blockLabel[i] = createComponent(new JLabel(), 270,120+h,100,12);
			centerPaenl.add(graphPanel[i]);
			centerPaenl.add(graphLabel[i]);
			centerPaenl.add(blockPanel[i]);
			centerPaenl.add(blockLabel[i]);
			h+=20;
		}
		
		JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		southPanel.add(previousBtn);
		southPanel.add(nextBtn);
		
		scrollPanel.add(scrollPane);
		scrollPanel.setBorder(BorderFactory.createEmptyBorder(0,0,20,30));
		
		add(northPanel,BorderLayout.NORTH);
		add(centerPaenl,BorderLayout.CENTER);
		add(southPanel,BorderLayout.SOUTH);
		
		comboBoxAct();
	}
	
	private void comboBoxAct() {
		index = 0;
		cntList.clear();
		nameList.clear();
		
		CM cm = new CM();
		cm.connect();
		ResultSet rs = null;
		if(comboBox.getSelectedIndex() == 0) {
			rs = cm.executeQuery("select count(*) as count,wty_Name as name from payment as p "
					+ "inner join weddingtype as wty on p.wty_No = wty.wty_No group by wty.wty_No order by count desc;");
			previousBtn.setEnabled(false);
			nextBtn.setEnabled(true);
		}else if(comboBox.getSelectedIndex() == 1) {
			rs = cm.executeQuery("select count(*) as count,m_Name as name from payment as p "
					+ "inner join mealtype as m on m.m_No = p.m_no group by m.m_no order by count desc;");
			previousBtn.setEnabled(false);
			nextBtn.setEnabled(false);
		}
		try {
			while(rs.next()) {
				cntList.add(rs.getInt("count"));
				nameList.add(rs.getString("name"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		cm.close();
		max = cntList.get(0);
		for(int i=0;i<graphPanel.length;i++) {
			moveGraph(graphPanel[i], i).start();
		}
		change();
	}
	
	void setLabels() {
		for(int i=0;i<3;i++) {
			graphLabel[i].setText(nameList.get(i+index));
			blockLabel[i].setText(nameList.get(i+index)+":"+cntList.get(i+index)+"개");
		}
	}
	
	private void blockPanelAct(int i) {
		 model.setNumRows(0);
		 setSize(800,400);
		 add(scrollPanel,BorderLayout.EAST);
		 ResultSet rs = null;
		 CM cm = new CM();
		 cm.connect();
		 if(comboBox.getSelectedIndex() == 0) {
			 rs = cm.executeQuery("select * from weddinghall as wh inner join division as d on d.wh_No = wh.wh_no "
			 		+ "inner join weddingtype as wty on d.wty_no = wty.wty_no where wty.wty_name = ?",nameList.get(i+index));
		 }else if(comboBox.getSelectedIndex() == 1) {
			 rs = cm.executeQuery("select * from weddinghall as wh inner join division as d on d.wh_no = wh.wh_no "
			 		+ "inner join mealtype as m on m.m_no = d.m_no where m.m_name = ?", nameList.get(i+index));
		 }
		 
		 try {
			while(rs.next()) {
				model.addRow(new Object[] {rs.getString("wh_name"),rs.getString("wh_add"),format(rs.getInt("wh_price"))+"원"});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		 setGraphColor();
		 graphPanel[i].setBackground(Color.magenta);
		 cm.close();
	}
	
	private void setGraphColor() {
		for(int i = 0;i<graphPanel.length;i++) {
			if(graphPanel[i].getBackground().equals(Color.magenta)) {
				graphPanel[i].setBackground(colors[i]);
			}
		}
	}
	
	Thread moveGraph(JPanel panel,int i) {
		return new Thread(()->{
			try {
				int height = (int)(((float)cntList.get(i+index) / max) * 250);
				for(int j=0;j<height;j++) {
					panel.setBounds(x[i],250-height,40,j);
					Thread.sleep(1000/height);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
	
	private void btnAct(String ac) {
		if(ac.equals("◀")) {
			index--;
			if(index == 0)
				previousBtn.setEnabled(false);
			nextBtn.setEnabled(true);
		}else {
			index++;
			if(index == 7) 
				nextBtn.setEnabled(false);
			previousBtn.setEnabled(true);
		}
		change();
		for (int i = 0; i < graphPanel.length; i++) {
			int height = (int)(((float)cntList.get(i+index) / max) * 250);
			graphPanel[i].setBounds(x[i],250-height,40,height);
		}
	}
	
	void change() {
		setSize(400,400);
		remove(scrollPanel);
		setGraphColor();
		setLabels();
	}
	
	public static void main(String[] args) {
		new PopularHallFrame().setVisible(true);
	}
	
	@Override
	public void closedAct() {
		previousFrame();
	}
}
