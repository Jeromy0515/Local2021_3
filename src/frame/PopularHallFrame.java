package frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class PopularHallFrame extends BaseFrame {
	JPanel graphPanels[] = new JPanel[3];
	JLabel graphLabels[] = new JLabel[3];
	JPanel blockPanels[] = new JPanel[3];
	JLabel blockLabels[] = new JLabel[3];

	Color colors[] = { Color.black, Color.blue, Color.red };

	JButton previousBtn = createButton("◀", e -> previousBtnAct());
	JButton nextBtn = createButton("▶", e -> nextBtnAct());

	ArrayList<Integer> cntList = new ArrayList<Integer>();
	ArrayList<String> nameList = new ArrayList<String>();
	JComboBox<String> comboBox = createComponent(new JComboBox<String>(), 250, 30);

	JPanel centerPanel;

	int x[] = { 25, 95, 165 }, y = 5; // x,width 고정
	int width = 40, height; // y,height 변동
	int maxHeight = 250;

	int index = 0;
	int max;

	DefaultTableModel model = new DefaultTableModel(null, new Object[] { "이름", "주소", "홀사용료" });
	JTable table = new JTable(model);
	JScrollPane scrollPane = new JScrollPane(table);
	JPanel scrollPanel;

	public PopularHallFrame() {
		super("인기 웨딩홀", 400, 400);

		JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		centerPanel = new JPanel(null);
		JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

		comboBox.addItem("인기 웨딩 종류");
		comboBox.addItem("인기 식사 종류");
		comboBoxAct(centerPanel);
		max = cntList.get(0);

		scrollPanel = new JPanel();
		scrollPanel.add(scrollPane);
		scrollPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 30));
		scrollPane.setPreferredSize(new Dimension(350, 250));

		int h = 10;
		for (int i = 0; i < graphPanels.length; i++) {
			graphPanels[i] = new JPanel();
			graphPanels[i].setBackground(colors[i]);
			graphPanels[i].setBorder(BorderFactory.createLineBorder(Color.black));
			graphLabels[i] = new JLabel(nameList.get(i + index));
			graphLabels[i].setBounds(x[i], 260, 65, 20);
			moveGraph(graphPanels[i], (int) (((float) cntList.get(i + index) / max) * maxHeight), i).start();
			centerPanel.add(graphPanels[i]);
			centerPanel.add(graphLabels[i]);
			blockPanels[i] = new JPanel();
			blockLabels[i] = new JLabel(nameList.get(i + index) + ":" + cntList.get(i + index) + "개");
			blockPanels[i].setBackground(colors[i]);
			int id = i + index;
			int i2 = i;
			blockPanels[i].addMouseListener(new MouseAdapter() {
				public void mouseClicked(java.awt.event.MouseEvent e) {
					if (SwingUtilities.isLeftMouseButton(e)) {
						model.setNumRows(0);
						if (comboBox.getSelectedIndex() == 0) {
							try (PreparedStatement pst = conn
									.prepareStatement("select wh_Name,wh_Add,wh_Price from weddinghall as wh \r\n"
											+ "inner join division as d \r\n" 
											+ "on d.wh_No = wh.wh_No \r\n"
											+ "inner join weddingtype as wty \r\n"
											+ "on d.wty_No = wty.wty_No\r\n"
											+ "where wty.wty_Name like concat('%',?,'%');")) {
								pst.setObject(1, nameList.get(id));
								ResultSet rs = pst.executeQuery();
								while (rs.next()) {
									model.addRow(new Object[] { rs.getString("wh_Name"), rs.getString("wh_Add"),
											rs.getString("wh_Price") });
								}
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						} else {
							try (PreparedStatement pst = conn
									.prepareStatement("select wh_Name,wh_Add,wh_Price from weddinghall as wh\r\n"
											+ "inner join division as d\r\n"
											+ "on d.wh_No = wh.wh_No\r\n"
											+ "inner join mealtype as m\r\n"
											+ "on d.m_No = m.m_No\r\n"
											+ "where m_Name like concat('%',?,'%');")) {
								pst.setObject(1, nameList.get(id));
								ResultSet rs = pst.executeQuery();
								while (rs.next()) {
									model.addRow(new Object[] { rs.getString("wh_Name"), rs.getString("wh_Add"),
											rs.getString("wh_Price") });
								}
							} catch (Exception e2) {
								e2.printStackTrace();
							}
						}
						for (int j = 0; j < 3; j++) {
							if (graphPanels[j].getBackground().equals(Color.magenta))
								graphPanels[j].setBackground(colors[j]);
							graphPanels[i2].setBackground(Color.magenta);
						}
						setSize(810, 400);

						add(scrollPanel, BorderLayout.EAST);
					}
				};

			});

			blockPanels[i].setBounds(250, 120 + h, 10, 10);
			blockLabels[i].setBounds(270, 120 + h, 100, 12);
			centerPanel.add(blockPanels[i]);
			centerPanel.add(blockLabels[i]);
			h += 20;
		}

		comboBox.addItemListener(e -> {
			comboBoxAct(centerPanel);
//			revalipaint();
			for (int j = 0; j < 3; j++) {
				moveGraph(graphPanels[j], (int) (((float) cntList.get(j + index) / max) * maxHeight), j).start();
				graphPanels[j].setBackground(colors[j]);
				graphLabels[j].setText(nameList.get(j + index));
				blockLabels[j].setText(nameList.get(j + index));
			}
			setSize(400, 400);
			remove(scrollPanel);
		});
		northPanel.add(comboBox);

		previousBtn.setEnabled(false);
		southPanel.add(previousBtn);
		southPanel.add(nextBtn);

		add(northPanel, BorderLayout.NORTH);
		add(centerPanel, BorderLayout.CENTER);
		add(southPanel, BorderLayout.SOUTH);
	}

	private void previousBtnAct() {
		index--;
		if (index == 0)
			previousBtn.setEnabled(false);

		revalipaint();
		setSize(400, 400);
		remove(scrollPanel);
		nextBtn.setEnabled(true);
	}

	private void revalipaint() {
		for (int j = 0; j < 3; j++) {
			int height = (int) (((float) cntList.get(j + index) / max) * maxHeight);
			graphPanels[j].setBounds(x[j], maxHeight - height, width, height);
			graphPanels[j].setBackground(colors[j]);
			graphLabels[j].setText(nameList.get(j + index));
			blockLabels[j].setText(nameList.get(j + index));
		}
	}

	private void nextBtnAct() {
		index++;
		if (index == 7)
			nextBtn.setEnabled(false);

		revalipaint();
		setSize(400, 400);
		remove(scrollPanel);
		previousBtn.setEnabled(true);
	}

	private void comboBoxAct(JPanel panel) {
		cntList.clear();
		nameList.clear();
		index = 0;
		String sql;
		if (comboBox.getSelectedItem().equals("인기 웨딩 종류")) {
			sql = "select count(*) as count,wty.wty_Name as name \r\n"
					+ "from payment as p \r\n"
					+ "inner join weddingtype as wty \r\n"
					+ "on p.wty_No = wty.wty_No \r\n"
					+ "group by wty.wty_No \r\n"
					+ "order by count desc;";
			nextBtn.setEnabled(true);
		} else {
			sql = "select m.m_Name as name,count(*) as count \r\n"
					+ "from payment as p \r\n"
					+ "inner join mealtype as m \r\n"
					+ "on p.m_No = m.m_No \r\n"
					+ "group by m.m_No \r\n"
					+ "order by count desc;";
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
		max = cntList.get(0);
	}

	private Thread moveGraph(JPanel panel, int height, int i) {
		return new Thread(() -> {
			try {
				for (int j = 1; j < height; j++) {
					panel.setBounds(x[i], maxHeight - height, width, j);
					Thread.sleep(1000 / height);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public static void main(String[] args) {
		new PopularHallFrame().setVisible(true);
	}

}
