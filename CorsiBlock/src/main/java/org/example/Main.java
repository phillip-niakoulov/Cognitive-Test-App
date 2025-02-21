package org.example;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Main extends JFrame {
    private final int GRID_SIZE = 5;
    private JButton[][] gridButtons = new JButton[GRID_SIZE][GRID_SIZE];
    private List<Point> sequence = new ArrayList<>();
    private List<Point> userInputs = new ArrayList<>();
    private int sequenceIndex = 0;
    private boolean inputEnabled = false;

    private final int flashDuration = 600;
    private final int delayBetweenFlashes = 300;

    private int currentSequenceLength = 3;

    private final String logFilePath = "log.csv";

    public Main() {
        setTitle("Corsi Block");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel gridPanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE, 2, 2));
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                JButton btn = new JButton();
                btn.setBackground(Color.GRAY);
                btn.setOpaque(true);
                btn.setBorderPainted(false);

                btn.setActionCommand(i + "," + j);
                btn.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (!inputEnabled) return;
                        handlePlayerClick(e.getActionCommand());
                    }
                });
                gridButtons[i][j] = btn;
                gridPanel.add(btn);
            }
        }
        add(gridPanel, BorderLayout.CENTER);

        JButton startButton = new JButton("Start Game");
        startButton.addActionListener(e -> startGame());
        add(startButton, BorderLayout.SOUTH);

        initializeLogFile();
    }

    private void initializeLogFile() {
        try {
            FileWriter fw = new FileWriter(logFilePath, true);
            fw.write("Timestamp,Level,Outcome,ExpectedSequence,UserSequence\n");
            fw.close();
        } catch (IOException e) {
            System.err.println("Error initializing log file: " + e.getMessage());
        }
    }

    private void startGame() {
        sequence.clear();
        userInputs.clear();
        sequenceIndex = 0;
        inputEnabled = false;
        Random rnd = new Random();
        for (int k = 0; k < currentSequenceLength; k++) {
            int row = rnd.nextInt(GRID_SIZE);
            int col = rnd.nextInt(GRID_SIZE);
            sequence.add(new Point(row, col));
        }
        flashSequence(0);
    }

    private void flashSequence(int index) {
        if (index >= sequence.size()) {
            inputEnabled = true;
            return;
        }
        Point p = sequence.get(index);
        JButton btn = gridButtons[p.x][p.y];

        Timer flashTimer = new Timer(flashDuration, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btn.setBackground(Color.GRAY);
                ((Timer)e.getSource()).stop();
                new Timer(delayBetweenFlashes, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e2) {
                        ((Timer)e2.getSource()).stop();
                        flashSequence(index + 1);
                    }
                }).start();
            }
        });
        btn.setBackground(Color.YELLOW);
        flashTimer.setRepeats(false);
        flashTimer.start();
    }

    private void handlePlayerClick(String command) {
        String[] parts = command.split(",");
        int row = Integer.parseInt(parts[0]);
        int col = Integer.parseInt(parts[1]);
        Point clickedPoint = new Point(row, col);

        userInputs.add(clickedPoint);

        JButton btn = gridButtons[row][col];
        btn.setBackground(Color.CYAN);
        new Timer(300, e -> {
            btn.setBackground(Color.GRAY);
            ((Timer)e.getSource()).stop();
        }).start();

        if (sequenceIndex < sequence.size() && clickedPoint.equals(sequence.get(sequenceIndex))) {
            sequenceIndex++;
            if (sequenceIndex == sequence.size()) {
                inputEnabled = false;
                JOptionPane.showMessageDialog(this, "Correct! Next level unlocked.");
                logRound("Success");
                currentSequenceLength++; // Increase level.
                new Timer(1000, e -> {
                    ((Timer)e.getSource()).stop();
                    startGame();
                }).start();
            }
        } else {
            inputEnabled = false;
            JOptionPane.showMessageDialog(this, "Wrong block! Try again at the same level.");
            logRound("Fail");
            new Timer(1000, e -> {
                ((Timer)e.getSource()).stop();
                startGame();
            }).start();
        }
    }

    private void logRound(String outcome) {
        long unixTimestamp = System.currentTimeMillis() / 1000;
        String level = String.valueOf(currentSequenceLength);
        String expectedSequence = sequenceToString(sequence);
        String userSequence = sequenceToString(userInputs);
        String logLine = String.format("%d,%s,%s,\"%s\",\"%s\"\n",
                unixTimestamp, level, outcome, expectedSequence, userSequence);
        try (FileWriter fw = new FileWriter(logFilePath, true)) {
            fw.write(logLine);
        } catch (IOException e) {
            System.err.println("Error logging round data: " + e.getMessage());
        }
    }

    private String sequenceToString(List<Point> seq) {
        StringBuilder sb = new StringBuilder();
        for (Point p : seq) {
            sb.append("(").append(p.x).append(",").append(p.y).append(");");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main game = new Main();
            game.setVisible(true);
        });
    }
}
