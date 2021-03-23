package frame;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class PaymentConfirmFrame extends BaseFrame {
	JTextField numField = createComponent(new JTextField(), 260, 30);
	Stack<String> stack = new Stack<String>();

	StringBuilder pmNum = new StringBuilder();
	
	JPanel btnPanel = createComponent(new JPanel(new GridLayout(0, 3, 4, 4)), 260, 170);

	JButton[] buttons = new JButton[10];

	public PaymentConfirmFrame() {
		super("결제번호 확인", 300, 300);
		setLayout(new FlowLayout(FlowLayout.CENTER));
		add(createComponent(createLabel("결제번호를 입력하세요."), 195, 40));

		add(createButton("확인", e -> confirm()));
		add(numField);

		numField.setBorder(BorderFactory.createLineBorder(Color.black, 2));
		numField.setHorizontalAlignment(JTextField.CENTER);
		numField.setEnabled(false);
		
		add(btnPanel);

		int count = 0;
		for (int i = 0; i < 11; i++) {
			if (i == 9) {
				btnPanel.add(createButton("재배열", e -> refreshButtons()));
			} else {
				btnPanel.add(buttons[count] = createButton("", e -> insertNumber(e.getActionCommand())));
				count++;
			}
		}
		btnPanel.add(createButton("←", e -> previous()));

		refreshButtons();
	}

	public void insertNumber(String btnText) {
		pmNum.append(btnText);
		numField.setText(pmNum.toString());
	}

	public void refreshButtons() {
		ArrayList<Integer> list = new ArrayList<Integer>();
		ArrayList<Integer> leftList = new ArrayList<Integer>();

		for (int i = 0; i < 10; i++) {
			leftList.add(i);
		}

		while (leftList.size() != 0) {
			int num = (int) (Math.random() * 10);
			for (int j = 0; j < leftList.size(); j++) {
				if (num == leftList.get(j)) {
					leftList.remove(j);
					list.add(num);
				}
			} 
		}

		for (int i = 0; i < 10; i++) {
			buttons[i].setText(list.get(i) + "");
		}
		revalidate();
	}

	public void confirm() {
		if(pmNum.toString().isEmpty()) {
			errorMessage("결제번호가 없습니다.");
			return;
		}
		
		if(!isCorrect()) {
			errorMessage("일치하는 결제번호가 없습니다.");
			numField.setText(pmNum.delete(0, pmNum.length()).toString());
			refreshButtons();
			return;
		}
		openFrame(new PaymentCheckFrame(pmNum.toString()));
	}
	
	private boolean isCorrect() {
		try (PreparedStatement pst = conn.prepareStatement("select * from payment")){
			ResultSet rs = pst.executeQuery();
			while(rs.next()) {
				if(rs.getString("p_No").equals(pmNum.toString())) 
					return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void previous() {
		if(pmNum.toString().isEmpty()) {
			errorMessage("삭제할 번호가 없습니다.");
			return;
		}
			
		pmNum.delete(pmNum.length()-1, pmNum.length());
		numField.setText(pmNum.toString());
	}

	public static void main(String[] args) {
		new PaymentConfirmFrame().setVisible(true);
	}
}
