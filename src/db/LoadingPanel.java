package db;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Label;
import java.awt.Point;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class LoadingPanel extends JFrame {
//	int progress = 0;
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
//			Graphics2D g2 = (Graphics2D) g;
//			g2.translate(getWidth() / 2, getHeight() / 2);
//			g2.rotate(Math.toRadians(270));
//			Arc2D.Float arc = new Arc2D.Float(Arc2D.PIE);
//			Ellipse2D circle = new Ellipse2D.Float(0, 0, 110, 110);
//			arc.setFrameFromCenter(new Point(0, 0), new Point(120, 120));
//			circle.setFrameFromCenter(new Point(0, 0), new Point(100, 100));
//			arc.setAngleStart(1);
//			arc.setAngleExtent(-progress * 3.6);
//			g2.setColor(Color.BLUE);
//			g2.draw(arc);
//			g2.fill(arc);
//			g2.setColor(Color.white);
//			g2.draw(circle);
//			g2.fill(circle);
//			g2.rotate(Math.toRadians(360));
//			g2.setColor(Color.black);
//			g2.rotate(Math.toRadians(90));
//			g2.setFont(new Font("굴림",Font.BOLD,12));
//			g2.drawString(progress+"%", -15, 0);

		}
		
	
	}

//	public void updateProgress(int progress_value){
//			progress = progress_value;
//		}
	
	public void updateProgress(){
		progress -= 3.6;
	}
}
