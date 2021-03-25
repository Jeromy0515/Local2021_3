package frame;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import model.UserInform;

public class WeddingHallFrame extends BaseFrame {
	float alpha = 1.0f;
	JTextField tfs[] = new JTextField[8]; // 주소,수용인원,홀사용료,예식형태,식사종류,식사비용,인원수,날짜
	JTextField tfIv;
	JPanel imagePanel;
	JLabel mainLabel;
	JButton payBtn = createComponent(createButtonWithoutMargin("결제", e -> payBtnAct()), 100, 30);
	JButton ivBtn = createComponent(createButtonWithoutMargin("청첩장 선택",
			e -> openFrame(new InvitationFrame(mainLabel.getText(),
					tfIv.getText().isEmpty() ? 1 : Integer.parseInt(tfIv.getText()), tfs[0].getText(), tfs[7].getText(),
					tfIv))),
			100, 30);
	boolean stop = false;
	static boolean ivCheck = false;
	Object whList[][] = new Object[42][7]; // 웨딩홀명,주소,수용인원,홀사용료,예식형태,식사종류,식사비용
	int index = 0;
	int numOfimageList = 1;
	UserInform ui;

	public WeddingHallFrame(UserInform ui) {
		super("웨딩홀", 950, 470);
		
		ivCheck = false;
		this.ui = ui;
		stop = false;

		imagePanel = createComponent(new JPanel() {
			@Override
			public void paint(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
//				g.drawImage(imageList.get(index).getImage(), 0,0,null);
				g.drawImage(getImage(380, 200, String.valueOf(whList[index][0])).getImage(), 0, 0, null);
			}
		}, 380, 200);

		setWhList();

		for (int i = 0; i < whList.length; i++) {
			if (whList[i][0].equals(ui.whName)) {
				index = i;
				break;
			}
		}

		for (int i = 0; i < tfs.length; i++) {
			tfs[i] = createComponent(new JTextField(), 100, 20);
			if (!(i == 6 || i == 7))
				tfs[i].setEnabled(false);
		}

		tfs[6].addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					if (e.getKeyCode() != KeyEvent.VK_BACK_SPACE)
						Integer.parseInt(String.valueOf(e.getKeyChar()));

					if (((JTextField) e.getSource()).getText().length() > 0 && tfs[7].getText().length() > 0)
						payBtn.setEnabled(true);
					else
						payBtn.setEnabled(false);
				} catch (Exception e2) {
					errorMessage("인원수를 바르게 입력해주세요");
					tfs[6].setText("");
				}
			}
		});

		tfs[7].addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				openFrame(new CalendarFrame(mainLabel.getText(), tfs[7], tfIv, ivBtn));
				if (tfs[6].getText().length() > 0)
					payBtn.setEnabled(true);
				else
					payBtn.setEnabled(false);
			}
		});

		for (int i = 1; i <= 6; i++)
			tfs[i - 1].setText(String.valueOf(whList[index][i]));

		JPanel fieldPanel = createComponent(new JPanel(new GridLayout(0, 2, -200, 20)), 450, 450);
		int cnt = 0;
		for (String text : "주소,수용인원,홀사용료,예식형태,식사종류,식사비용,인원수,날짜".split(",")) {
			fieldPanel.add(createLabel(text));
			fieldPanel.add(tfs[cnt]); 
			cnt++;
		}

		tfIv = createComponent(new JTextField(), 50, 30);
		tfIv.setHorizontalAlignment(JTextField.CENTER);
		tfIv.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {//tfIv의 길이 1로제한 1,2,3만 입력가능
				tfIv.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_MASK), "none");
				
				if(tfIv.getText().length() > 0 || !(e.getKeyChar() == 49 || e.getKeyChar() == 50 || e.getKeyChar() == 51))
					e.consume();
				
				if (ivCheck) 
					e.consume();
			};
		});

		payBtn.setEnabled(false);
		ivBtn.setEnabled(false);
		JPanel btnPanel = createComponent(new JPanel(new FlowLayout(FlowLayout.CENTER)), 350, 150);
		btnPanel.add(createComponent(createButtonWithoutMargin("이전", e -> previousBtnAct()), 100, 30));
		btnPanel.add(payBtn);
		btnPanel.add(createComponent(createButtonWithoutMargin("다음", e -> nextBtnAct()), 100, 30));
		btnPanel.add(ivBtn);
		btnPanel.add(tfIv);

		mainLabel = createComponent(createLabel(ui.whName, new Font("굴림", Font.BOLD, 40), JLabel.CENTER), 500, 50);
		JPanel leftPanel = createComponent(new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 15)), 450, 420);
		leftPanel.add(createComponent(createLabel("웨딩홀명", JLabel.CENTER), 500, 20));
		leftPanel.add(mainLabel);
		leftPanel.add(imagePanel);
		leftPanel.add(btnPanel);

		JPanel rightPanel = createComponentWithBorder(new JPanel(new BorderLayout()), 450, 450,
				BorderFactory.createEmptyBorder(35, 0, 35, 40));
		rightPanel.add(fieldPanel, BorderLayout.CENTER);

		add(rightPanel, BorderLayout.LINE_END);
		add(leftPanel);

		numOfimageList = getwhFileList();
		changeImage().start();
	}

	private void previousBtnAct() {
		if (index == 0)
			index = whList.length - 1;
		else
			index--;
		setInform();
		numOfimageList = getwhFileList();
		imageNum = 1;
	}

	private void nextBtnAct() {
		if (index == whList.length - 1)
			index = 0;
		else
			index++;

		setInform();
		numOfimageList = getwhFileList();
		imageNum = 1;
	}

	private int getwhFileList() {
		return new File("./제3과제 datafile/웨딩홀/" + whList[index][0]).list().length;
	}

	private Thread changeImage() {
		return new Thread(() -> {
			while (!stop) {
				for (int i = 0; i < 500; i++) {
					alpha -= 0.002f;
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					repaint();
				}
				if (numOfimageList == imageNum)
					imageNum = 1;
				else
					imageNum++;
				for (int i = 0; i < 500; i++) {
					alpha += 0.002f;
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					repaint();
				}
			}
		});
	}

	@Override
	public void closeFrame() {
		super.closeFrame();
		stop = true;
	}

	private void setInform() {
		for (int i = 1; i <= 6; i++)
			tfs[i - 1].setText(String.valueOf(whList[index][i]));
		mainLabel.setText(String.valueOf(whList[index][0]));
	}

	public void setWhList() { // 배열에 모든 웨딩홀에대한 정보 셋팅
		try (PreparedStatement pst = conn
				.prepareStatement(
						"select wh_Name,wh_Add,wh_People,wh_Price, wty_Name, m_Name,m_Price\r\n"
						+ "from weddinghall as wh\r\n"
						+ "inner join division as d\r\n"
						+ "on d.wh_No = wh.wh_No\r\n"
						+ "inner join weddingtype as wty\r\n"
						+ "on wty.wty_No = d.wty_No\r\n"
						+ "inner join mealtype as mt\r\n"
						+ "on d.m_No = mt.m_No\r\n"
						+ "order by wh_Name asc;")) {
			ResultSet rs = pst.executeQuery();
			int cnt = 0;
			while (rs.next()) {
				for (int i = 1; i <= whList[cnt].length; i++) {
					whList[cnt][i - 1] = rs.getObject(i).toString().trim();
				}
				cnt++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void payBtnAct() {
		if (Integer.parseInt(tfs[1].getText()) < Integer.parseInt(tfs[6].getText())) {
			errorMessage("수용인원보다 작게 입력하세요.");
			return;
		}
		ui.whName = mainLabel.getText();
		ui.people = Integer.parseInt(tfs[6].getText());
		ui.whType = tfs[3].getText();
		ui.mealType = tfs[4].getText();
		ui.ivNum = tfIv.getText().isEmpty() ? 0 : Integer.parseInt(tfIv.getText());
		ui.date = tfs[7].getText();

		openFrame(new BillFrame(mainLabel.getText(), Integer.parseInt(tfs[2].getText()),
				Integer.parseInt(tfs[5].getText()) * Integer.parseInt(tfs[6].getText()), ui.ivNum == 0 ? 0 : 150000,
				ui));
	}

	public static void main(String[] args) {
		new WeddingHallFrame(new UserInform("AW컨벤션센터", "서울 강남구 역삼동 680", 150, 2200000, "강당", "한식", 18000, 0, ""))
				.setVisible(true);
	}

}
