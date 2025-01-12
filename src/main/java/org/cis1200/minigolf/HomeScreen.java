package org.cis1200.minigolf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * HomePage class displays the home screen with options to start a 3-hole or
 * 6-hole game.
 */
public class HomeScreen extends JFrame {

    private JLabel status;

    public HomeScreen() {
        setTitle("Mini Golf Game");
        setSize(500, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(54, 97, 52));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50)); // Padding around
                                                                              // edges

        JLabel titleLabel = new JLabel("Welcome to Mini Golf!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);

        mainPanel.add(Box.createVerticalStrut(25));

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setOpaque(false);
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));

        JButton threeHoleButton = new JButton("Start 3-Hole Game");
        styleButton(threeHoleButton);
        threeHoleButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        threeHoleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame(3);
            }
        });
        buttonsPanel.add(threeHoleButton);

        buttonsPanel.add(Box.createVerticalStrut(20));

        JLabel leaderboardLabel = new JLabel("Instructions");
        leaderboardLabel.setFont(new Font("Arial", Font.BOLD, 24));
        leaderboardLabel.setForeground(Color.WHITE);
        leaderboardLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(leaderboardLabel);

        mainPanel.add(Box.createVerticalStrut(20));

        String[] leaderboardData = { "The Water is bad ", "you will get an extra stroke",
            "The sand will slow you down",
            "Aim and increase power", "using arrows",
            "Enter to hit shot",
            "less strokes = WIN"
        };
        JList<String> latestScoresList = new JList<>(leaderboardData);
        latestScoresList.setFont(new Font("Arial", Font.PLAIN, 18));
        latestScoresList.setFixedCellHeight(30);
        latestScoresList.setAlignmentX(Component.CENTER_ALIGNMENT);
        latestScoresList.setOpaque(false);
        latestScoresList.setForeground(Color.WHITE);
        latestScoresList.setBackground(new Color(0, 0, 0, 0)); // transparent background
        latestScoresList.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 50)); // Padding
        mainPanel.add(latestScoresList);

        add(mainPanel);

        mainPanel.add(buttonsPanel, BorderLayout.CENTER);
        mainPanel.add(latestScoresList, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void styleButton(JButton button) {
        button.setPreferredSize(new Dimension(180, 50));
        button.setBackground(new Color(60, 69, 231, 136));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void startGame(int holes) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createGameWindow(holes);
            }
        });

        this.dispose();
    }

    private void createGameWindow(int holes) {

        JFrame frame = new JFrame("Mini Golf - " + holes + " Holes");
        frame.setLocation(300, 300);

        final JPanel control_panel1 = new JPanel();
        control_panel1.setLayout(new FlowLayout(FlowLayout.CENTER));
        frame.add(control_panel1, BorderLayout.NORTH);

        String power = "50";
        JLabel currentpower = new JLabel("Power " + power + "%");
        control_panel1.add(currentpower);

        /*
         * final JButton increase_power = new JButton("Increase");
         * control_panel1.add(increase_power);
         * increase_power.addActionListener(e -> {
         * if (GameBoard.power < 96) {
         * GameBoard.power += 5;
         * }
         * System.out.println(power);
         * currentpower.setText("Power = " + GameBoard.power + "%");
         * });
         * 
         * final JButton decrease_power = new JButton("Decrease");
         * control_panel1.add(decrease_power);
         * decrease_power.addActionListener(e ->{
         * if (GameBoard.power > 4) {
         * GameBoard.power -= 5;
         * }
         * currentpower.setText("Power = " + GameBoard.power + "%");
         * });
         */

        final JPanel status_panel = new JPanel();
        frame.add(status_panel, BorderLayout.SOUTH);
        final JLabel status = new JLabel("Hole" + (GolfCourse.currentHoleIndex() + 1));
        status_panel.add(status);

        final GameBoard board = new GameBoard(status, currentpower);
        frame.add(board, BorderLayout.CENTER);

        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        board.reset();
    }
}
