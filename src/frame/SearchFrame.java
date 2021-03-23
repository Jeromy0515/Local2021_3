package frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import model.UserInform;

public class SearchFrame extends BaseFrame {
	private JTextField minOfwhPeopleField, maxOfwhPeopleField, minOfwhPriceField, maxOfwhPriceField;
	private int panelCnt = 0;
	private JPanel contentsPanel;
	private JPanel whInformPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
	JCheckBox[] cbRegion = new JCheckBox[11];
	JCheckBox[] cbWhType = new JCheckBox[10];
	JCheckBox[] cbMealType = new JCheckBox[3];

	public SearchFrame() {
		super("검색", 825, 600);
		JPanel[] cbPanels = new JPanel[3];
		for (int i = 0; i < cbPanels.length; i++) {
			cbPanels[i] = new JPanel(new GridLayout(0, 6, 0, -12));
		}

		String[] regionName = "강남구,영등포구,구로구,서초구,송파구,중구,용산구,노원구,관악구,종로구,서대문구".split(",");
		String[] whTypeName = "일반웨딩홀,강당,하우스,호텔웨딩홀,야외예식,컨벤션,레스토랑,회관,성당,교회".split(",");
		String[] mealTypeName = "양식,뷔페,한식".split(",");

		for (int i = 0; i < cbRegion.length; i++) {
			cbRegion[i] = new JCheckBox(regionName[i]);
			cbPanels[0].add(cbRegion[i]);
		}

		for (int i = 0; i < cbWhType.length; i++) {
			cbWhType[i] = new JCheckBox(whTypeName[i]);
			cbPanels[1].add(cbWhType[i]);
		}

		for (int i = 0; i < cbMealType.length; i++) {
			cbMealType[i] = new JCheckBox(mealTypeName[i]);
			cbPanels[2].add(cbMealType[i]);
		}

		JPanel cbPanel = new JPanel(new GridLayout(0, 2, -630, 0));
		cbPanel.add(createLabel("지역"));
		cbPanel.add(cbPanels[0]);
		cbPanel.add(createLabel("예식형태"));
		cbPanel.add(cbPanels[1]);
		cbPanel.add(createLabel("식사종류"));
		cbPanel.add(cbPanels[2]);
		cbPanel.setBounds(10, 5, 750, 200);

		minOfwhPeopleField = new JTextField(12);
		maxOfwhPeopleField = new JTextField(12);
		minOfwhPriceField = new JTextField(12);
		maxOfwhPriceField = new JTextField(12);

		JPanel fieldPanel = new JPanel(new FlowLayout());
		fieldPanel.add(createLabel("수용인원"));
		fieldPanel.add(minOfwhPeopleField);
		fieldPanel.add(createLabel("~"));
		fieldPanel.add(maxOfwhPeopleField);
		fieldPanel.add(createLabel("홀사용료"));
		fieldPanel.add(minOfwhPriceField);
		fieldPanel.add(createLabel("~"));
		fieldPanel.add(maxOfwhPriceField);
		fieldPanel.setBounds(5, 200, 400, 50);

		JPanel btnPanel = new JPanel(new FlowLayout());
		btnPanel.add(createButton("검색", e -> searchBtnAct()));
		btnPanel.add(createButton("초기화", e -> resetBtnAct()));
		btnPanel.setBounds(610, 216, 200, 40);

		createWhPanel(
				"select wh_Name,wh_Add,wty_Name,m_Name,wh_People,wh_Price\r\n"
				+ "from weddinghall as wh\r\n"
				+ "left join division as d on d.wh_No = wh.wh_No\r\n"
				+ "left join mealtype as m on d.m_No = m.m_No\r\n"
				+ "left join weddingtype as wty on d.wty_No = wty.wty_No;");
		whInformPanel.setBounds(5, 260, 780, 100 * panelCnt);
		contentsPanel = createComponent(new JPanel(null), 750, 260 + (100 * panelCnt));
		contentsPanel.add(cbPanel);
		contentsPanel.add(fieldPanel);
		contentsPanel.add(btnPanel);
		contentsPanel.add(whInformPanel);

		add(new JScrollPane(contentsPanel), BorderLayout.CENTER);

	}

