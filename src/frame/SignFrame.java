package frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.sql.ResultSet;
import java.util.Random;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import db.CM;
import model.WhInform;

public class SignFrame extends BaseFrame{
	JPanel signPanel = new JPanel();
	boolean signCheck;
	int pmNum;
	WhInform wi;
	public SignFrame(WhInform wi) {
		super("서명", 200,250);
		this.wi = wi;
		signPanel.setBackground(Color.white);
		signPanel.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				signPanel.getGraphics().fillOval(e.getX(), e.getY(), 12,12);
				signCheck = true;
			}
		});
		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		btnPanel.add(createButton("확인", e->confirm()));
		add(signPanel,BorderLayout.CENTER);
		add(btnPanel,BorderLayout.SOUTH);
	}
	
	private void confirm() {
		if(!signCheck) {
			eMsg("서명을 하지 않았습니다.", "경고");
			return;
		}
		
		createRandNum();
		iMsg("결제가 완료되었습니다.\n결제번호:"+pmNum, "메시지");
		CM cm = new CM();
		cm.connect();
		int whNo = Integer.parseInt(cm.getOneSqlResult("select * from weddinghall where wh_Name = ?", "wh_No", wi.whName));
		int wtyNo = Integer.parseInt(cm.getOneSqlResult("select * from weddingtype where wty_Name = ?", "wty_No", wi.wty));
		int mtyNo = Integer.parseInt(cm.getOneSqlResult("select * from mealtype where m_Name = ?", "m_No", wi.mty));
		
		cm.execute("insert into payment values (?,?,?,?,?,?,?,?)", pmNum,whNo,Integer.parseInt(WeddingHallFrame.tfs[6].getText()),
				wtyNo,mtyNo,WeddingHallFrame.tfIv.getText().isEmpty() ? 0 : Integer.parseInt(WeddingHallFrame.tfIv.getText()),WeddingHallFrame.tfs[7].getText(),uNo);
		cm.close();
		if(JOptionPane.showConfirmDialog(null, "청첩장을 보내겠습니까?","정보",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			openFrame(new FriendLIstFrame(pmNum));
		}
	}
	
	
	private void createRandNum() {
		StringBuilder builder = new StringBuilder();
		Random rand = new Random();
		
		for(int i=0;i<4;i++) {
			builder.append(rand.nextInt(10));
		}
		
		int randNum = Integer.parseInt(builder.toString());
		
		CM cm = new CM();
		cm.connect();
		
		ResultSet rs = cm.executeQuery("select * from payment");
		try {
			while(rs.next()) {
				if(rs.getInt("p_no") == randNum) {
					createRandNum();
				}else {
					pmNum = randNum;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		cm.close();
	}
	
	@Override
	public void closedAction() {
		previousFrame();
	}
	public static void main(String[] args) {
		new SignFrame(null).setVisible(true);
	}
}
