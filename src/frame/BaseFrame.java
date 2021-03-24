package frame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Stack;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.swing.border.Border;

public class BaseFrame extends JFrame{

	public static final int FIELD_EMPTY = -1;
	
	static Stack<JFrame> stack = new Stack<JFrame>();
	
	static int imageNum = 1;
	
	static int user_No = 44;

	static Connection conn = null;
	static {
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost/2021지방_2?serverTimezone=UTC", "user", "1234");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public BaseFrame(String title, int width, int height) {
		super(title);
		setSize(width, height);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				stack.push(BaseFrame.this);
			}
			@Override
			public void windowClosing(WindowEvent e) {
				closeFrame();
			}
		});
	}

	public void closeFrame() {
		stack.pop();
		try {
			openFrame(stack.peek());
		} catch (Exception ex) {
			System.exit(0);
		}
	}
	
	public static JLabel createLabel(String text, Font font,int alig) {
		JLabel label = new JLabel(text);
		label.setFont(font);
		label.setHorizontalAlignment(alig);
		return label;
	}

	public static JLabel createLabel(String text) {
		JLabel label = new JLabel(text);
		label.setFont(new Font("굴림", Font.BOLD, 12));
		return label;
	}
	
	public static JLabel createLabel(String text,int alig) {
		JLabel label = new JLabel(text);
		label.setFont(new Font("굴림", Font.BOLD, 12));
		label.setHorizontalAlignment(alig);
		return label;
	}
	
	public static <T extends JComponent> T createComponent(T comp, int width, int height) {
		comp.setPreferredSize(new Dimension(width, height));
		return comp;
	}

	public static <T extends JComponent> T createComponentWithBorder(T comp, int width, int height,Border border) {
		comp.setPreferredSize(new Dimension(width, height));
		comp.setBorder(border);
		return comp;
	}

	

	public static JButton createButtonWithoutMargin(String text, ActionListener act) {
		JButton button = new JButton(text);
		button.addActionListener(act);
		button.setMargin(new Insets(0, 0, 0, 0));
		return button;
	}

	public static JButton createButton(String text, ActionListener act) {
		JButton button = new JButton(text);
		button.addActionListener(act);
		return button;
	}

	public static ImageIcon getImage(int width, int height, String whName) {
		return new ImageIcon(
				Toolkit.getDefaultToolkit().getImage("./제3과제 datafile/웨딩홀/" + whName + "/" + whName + imageNum +".jpg").getScaledInstance(width, height, Image.SCALE_SMOOTH));
		
	}
	
	//getimage 오버로딩하기
	
	public static ImageIcon getImage(int width, int height, int num) {
		return new ImageIcon(
				Toolkit.getDefaultToolkit().getImage("./제3과제 datafile/청첩장/청첩장"+ num +".jpg").getScaledInstance(width, height, Image.SCALE_SMOOTH));
	}

	public void openFrame(JFrame frame) {
		dispose();
		frame.setVisible(true);
	}

	public static void informMessage(String caption) {
		JOptionPane.showMessageDialog(null, caption, "정보", JOptionPane.INFORMATION_MESSAGE);
	}
	
	public static void errorMessage(String caption) {
		JOptionPane.showMessageDialog(null, caption,"경고", JOptionPane.ERROR_MESSAGE);
	}
	
	
	
}
