package org.example;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class Main extends JFrame {

    private JPanel mainPanel;
    private JLabel instructionLabel;
    private JLabel numberLabel;
    private JTextField inputField;
    private JButton startButton;
    private JButton submitButton;

    private final int displayDuration = 3000;

    private String currentDigits;
    private int digitCount = 3;

    public Main() {
        createUI();
    }

    private void createUI() {
        setTitle("Digit Span");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));

        instructionLabel = new JLabel("Press Start to begin the game", SwingConstants.CENTER);
        mainPanel.add(instructionLabel, BorderLayout.NORTH);

        numberLabel = new JLabel("", SwingConstants.CENTER);
        numberLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        mainPanel.add(numberLabel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout());
        inputField = new JTextField(10);
        inputField.setEnabled(false);
        bottomPanel.add(inputField);

        startButton = new JButton("Start");
        bottomPanel.add(startButton);

        submitButton = new JButton("Submit");
        submitButton.setEnabled(false);
        bottomPanel.add(submitButton);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startButton.setEnabled(false);
                instructionLabel.setText("Memorize the digits!");
                inputField.setText("");
                inputField.setEnabled(false);
                generateAndDisplayDigits();
            }
        });

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkInput();
            }
        });
    }

    private void generateAndDisplayDigits() {
        currentDigits = generateRandomDigits(digitCount);
        numberLabel.setText(currentDigits);

        Timer timer = new Timer(displayDuration, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                numberLabel.setText("");
                instructionLabel.setText("Enter the digits you saw:");
                inputField.setEnabled(true);
                submitButton.setEnabled(true);
                ((Timer) e.getSource()).stop();
                inputField.requestFocusInWindow();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    private String generateRandomDigits(int length) {
        Random rand = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(rand.nextInt(10));
        }
        return sb.toString();
    }

    private void checkInput() {
        String userInput = inputField.getText().trim();
        String outcome;
        if (userInput.equals(currentDigits)) {
            outcome = "Success";
            JOptionPane.showMessageDialog(this, "Correct! Get ready for a longer sequence.");
            digitCount++;
        } else {
            outcome = "Fail";
            JOptionPane.showMessageDialog(this, "Incorrect. The correct sequence was: " + currentDigits);
        }
        logAttempt(System.currentTimeMillis() / 1000, digitCount, outcome, currentDigits, userInput);
        inputField.setEnabled(false);
        submitButton.setEnabled(false);
        instructionLabel.setText("Press Start to begin next round");
        startButton.setEnabled(true);
    }

    private void logAttempt(long timestamp, int level, String outcome, String expectedSequence, String userSequence) {
        File logFile = new File("log.csv");
        boolean fileExists = logFile.exists();

        String logLine = String.format("%d,%d,%s,%s,%s", timestamp, level, outcome, expectedSequence, userSequence);

        try (PrintWriter writer = new PrintWriter(new FileWriter(logFile, true))) {
            if (!fileExists) {
                writer.println("Timestamp,Level,Outcome,ExpectedSequence,UserSequence");
            }
            writer.println(logLine);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main().setVisible(true);
            }
        });
    }
}
