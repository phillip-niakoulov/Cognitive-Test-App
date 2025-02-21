package org.example;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {
	private JLabel statusLabel;

	public Main() {
		setLayout(new BorderLayout());
		// size
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int squareSize = (2 * screenSize.height) / 3;
		setSize(squareSize, squareSize);
		setLocationRelativeTo(null);
		// controller
		Controller controller = new Controller(this);
		// menu bar
		setJMenuBar(createMenuBar(controller));
		// work area
		DrawPanel cobotPanel = new DrawPanel();
		add(cobotPanel);
		Blackboard.getInstance().addPropertyChangeListener(cobotPanel);
		// Status label
		statusLabel = new JLabel("Status: Not connected");
		add(statusLabel, BorderLayout.SOUTH);
	}
	
	private JMenuBar createMenuBar(Controller controller) {
		// Run dropdown
		JMenu fileMenu = new JMenu("Run");
		// item - start
		JMenuItem connectItem = new JMenuItem("Connect (CTRL+C)");
		connectItem.addActionListener(controller);
		fileMenu.add(connectItem);
		// item - stop
		JMenuItem pauseItem = new JMenuItem("Disconnect (CTRL+D)");
		pauseItem.addActionListener(controller);
		fileMenu.add(pauseItem);
		// item - exit
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(controller);
		fileMenu.add(exitItem);

		// Settings dropdown
		JMenu settingsMenu = new JMenu("Settings");
		// Configure menu
		JMenuItem settingsItem = new JMenuItem("Configure");
		settingsItem.addActionListener(controller);
		settingsMenu.add(settingsItem);

		// menu bar
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		menuBar.add(settingsMenu);
		return menuBar;
	}

	public void setStatus(String status) {
		statusLabel.setText("Status: " + status);
	}

	public static void main(String[] args) {
		Main main = new Main();
		main.setTitle("Subscriber");
		main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		main.setVisible(true);
	}
	
}