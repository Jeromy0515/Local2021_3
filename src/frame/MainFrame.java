package frame;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MainFrame extends BaseFrame{
	
	public MainFrame() {
		super("메인", 900, 600);
		
		JLabel mainLabel = createLabel("결혼식장 예약프로그램",new Font("굴림",Font.BOLD,20));
		mainLabel.setHorizontalAlignment(JLabel.CENTER);
		
		JPanel btnPanel = new JPanel(new GridLayout(0,1,0,5));
		btnPanel.add(createButton("웨딩홀 검색", e->{}));
		btnPanel.add(createButton("결제 확인", e->{}));
		btnPanel.add(createButton("인기 웨딩홀", e->{}));
		btnPanel.add(createButton("로그아웃", e->{}));
		btnPanel.add(createButton("종료", e->{}));
		
		JPanel centerPanel = new JPanel(new GridLayout(0,1));
		
		try (PreparedStatement pst = conn.prepareStatement(
				"select wh_Name,count(*) as count,wh_Price,wh_Add\r\n"
				+ "from payment as p\r\n"
				+ "left join weddinghall as wh \r\n"
				+ "on p.wh_No = wh.wh_No \r\n"
				+ "group by p.wh_No \r\n"
				+ "order by count desc limit 10;")){
			ResultSet rs = pst.executeQuery();
			while(rs.next()) {
				centerPanel.add(createComponent(new WhPanel(rs.getString("wh_Name"),rs.getInt("count"),rs.getInt("wh_Price"),rs.getString("wh_Add")), 600, 550));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		add(mainLabel);
		add(btnPanel);
		add(centerPanel);
	}
	
	class WhPanel extends JPanel{
		public WhPanel(String imgPath,int count,int price, String address) { //
			setLayout(new BorderLayout());
			JLabel image = new JLabel(new ImageIcon("./datafile/웨딩홀/"+imgPath+"/"+imgPath+"1.jpg"));
			add(image,BorderLayout.LINE_START);
		}
		
		
	}
	
	
	
}
