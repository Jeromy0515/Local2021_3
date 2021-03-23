package frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.Format;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class PaymentCheckFrame extends BaseFrame{
	JTextField tfs[] = new JTextField[11];
	JCheckBox cb[] = new JCheckBox[3];
	
	public PaymentCheckFrame(String pmNum) {
		super("결제확인", 350, 480);
		
		JLabel mainLabel = createLabel("결제확인",new Font("맑은 고딕",Font.PLAIN,30),JLabel.CENTER);
		mainLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, Color.black));
		
		JPanel centerPanel = new JPanel(null);
		centerPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		for(int i=0;i<tfs.length;i++) {
			tfs[i] = new JTextField(18);
			tfs[i].setHorizontalAlignment(JTextField.LEFT);
			tfs[i].setEnabled(false);
		}
		JPanel cbPanel = new JPanel(new GridLayout(1,3,53,0));
		for(int i=1;i<=3;i++) {
			cb[i-1] = new JCheckBox(String.valueOf(i));
			cb[i-1].setEnabled(false);
			cbPanel.add(cb[i-1]);
		}
		
		String labelTxt[] = {"웨딩홀명","날짜","주소","예식형태","식사종류","식사비용","인원수","청첩장","홀사용료   ","총식사비용","청첩장금액","총금액"};
		
		int y = 10;
		for (int i = 0; i <= 6; i++) {
			JPanel panel = new JPanel(new GridLayout(0,2,-140,0));
			panel.add(createLabel(labelTxt[i]));
			panel.add(tfs[i]);
			panel.setBounds(10,y,310,20);
			centerPanel.add(panel);
			y+=30;
		}
		JPanel panel = new JPanel(new GridLayout(0,2,-145,0));
		panel.add(createLabel(labelTxt[7]));
		panel.add(cbPanel);
		panel.setBounds(10,y,310,20);
		y+=30;
		centerPanel.add(panel);
		
		int y2 = 10;
		JPanel grayPanel = new JPanel(null);
		for (int i = 7; i <= 9; i++) {
			JLabel label =createLabel(labelTxt[i+1]);
			JTextField tf = tfs[i];
			label.setBounds(5, y2, 100, 20);
			tf.setBounds(90, y2, 225, 20);
			grayPanel.add(label);
			grayPanel.add(tf);
			y2 += 30;
		}
		grayPanel.setBackground(Color.LIGHT_GRAY);
		grayPanel.setBounds(5,y,320,100);
		centerPanel.add(grayPanel);
		y+=110;
	
		JPanel panel2 = new JPanel(new GridLayout(0,2,-145,0));
		panel2.add(createLabel(labelTxt[11]));
		panel2.add(tfs[10]);
		panel2.setBounds(10,y,310,20);
		centerPanel.add(panel2);
		
		add(mainLabel,BorderLayout.NORTH);
		add(centerPanel,BorderLayout.CENTER);
		
		try (PreparedStatement pst = conn.prepareStatement(
				"select wh_Name,p_Date,wh_Add,wty_Name,m_Name,m_Price,p_People,i_No,wh_Price,m_Price*p_People as total_m_Price\r\n"
				+ "from payment as p \r\n"
				+ "inner join weddinghall as wh\r\n"
				+ "on p.wh_No = wh.wh_No\r\n"
				+ "inner join weddingtype as wty\r\n"
				+ "on p.wty_No = wty.wty_No\r\n"
				+ "inner join mealType as m\r\n"
				+ "on p.m_No = m.m_No\r\n"
				+ "where p_No = ?;")){
			pst.setObject(1, pmNum);
			ResultSet rs = pst.executeQuery();
			if(rs.next()) {
				int i = 0;
				for(String str:new String[] {rs.getString("wh_Name"),rs.getString("p_Date"),rs.getString("wh_Add"),rs.getString("wty_Name"),
						rs.getString("m_Name"),rs.getString("m_Price"),rs.getString("p_People")}) {
					tfs[i].setText(str);
					i++;
				}
				if(rs.getInt("i_No") != 0)
					cb[rs.getInt("i_No")-1].setSelected(true);
				
				int total = 0;
				for(int price:new int[] {rs.getInt("wh_Price"),rs.getInt("total_m_Price"),rs.getInt("i_No") != 0 ? 150000:0}){
					tfs[i].setText(String.format("%,d",price));
					total += price;
					i++;
				}
				tfs[10].setText(String.format("%,d", total));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		new PaymentCheckFrame("0502").setVisible(true);
	}
}
