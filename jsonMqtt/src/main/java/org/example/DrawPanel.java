package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class DrawPanel extends JPanel implements PropertyChangeListener {

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		// Calculate the scaling factors
		int width = getWidth();
		int height = getHeight();
		double scaleX = (double) width / 100;
		double scaleY = (double) height / 100;

		// Scale and make origin the bottom left (instead of top left)
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.scale(scaleX, scaleY);
		AffineTransform transform = AffineTransform.getScaleInstance(1, -1);
		transform.translate(0, -100); // Translate to flip around the bottom
		g2d.transform(transform);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		repaint();
	}
	
}