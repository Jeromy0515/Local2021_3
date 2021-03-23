package frame;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.mysql.cj.protocol.ResultsetRow;

public class LoginFrame extends BaseFrame{
	private JTextField idField;
	private JPasswordField pwField;
	
	public LoginFrame() {
		super("로그인", 300, 180);
		setLayout(new FlowLayout());
		JLabel mainLabel = createLabel("WEDDING", new Font("맑은 고딕",Font.BOLD,40),JLabel.CENTER);
		
		idField = new JTextField(12);
		pwField = new JPasswordField(12);
		
		JPanel centerPanel = new JPanel(new GridLayout(2,2,-100,10));
		centerPanel.add(createLabel("ID:"));
		centerPanel.add(idField);
		centerPanel.add(createLabel("PW:"));
		centerPanel.add(pwField);
		
		add(mainLabel);
		add(centerPanel);
		add(createComponent(createButtonWithoutMargin("로그인", e->login()), 60, 60));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	private void login() {
		if(isCorrect())
			openFrame(new MainFrame());
	}
	
	private boolean isCorrect() {
		try (PreparedStatement pst = conn.prepareStatement("select u_No from user where u_id = ? and u_Pw = ?")){
			pst.setObject(1, idField.getText());
			pst.setObject(2, pwField.getText());
			ResultSet rs = pst.executeQuery();
			if(rs.next()) {
				getInformOfInvitation(rs.getInt("u_No"));
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private void getInformOfInvitation(int user_No) {
		try (PreparedStatement pst = conn.prepareStatement(
			"select u_name, dayofyear(p_date) - dayofyear(now()) as dDay,i_to from user as u \r\n"
			+ "left join payment as p \r\n"
			+ "on u.u_no = p.u_no \r\n"
			+ "left join invitation as iv \r\n"
			+ "on p.p_no = iv.p_no\r\n"
			+ "where i_to = ? and p_date > now() order by p_date;")){
			pst.setObject(1, user_No);
			ResultSet rs = pst.executeQuery();
			if(rs.next()) {
				informMessage(rs.getString("u_name")+"님의 결혼식이 D-"+rs.getInt("dDay")+"일 남았습니다.");
				BaseFrame.user_No = rs.getInt("i_to");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new LoginFrame().setVisible(true);
	}
	
}
