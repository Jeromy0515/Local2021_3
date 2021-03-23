package frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import frame.PopularHallFrame.GraphPanel;

public class PopularHallFrame extends BaseFrame {

	ArrayList<Integer> cntList = new ArrayList<Integer>();
	ArrayList<String> nameList = new ArrayList<String>();
	
	JComboBox<String> comboBox = createComponent(new JComboBox<String>(), 250, 30);
	Color colors[] = {Color.black,Color.blue,Color.red};
	
	DefaultTableModel model = new DefaultTableModel(null,new Object[] {"이름","주소","홀사용료"});
	JTable table = new JTable(model);
	JScrollPane scrollPane = new JScrollPane(table);
	
	JButton previousBtn = createButton("◀", e->previousBtnAct());
	JButton nextBtn = createButton("▶", e->nextBtnAct());
	
	JPanel graphPanel = new JPanel(new GridLayout(0,3,-200,0));
	
	int index = 0;
	
	boolean thMove = false;

	public PopularHallFrame() {
		super("인기 웨딩홀", 400, 400);
		comboBox.addItem("인기 웨딩 종류");
		comboBox.addItem("인기 식사 종류");
		comboBoxAct();
		comboBox.addItemListener((e) -> comboBoxAct());

		JPanel blockPanel = new JPanel(null);
		
		int h = 10;
		
		JPanel scrollPanel = new JPanel();
		scrollPanel.add(scrollPane);
		scrollPanel.setBorder(BorderFactory.createEmptyBorder(0,20,0,30));
		
		scrollPane.setPreferredSize(new Dimension(350,270));
		JPanel blockPanels[] = new JPanel[3];
		for(int i=0;i<blockPanels.length;i++) {
			blockPanels[i] = new JPanel();
			blockPanels[i].setBackground(colors[i]);
			blockPanels[i].addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if(SwingUtilities.isLeftMouseButton(e)) {
						model.setNumRows(0);
						try (PreparedStatement pst = conn.prepareStatement(
								"select wh_Name,wh_Add,wh_Price from weddinghall as wh \r\n"
								+ "inner join division as d \r\n"
								+ "on d.wh_No = wh.wh_No \r\n"
								+ "inner join weddingtype as wty \r\n"
								+ "on d.wty_No = wty.wty_No\r\n"
								+ "where wty.wty_Name like concat('%',?,'%');")){
							pst.setObject(1, nameList.get(index));
							ResultSet rs = pst.executeQuery();
							while(rs.next()) {
								model.addRow(new Object[] {rs.getString("wh_Name"),rs.getString("wh_Add"),rs.getString("wh_Price")});
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						}
						setSize(810,400);
						add(scrollPanel,BorderLayout.EAST);
					}
				}
			});
			blockPanels[i].setBounds(50,120+h,10,10);
			blockPanel.add(blockPanels[i]);
			JLabel label = createLabel(nameList.get(i)+":"+cntList.get(i)+"개");
			label.setBounds(70, 120+h, 100, 10);
			blockPanel.add(label);
			h+=20;
			index++;
		}
		index = 0;
		blockPanel.setPreferredSize(new Dimension(180,200));
		
		JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		northPanel.add(comboBox);

		JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		southPanel.add(previousBtn);
		southPanel.add(nextBtn);
		previousBtn.setEnabled(false);
		
		GraphPanel graphPanels[] = new GraphPanel[3];
		for(int i=0;i<graphPanels.length;i++) {
			graphPanels[i] = new GraphPanel(i);
			graphPanel.add(graphPanels[i]);
		}
		
		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.add(graphPanel,BorderLayout.CENTER);
		centerPanel.add(blockPanel,BorderLayout.EAST);
		
		add(northPanel, BorderLayout.NORTH);
		add(centerPanel,BorderLayout.CENTER);
		add(southPanel, BorderLayout.SOUTH);
	}

	private void comboBoxAct() {
		cntList.clear();
		nameList.clear();
		
		String sql;
		if (comboBox.getSelectedItem().equals("인기 웨딩 종류")) {
			sql = "select count(*) as count,wty.wty_Name as name \r\n"
					+ "from payment as p \r\n"
					+ "inner join weddingtype as wty \r\n"
					+ "on p.wty_No = wty.wty_No \r\n"
					+ "group by wty.wty_No \r\n"
					+ "order by count desc;";
			nextBtn.setEnabled(true);
		}else {
			sql = "select m.m_Name as name,count(*) as count \r\n"
					+ "from payment as p \r\n"
					+ "inner join mealtype as m \r\n"
					+ "on p.m_No = m.m_No \r\n"
					+ "group by m.m_No \r\n"
					+ "order by count asc;";
			nextBtn.setEnabled(false);
			previousBtn.setEnabled(false);
		}

		try (PreparedStatement pst = conn.prepareStatement(sql)) {
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				cntList.add(rs.getInt("count"));
				nameList.add(rs.getString("name"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		thMove = true;
		graphPanel.paint(getGraphics());	
	}
	
	private Thread moveGraph(JPanel panel,int height,int i) {
		return new Thread(()->{
			Graphics g = panel.getGraphics();
			try {
				g.setColor(colors[i]);
				for(int j=height;j>=1;j--) {
					g.fillRect(30 + (i * 65), 270-j, 40, 1);
					Thread.sleep(1000/height);
				}
				g.setColor(Color.black);
				g.drawRect(30 + (i * 65), 270-height, 40, height);
				thMove = false;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}); 
	}
	
	private Thread drawGraph(JPanel panel,int height,int i) {
		return new Thread(()->{
			Graphics g = panel.getGraphics();
			try {
				g.setColor(colors[i]);
				g.fillRect(30 + (i * 65), 270-height, 40, height);
				g.setColor(Color.black);
				g.drawRect(30 + (i * 65), 270-height, 40, height);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
	
	private void nextBtnAct() {
		if(index == 6)
			nextBtn.setEnabled(false);
			
		previousBtn.setEnabled(true);
		index++;
		graphPanel.repaint();
	}
	
	private void previousBtnAct() {
		if(index == 1)
			previousBtn.setEnabled(false);
		
		nextBtn.setEnabled(true);
		index--;
		graphPanel.repaint();
	}
	
	
	class GraphPanel extends JPanel{
		int height;
		int i;
		int max = cntList.get(0);
		@Override
		public void paint(Graphics g) {
			g.dispose();
			this.height = (int) (((float) cntList.get(i+index) / max) * 280);
			if(thMove)
				moveGraph(this,height, i).start();
			else
				drawGraph(this,height,i).start();
		}
		
		public GraphPanel(int i) {
			this.i = i;
		}
	}

	public static void main(String[] args) {
		new PopularHallFrame().setVisible(true);
	}

}
