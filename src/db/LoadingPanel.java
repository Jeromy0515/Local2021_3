package db;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class LoadingPanel extends JFrame {
	double progress = 0.0;
	public LoadingPanel() {
		JPanel panel = new Panel();
		new Thread(() -> {
			for (int i = 0; i <= 99; i++) {
				try {
					updateProgress();
					panel.repaint();
					Thread.sleep(100);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			dispose();
		}).start();
		setSize(260,280);
		setLayout(new BorderLayout());
		add(panel,BorderLayout.CENTER);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setBackground(Color.WHITE);
	}
	
	class Panel extends JPanel{
		
		@Override
		protected void paintComponent(Graphics g) {
			g.setColor(Color.BLUE);
			g.fillArc(20, 20, 200, 200, 90, (int)progress);

			g.setColor(Color.white);
			g.fillArc(50, 50, 140, 140, 0, 360);
			
			g.setColor(Color.BLACK);
			g.setFont(new Font("굴림",Font.BOLD,15));
			g.drawString((int)(-progress / 3.6)+"%", 100, 120);

		}
		
	}
	
	public void updateProgress(){
		progress -= 3.6;
	}
}
