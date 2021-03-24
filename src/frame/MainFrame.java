package frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class MainFrame extends BaseFrame {

	public MainFrame() {
		super("메인", 900, 600);

		setLayout(null);

		JLabel mainLabel = createLabel("결혼식장 예약프로그램", new Font("굴림", Font.BOLD, 30),JLabel.CENTER);
		mainLabel.setBounds(250, 0, 350, 40);

		JPanel btnPanel = new JPanel(new GridLayout(0, 1, 0, 5));
		btnPanel.add(createButton("웨딩홀 검색", e -> openFrame(new SearchFrame())));
		btnPanel.add(createButton("결제 확인", e -> openFrame(new PaymentConfirmFrame())));
		btnPanel.add(createButton("인기 웨딩홀", e -> openFrame(new PopularHallFrame())));
		btnPanel.add(createButton("로그아웃", e -> openFrame(new LoginFrame())));
		btnPanel.add(createButton("종료", e -> System.exit(0)));
		btnPanel.setBounds(5, 70, 100, 150);

		JPanel centerPanel = createComponent(new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0)), 700, 1500);
		try (PreparedStatement pst = conn.prepareStatement(
				"select wh_Name,count(*) as count,wh_Price,wh_Add\r\n"
				+ "from payment as p\r\n"
				+ "left join weddinghall as wh \r\n"
				+ "on p.wh_No = wh.wh_No \r\n"
				+ "group by p.wh_No \r\n"
				+ "order by count desc limit 10;")) {
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				centerPanel.add(createComponent(new WhPanel(rs.getString("wh_Name"), rs.getInt("count"),
						rs.getInt("wh_Price"), rs.getString("wh_Add")), 732, 150));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JScrollPane scrollPane = new JScrollPane(centerPanel);
		scrollPane.setBounds(120, 70, 750, 480);

		add(mainLabel);
		add(btnPanel);
		add(scrollPane);
	}

	class WhPanel extends JPanel {
		public WhPanel(String whName, int count, int price, String address) { //
			setLayout(new BorderLayout());
			JLabel image = new JLabel(new ImageIcon(
					Toolkit.getDefaultToolkit().getImage("./제3과제 datafile/웨딩홀/" + whName + "/" + whName +"1.jpg").getScaledInstance(250,150,Image.SCALE_SMOOTH)));
			add(image, BorderLayout.LINE_START);
			JPanel centerPanel = createComponent(new JPanel(new GridLayout(0,1,0,5)),732,150);
			centerPanel.add(createLabel("예약: " + count + "건"));
			centerPanel.add(createLabel("이름: " + whName));
			centerPanel.add(createLabel("가격: " + new DecimalFormat("###,###").format(price)+ "원"));
			centerPanel.add(createLabel("주소: " + address));
			add(centerPanel,BorderLayout.CENTER);
			setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.BLACK));
		}

	}
	
	public static void main(String[] args) {
		new MainFrame().setVisible(true);
	}


}
