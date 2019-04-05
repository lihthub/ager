package ager.view;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

public class BackgroundPanel extends JPanel {

	private static final long serialVersionUID = -6869784231391178411L;
	
	private Image img;

	public BackgroundPanel(Image img) {
		this.img = img;
		this.setOpaque(true);
	}

	// Draw the back ground.
	public void paintComponent(Graphics g) {
		super.paintComponents(g);
		g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this);
	}
	
}