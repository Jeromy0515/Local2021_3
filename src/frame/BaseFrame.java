package frame;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

public class BaseFrame extends JFrame{
	
	static int user_No;
	
	static Connection conn = null;
	static {
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost/2021지방_2?serverTimezone=UTC","user","1234");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public BaseFrame(String title,int width,int height) {
		super(title);
		setSize(width,height);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}
	
	public static JLabel createLabel(String text, Font font) {
		JLabel label = new JLabel(text);
		label.setFont(font);
		return label;
	}
	
	public static JLabel createLabel(String text) {
		JLabel label = new JLabel(text);
		label.setFont(new Font("굴림",Font.BOLD,12));
		return label;
	}

	
	public static <T extends JComponent> T createComponent(T comp,int width,int height) {
		comp.setPreferredSize(new Dimension(width,height));
		return comp;
	}
	
	public static JButton createButton(String text,ActionListener act) {
		JButton button = new JButton(text);
		button.addActionListener(act);
		button.setMargin(new Insets(0,0,0,0));
		return button;
	}
	
	public void openFrame(JFrame frame) {
		dispose();
		frame.setVisible(true);
	}
	
	public static void informMessage(String caption) {
		JOptionPane.showMessageDialog(null, caption,"정보",JOptionPane.INFORMATION_MESSAGE);
	}

}