	private class WhPanel extends JPanel {
		public WhPanel(String whName, String address, String whType, String mealType, int capacity, int hallPrice) {
			setLayout(new BorderLayout());
			JLabel image = new JLabel(getImage(150, 90, whName));
			image.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

			setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));
			JLabel whNameLabel = new JLabel(whName);

			JPanel labelPanel = new JPanel(new GridLayout(0, 1, 3, 5));
			labelPanel.add(whNameLabel);
			labelPanel.add(createLabel("주소: " + address + "/"));
			labelPanel.add(createLabel("예식형태: " + whType + "/식사종류: " + mealType + "/"));
			labelPanel.add(
					createLabel("수용인원: " + capacity + "/홀사용료: " + String.format("%,d",hallPrice) + "원"));
			labelPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

			ImageIcon imageIcon = getImage(380, 200, whName);
			JButton chooseBtn = createComponent(
					createButtonWithoutMargin("선택",
							e -> openFrame(new WeddingHallFrame(new UserInform(whName, address, capacity, hallPrice, whType, mealType, 0, 0, "")))),
					70, 80);
			
			JPanel btnPanel = createComponentWithBorder(new JPanel(new BorderLayout()), 70, 100,
					BorderFactory.createEmptyBorder(5, 5, 5, 5));
			btnPanel.add(chooseBtn);

