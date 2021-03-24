package frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.UserInform;

public class BillFrame extends BaseFrame{
	UserInform ui;
	public BillFrame(String whName,int hallPrice,int mealPrice,int ivPrice,UserInform ui) {
		super("계산서", 350, 400);
		
		this.ui = ui;
		
		JLabel mainLabel = createLabel("계산서",new Font("굴림",Font.BOLD,30),JLabel.CENTER);
		
		JPanel gridPanels[] = new JPanel[6];
		for(int i=0;i<gridPanels.length;i++) {
			gridPanels[i] = createComponent(new JPanel(new GridLayout(0,2,100,0)),350,-222222);
		}
		
		gridPanels[0].setBackground(Color.white);
		
		gridPanels[1].setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, Color.black));
		gridPanels[4].setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, Color.black));
		
		JPanel gridPanel = new JPanel(new GridLayout(0,1,0,-10));
		for(int i=0;i<gridPanels.length;i++)
			gridPanel.add(gridPanels[i]);
		gridPanel.setBorder(BorderFactory.createEmptyBorder(0,10,0,10));
		
		String labelTitle[] = new String("웨딩홀명."+whName+".항목.금액.홀사용료."+String.format("%,d",hallPrice)+".식사비용."
		+String.format("%,d",mealPrice)+".청첩장."+String.format("%,d",ivPrice)+".총금액."
		+new DecimalFormat("###,###").format(hallPrice+mealPrice+ivPrice).toString()).split("\\.");
		
		JLabel labels[] = new JLabel[12];
		for(int i=0;i<labels.length;i++) {
			labels[i] = createLabel(labelTitle[i]);
			if(i%2==0)
				labels[i].setHorizontalAlignment(JLabel.LEFT);
			else
				labels[i].setHorizontalAlignment(JLabel.RIGHT);
		}
		
		if(ivPrice == 0)
			labels[9].setEnabled(false);
		
		int arr[] = {4,6,2};
		
		for(int i=0,k=0;i<labels.length;i++) {
			gridPanels[k].add(labels[i]);
			if(i%2==1)
				k++;
		}
		
		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		btnPanel.add(createComponent(createButtonWithoutMargin("결제", e->openFrame(new SignFrame(ui))),60,30));
		
		add(mainLabel,BorderLayout.NORTH);
		add(gridPanel,BorderLayout.CENTER);
		add(btnPanel,BorderLayout.SOUTH);
	}	
	

}
