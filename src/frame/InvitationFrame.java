package frame;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class InvitationFrame extends BaseFrame{
	String itemTitle[] = "1번,2번,3번,결정".split(",");
	int ivNum = 1;
	JLabel imageLabel,numLabel;
	
	JTextField tf; // 웨딩홀폼에 청첩장 선택번호 필드
	public InvitationFrame(String whName,int ivNum,String addresss,String date,JTextField t) {
		super("청첩장", 300, 500);
		JPanel contentsPanel = createComponent(new JPanel(null), 300, 490);
		this.ivNum = ivNum;
		
		tf = t;
		
		numLabel = new JLabel(ivNum+"번 이미지");
		numLabel.setHorizontalAlignment(JLabel.CENTER);
		
		JPopupMenu popupMenu = new JPopupMenu();
		
		JMenuItem menuItem[] = new JMenuItem[4];
		
		for(int i=0;i<menuItem.length;i++) {
			menuItem[i] = new JMenuItem(itemTitle[i]);
			menuItem[i].addActionListener(e->itemAct(e.getActionCommand()));
			popupMenu.add(menuItem[i]);
		}
		
		contentsPanel.add(popupMenu);
		contentsPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(SwingUtilities.isRightMouseButton(e)) {
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
		
		JLabel textLabel = new JLabel("<html><center>오랜 기다림 속에서<br>저희 두 사람,한 마음이 되어<br>참된 사랑의 결실을 맺게 되었습니다.<br>오셔서 축복해 주시면 감사하겠습니다.</center></html>");
		textLabel.setHorizontalAlignment(JLabel.CENTER);
		textLabel.setBounds(20,100,250,150);
		contentsPanel.add(textLabel);
		
		JLabel informLabel = new JLabel("<html>웨딩홀명:"+whName+"<br><br>장소:"+addresss+"<br><br>날짜:"+date+"</html>");
		informLabel.setBounds(65,190,200,200);
		contentsPanel.add(informLabel);
		
		imageLabel = new JLabel(getImage(300, 490, ivNum));
		imageLabel.setBounds(0,0,300,445);
		contentsPanel.add(imageLabel,BorderLayout.CENTER);
		
		
		add(numLabel,BorderLayout.NORTH);
		add(contentsPanel,BorderLayout.CENTER);
	}
	
	private void itemAct(String actionCommand) {
		if(actionCommand.equals("결정")) {
			informMessage("디자인 "+ivNum+"번으로 결정되었습니다.");
			tf.setText(ivNum+"");
			WeddingHallFrame.ivCheck = true;
			closeFrame();
		}else {
			ivNum = Integer.parseInt(actionCommand.substring(0,1));
			imageLabel.setIcon(getImage(300, 490, ivNum));
			numLabel.setText(ivNum+"번 이미지");
		}
	}
	
	public static void main(String[] args) {
		new InvitationFrame("AW컨벤션센터",2,"서울 강남구 역삼동 680","2021-04-07",null).setVisible(true);
	}

}