			add(image, BorderLayout.WEST);
			add(labelPanel, BorderLayout.CENTER);
			add(btnPanel, BorderLayout.EAST);

		}
	}

	private void createWhPanel(String sql) {
		panelCnt = 0;
		try (PreparedStatement pst = conn.prepareStatement(sql)) {
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				JPanel whPanel = createComponent(
						new WhPanel(rs.getString("wh_Name"), rs.getString("wh_Add"), rs.getString("wty_Name"),
								rs.getString("m_Name"), rs.getInt("wh_People"), rs.getInt("wh_Price")),
						780, 100);
				whInformPanel.add(whPanel);
				panelCnt++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void searchBtnAct() {

		StringBuilder sql = new StringBuilder(
				"select wh_Name,wh_Add,wty_Name,m_Name,wh_People,wh_Price\r\n"
				+ "from weddinghall as wh\r\n"
				+ "left join division as d on d.wh_No = wh.wh_No\r\n"
				+ "left join mealtype as m on d.m_No = m.m_No\r\n"
				+ "left join weddingtype as wty on d.wty_No = wty.wty_No\r\n" + "where (");
		ArrayList<String> regionSelectionList = new ArrayList<String>();
		ArrayList<String> wtySelectionList = new ArrayList<String>();
		ArrayList<String> mtySelectionList = new ArrayList<String>();

		for (int i = 0; i < cbRegion.length; i++) {
			if (cbRegion[i].isSelected())
				regionSelectionList.add(cbRegion[i].getText());
		}
		for (int i = 0; i < cbWhType.length; i++) {
			if (cbWhType[i].isSelected())
				wtySelectionList.add(cbWhType[i].getText());
		}
		for (int i = 0; i < cbMealType.length; i++) {
			if (cbMealType[i].isSelected())
				mtySelectionList.add(cbMealType[i].getText());
		}

		appendSql(sql, regionSelectionList, "wh_Add");

		if (wtySelectionList.size() != 0) {
			if (regionSelectionList.size() != 0)
				sql.append(" and (");
			appendSql(sql, wtySelectionList, "wty_Name");
		}

		if (mtySelectionList.size() != 0) {
			if (!isEmpty(regionSelectionList, wtySelectionList))
				sql.append(" and (");
			appendSql(sql, mtySelectionList, "m_Name");
		}

		int minPeople = isNumeric(minOfwhPeopleField.getText());
		int maxPeople = isNumeric(maxOfwhPeopleField.getText());
		int minPrice = isNumeric(minOfwhPriceField.getText());
		int maxPrice = isNumeric(maxOfwhPriceField.getText());

		if (minPeople == FIELD_EMPTY || maxPeople == FIELD_EMPTY || minPrice == FIELD_EMPTY
				|| maxPrice == FIELD_EMPTY) {
			errorMessage("수용인원과 홀사용료는 숫자만 입력 가능합니다.");
			return;
		}

		if ((minPeople > maxPeople && maxPeople != 0) || (minPrice > maxPrice && maxPrice != 0)) {
			errorMessage("숫자를 올바르게 입력해주세요.");
			return;
		}

//		minPeople = sql.append("wh_People between ");

		if (minPeople != 0 || maxPeople != 0) {
			if (!isEmpty(regionSelectionList, wtySelectionList, mtySelectionList))
				sql.append(" and ( ");

			if (minPeople != 0 && maxPeople == 0)
				sql.append("wh_People > " + minPeople + ")");
			else if (minPeople == 0 && maxPeople != 0)
				sql.append("wh_People < " + maxPeople + ")");
			else if (minPeople != 0 && maxPeople != 0)
				sql.append("wh_People > " + minPeople + " and wh_People < " + maxPeople + ")");
		}

		if (minPrice != 0 && maxPrice != 0) {
			if (!isEmpty(regionSelectionList, wtySelectionList, mtySelectionList) || (minPeople != 0 || maxPeople != 0))
				sql.append(" and ( ");

			if (minPrice != 0 && maxPrice == 0)
				sql.append("wh_Price > " + minPrice + ")");
			else if (minPrice == 0 && maxPrice != 0)
				sql.append("wh_Price < " + maxPrice + ")");
			else if (minPrice != 0 && maxPrice != 0)
				sql.append("wh_Price > " + minPrice + " and wh_Price < " + maxPrice + ")");
		}

		System.out.println(sql.toString());
		whInformPanel.removeAll();

		try {
			createWhPanel(sql.toString());
			contentsPanel.setPreferredSize(new Dimension(750, 260 + (106 * panelCnt)));
			contentsPanel.revalidate();
		} catch (Exception e) {
			e.printStackTrace();
			whInformPanel.removeAll();
			resetBtnAct();
		}

	}

	private void resetBtnAct() {
		resetCbSelected(cbRegion);
		resetCbSelected(cbWhType);
		resetCbSelected(cbMealType);

		minOfwhPeopleField.setText("");
		maxOfwhPeopleField.setText("");
		minOfwhPriceField.setText("");
		maxOfwhPriceField.setText("");

		whInformPanel.removeAll();

		createWhPanel("select wh_Name,wh_Add,wty_Name,m_Name,wh_People,wh_Price\r\n" + "from weddinghall as wh\r\n"
				+ "left join division as d on d.wh_No = wh.wh_No\r\n" + "left join mealtype as m on d.m_No = m.m_No\r\n"
				+ "left join weddingtype as wty on d.wty_No = wty.wty_No;");
		contentsPanel.setPreferredSize(new Dimension(750, 260 + (100 * panelCnt)));
		contentsPanel.revalidate();

	}

	private void resetCbSelected(JCheckBox[] cb) {
		for (int i = 0; i < cb.length; i++) {
			if (cb[i].isSelected())
				cb[i].setSelected(false);
		}
	}

	private boolean isEmpty(ArrayList<String>... lists) {
		for (int i = 0; i < lists.length; i++) {
			if (lists[i].size() != 0)
				return false;
		}
		return true;
	}

	private int isNumeric(String num) {
		int result;
		try {
			if (num.equals(""))
				return 0;
			result = Integer.parseInt(num);
		} catch (Exception e) {
			return -1;
		}
		return result;
	}

	private void appendSql(StringBuilder sql, ArrayList<String> list, String col) {
		for (int i = 0; i < list.size(); i++) {
			sql.append(col + " like '%" + list.get(i) + "%'");
			if (i == list.size() - 1)
				sql.append(")");
			else
				sql.append(" or ");
		}
	}

	public static void main(String[] args) {
		new SearchFrame().setVisible(true);
	}
}
