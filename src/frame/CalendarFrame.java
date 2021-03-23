package frame;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import model.UserInform;

public class CalendarFrame extends BaseFrame{
	LocalDate now = LocalDate.now();
	LocalDate date = LocalDate.of(now.getYear(), now.getMonthValue(), 1);
	JPanel centerPanel = new JPanel(new GridLayout(0,7));
	JLabel ymLabel = createLabel(date.format(DateTimeFormatter.ofPattern("yyyy 년 MM 월")));
	JButton previousBtn = createButton("◁", e->previousBtnAct());
	String whName;
	
	JTextField tfDate,tfIv; // 웨딩홀폼 날짜 텍스트필드
	JButton btn; // 웨딩홀폼 청첩장 선택 버튼
	
	public CalendarFrame(String whName,JTextField t,JTextField t2,JButton b) {
		super("기간선택", 350, 420);
		
		this.whName = whName;
		
		tfDate = t;
		tfIv = t2;
		btn = b;
		
		setLayout(new FlowLayout(FlowLayout.CENTER,0,10));
		JPanel datePanel = createComponent(new JPanel(new FlowLayout(FlowLayout.CENTER,35,0)),350,20);
		JLabel dateLabel[] = new JLabel[7];
		String str[] = "일월화수목금토".split("");
		for(int i=0;i<dateLabel.length;i++) {
			dateLabel[i] = new JLabel(str[i]);
			datePanel.add(dateLabel[i]);
		}
		dateLabel[0].setForeground(Color.red);
		dateLabel[6].setForeground(Color.blue);
		
		JPanel btnPanel = createComponent(new JPanel(new FlowLayout(FlowLayout.CENTER)),350,32);
		previousBtn.setEnabled(false);
		btnPanel.add(previousBtn);
		btnPanel.add(ymLabel);
		btnPanel.add(createButton("▷", e->nextBtnAct()));
		
		setCalendar();
		add(btnPanel);
		add(datePanel);
		add(centerPanel);
	}
	
	private void setCalendar() {
		centerPanel.removeAll();
		boolean btnStart = false;
		String dayOfWeek[] = {"SUNDAY","MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY","SATURDAY"};
		int id = 0;
		
		ArrayList<Integer> dayList = getSelectedDate();
		
		for(int i=0;i<date.lengthOfMonth();i++) {
			if(!btnStart) {
				if(dayOfWeek[i%7].equals(String.valueOf(date.getDayOfWeek()))) {
					i=0;
					btnStart = true;
				}
			}
			if(btnStart) {
				JButton btn = createComponentWithBorder(createButtonWithoutMargin(i+1+"", e->calBtnAct(e.getActionCommand())), 48, 48,BorderFactory.createLineBorder(Color.black));
				if(id%7==0)
					btn.setForeground(Color.red);
				else if(id%7==6) 
					btn.setForeground(Color.blue);
				
				if(date.getMonthValue() == now.getMonthValue() && i+1<=now.getDayOfMonth())
					btn.setEnabled(false);
				
				for (int j = 0; j < dayList.size(); j++) {
					if(dayList.get(j) == i+1) { // i는 0부터 시작 날짜는 1부터 시작이라 +1해줘야됨
						btn.setEnabled(false);
						break;
					}
				}
				
				centerPanel.add(btn);
			}else {
				centerPanel.add(new JPanel());
			}
			id++;
		}
	}
	private void nextBtnAct() {
		date = date.plusMonths(1);
		setCalendar();
		ymLabel.setText(date.format(DateTimeFormatter.ofPattern("yyyy 년 MM 월")));
		previousBtn.setEnabled(true);
		revalidate();
	}
	
	private ArrayList<Integer> getSelectedDate() {
		ArrayList<Integer> list = new ArrayList<Integer>();
		try (PreparedStatement pst = conn.prepareStatement(
				"select wh_Name,day(p_date) as date \r\n"
				+ "from payment as p\r\n"
				+ "inner join weddinghall wh\r\n"
				+ "on wh.wh_No = p.wh_No\r\n"
				+ "where wh_Name like concat('%',?,'%') and month(p_date) = ?;")){
			pst.setObject(1, whName);
			pst.setObject(2, date.getMonthValue());
			ResultSet rs = pst.executeQuery();
			while(rs.next()) {
				list.add(rs.getInt("date"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	private void calBtnAct(String btnText) {
		closeFrame();
		tfDate.setText(String.format("%d-%02d-%02d", date.getYear(),date.getMonthValue(),Integer.parseInt(btnText)));
		tfIv.setText("");
		btn.setEnabled(true);
	}	
	
	private void previousBtnAct() {
		date = date.minusMonths(1);
		setCalendar();
		ymLabel.setText(date.format(DateTimeFormatter.ofPattern("yyyy 년 MM 월")));
		if(date.getMonthValue() == now.getMonthValue())
			previousBtn.setEnabled(false);
		revalidate();
	}

	public static void main(String[] args) {
//		new CalendarFrame("AW컨벤션센터").setVisible(true);
	}
}
