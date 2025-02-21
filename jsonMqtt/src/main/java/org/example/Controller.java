package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;

public class Controller implements ActionListener {
	
	private MQTTServer subscriber;
	public String BROKER = "tcp://test.mosquitto.org:1883";
	public String TOPIC = "javiergs/emotiv/bcidata";

	private final Main mainFrame;

	public Controller(Main mainFrame) {
		this.mainFrame = mainFrame;
		setupKeyBindings();
	}

	private void setupKeyBindings() {
		// Get the input map and action map for the main frame
		InputMap inputMap = mainFrame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actionMap = mainFrame.getRootPane().getActionMap();

		// Bind CTRL+C to the startClient action
		inputMap.put(KeyStroke.getKeyStroke("control C"), "startClient");
		actionMap.put("startClient", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startClient();
			}
		});

		// Bind CTRL+D to the stopClient action
		inputMap.put(KeyStroke.getKeyStroke("control D"), "stopClient");
		actionMap.put("stopClient", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stopClient();
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Connect (CTRL+C)")) {
			startClient();
		} else if (e.getActionCommand().equals("Disconnect (CTRL+D)")) {
			stopClient();
		} else if (e.getActionCommand().equals("Exit")) {
			System.exit(0);
		} else if (e.getActionCommand().equals("Configure")) {
			showSettingsDialog();
		}
	}
	
	private void startClient() {
		if (subscriber != null && subscriber.isRunning()) {
			JOptionPane.showMessageDialog(mainFrame, "Already connected to broker", "Error", JOptionPane.ERROR_MESSAGE);
		} else {
			subscriber = new MQTTServer(mainFrame, BROKER, TOPIC);
			Thread subscriberThread = new Thread(subscriber);
			subscriberThread.start();
		}
	}
	
	private void stopClient() {
		if (subscriber != null && subscriber.isRunning()) {
			subscriber.stop();
			Blackboard.getInstance().clear();
			mainFrame.setStatus("Not connected");
		} else {
			JOptionPane.showMessageDialog(mainFrame, "Not connected to broker", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private boolean isValidUrl(String url) {
		// Regular expression to validate the URL format
		String urlPattern = "^tcp://[a-zA-Z0-9.-]+:\\d{1,5}$";
		return Pattern.matches(urlPattern, url);
	}

	private void showSettingsDialog() {
		JDialog dialog = new JDialog(mainFrame, "Settings", true);
		dialog.setLayout(new FlowLayout());
		dialog.setSize(300, 125);
		dialog.setLocationRelativeTo(mainFrame);

		JLabel label = new JLabel("Enter URL:");
		JTextField urlField = new JTextField(20);
		JLabel topicLabel = new JLabel("Enter Topic:");
		JTextField topicField = new JTextField(20);
		JButton saveButton = new JButton("Save");

		urlField.setText(BROKER);
		topicField.setText(TOPIC);

		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String inputUrl = urlField.getText();
				String inputTopic = topicField.getText();
				if (isValidUrl(inputUrl)) {
					BROKER = inputUrl;
					TOPIC = inputTopic;
					dialog.dispose();
					JOptionPane.showMessageDialog(mainFrame, "URL set to: " + BROKER + "\nTopic set to: " + TOPIC);
					if (subscriber != null) {
						stopClient();
					}
				} else {
					JOptionPane.showMessageDialog(mainFrame, "Invalid URL format. Please use 'tcp://hostname:port'.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		dialog.add(label);
		dialog.add(urlField);
		dialog.add(topicLabel);
		dialog.add(topicField);
		dialog.add(saveButton);
		dialog.setVisible(true);

	}
}