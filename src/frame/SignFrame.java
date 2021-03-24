package frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import model.UserInform;

public class SignFrame extends BaseFrame {
	boolean signCheck = false;
	static StringBuilder pmNum = new StringBuilder();
	JButton okBtn = createComponent(createButtonWithoutMargin("확인", e -> okBtnAct()), 60, 30);

	UserInform ui;

	public SignFrame(UserInform ui) {
		super("서명", 200, 250);

		this.ui = ui;

		JPanel signPanel = new SignPanel();
		signPanel.setBackground(Color.white);

		JPanel btnPanel = new JPanel();
		btnPanel.add(okBtn);

		add(signPanel, BorderLayout.CENTER);
		add(btnPanel, BorderLayout.SOUTH);
	}

	private class SignPanel extends JPanel implements MouseMotionListener {

		public SignPanel() {
			setLayout(new BorderLayout());
			addMouseMotionListener(this);
			JPanel panel = new JPanel();
			panel.setBackground(Color.white);
			add(panel, BorderLayout.CENTER);
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			getGraphics().fillOval(e.getX() - 5, e.getY(), 15, 15);
			signCheck = true;
		}

		@Override
		public void mouseMoved(MouseEvent e) {
		}
	}

	public static void main(String[] args) {
		new SignFrame(null).setVisible(true);
	}

	private void okBtnAct() {
		if (!signCheck) {
			errorMessage("서명을 하지 않았습니다.");
			return;
		}
		updateRandNum();
		try (PreparedStatement pst = conn.prepareStatement("select p_No from payment")) {
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				if (String.valueOf(rs.getInt("p_No")).equals(pmNum.toString()))
					updateRandNum();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		JOptionPane.showMessageDialog(null, "결제가 완료되었습니다.\n결제번호:" + pmNum, "메시지", JOptionPane.INFORMATION_MESSAGE);
		try (PreparedStatement pst = conn.prepareStatement("insert into payment values (?,?,?,?,?,?,?,?)")) {
			pst.setObject(1, pmNum.toString());
			pst.setObject(2, getfk("select * from weddinghall where wh_Name = '" + ui.whName + "'", "wh_No"));
			pst.setObject(3, ui.people);
			pst.setObject(4, getfk("select * from weddingtype where wty_Name like '%" + ui.whType + "%'", "wty_No"));
			pst.setObject(5, getfk("select * from mealType where m_Name = '" + ui.mealType + "'", "m_No"));
			pst.setObject(6, ui.ivNum);
			pst.setObject(7, ui.date);
			pst.setObject(8, user_No);

			pst.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (JOptionPane.showConfirmDialog(null, "청접장을 보내겠습니까?", "정보", JOptionPane.YES_NO_OPTION) == 0) {
			openFrame(new FriendListFrame(pmNum.toString()));
		} else {
			openFrame(new MainFrame());
		}
	}

	int getfk(String sql, String col) {
		try (PreparedStatement pst = conn.prepareStatement(sql)) {
			ResultSet rs = pst.executeQuery();
			if (rs.next())
				return rs.getInt(col);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	private void updateRandNum() {
		pmNum.delete(0, pmNum.length());
		Random rand = new Random();
		for (int i = 0; i < 4; i++) {
			pmNum.append(rand.nextInt(10));
		}
	}

}
