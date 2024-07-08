import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainMenu extends JFrame {

    public MainMenu() {
        initUI();
    }

    private void initUI() {
        setTitle("Main Menu");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Fullscreen settings
        setUndecorated(true);
        GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(this);

        // Main panel for the whole window
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel for menu components
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(new Color(245, 245, 245));
        menuPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1, true),
                "Main Menu",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("SansSerif", Font.BOLD, 20)
        ));

        // Basket selection
        JLabel basketLabel = new JLabel("Select Basket:");
        basketLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        basketLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        String[] baskets = listImageFiles("resources/basket/");
        JComboBox<String> basketComboBox = new JComboBox<>(baskets);
        basketComboBox.setMaximumSize(new Dimension(200, 30));
        basketComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Object selection
        JLabel objectLabel = new JLabel("Select Falling Object:");
        objectLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        objectLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        String[] objects = listImageFiles("resources/object/");
        JComboBox<String> objectComboBox = new JComboBox<>(objects);
        objectComboBox.setMaximumSize(new Dimension(200, 30));
        objectComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Play Game button
        JButton playButton = new JButton("Play Game");
        playButton.setFont(new Font("SansSerif", Font.BOLD, 15));
        playButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        playButton.setPreferredSize(new Dimension(200, 50));
        playButton.setMaximumSize(new Dimension(200, 50));

        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedBasket = (String) basketComboBox.getSelectedItem();
                String selectedObject = (String) objectComboBox.getSelectedItem();

                if (selectedBasket == null || selectedObject == null) {
                    JOptionPane.showMessageDialog(MainMenu.this, "Please select both a basket and an object.", "Selection Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                dispose(); // Close the main menu
                SwingUtilities.invokeLater(() -> {
                    JFrame gameFrame = new JFrame("Falling Objects Game");
                    gameFrame.add(new GameBoard(selectedBasket, selectedObject)); // Pass the images here
                    gameFrame.setSize(400, 600);
                    gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    gameFrame.setLocationRelativeTo(null);
                    gameFrame.setVisible(true);
                });
            }
        });

        // Battle Mode button
        JButton battleModeButton = new JButton("Battle Mode");
        battleModeButton.setFont(new Font("SansSerif", Font.BOLD, 15));
        battleModeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        battleModeButton.setPreferredSize(new Dimension(200, 50));
        battleModeButton.setMaximumSize(new Dimension(200, 50));

        battleModeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedBasket = (String) basketComboBox.getSelectedItem();
                String selectedObject = (String) objectComboBox.getSelectedItem();

                if (selectedBasket == null || selectedObject == null) {
                    JOptionPane.showMessageDialog(MainMenu.this, "Please select both a basket and an object.", "Selection Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                dispose(); // Close the main menu
                SwingUtilities.invokeLater(() -> {
                    BattleModeActivity battleModeActivity = new BattleModeActivity(selectedBasket, selectedObject);
                    battleModeActivity.setVisible(true);
                });
            }
        });

        // High Scores button
        JButton highScoreButton = new JButton("High Scores");
        highScoreButton.setFont(new Font("SansSerif", Font.BOLD, 15));
        highScoreButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        highScoreButton.setPreferredSize(new Dimension(200, 50));
        highScoreButton.setMaximumSize(new Dimension(200, 50));

        highScoreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Show high scores (assuming a HighScore class exists)
                HighScore highScore = new HighScore(MainMenu.this);
                highScore.setVisible(true);
            }
        });

        // Post Selection button
        JButton postSelectionButton = new JButton("Post Selection");
        postSelectionButton.setFont(new Font("SansSerif", Font.BOLD, 15));
        postSelectionButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        postSelectionButton.setPreferredSize(new Dimension(200, 50));
        postSelectionButton.setMaximumSize(new Dimension(200, 50));

        postSelectionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedBasket = (String) basketComboBox.getSelectedItem();
                String selectedObject = (String) objectComboBox.getSelectedItem();

                if (selectedBasket == null || selectedObject == null) {
                    JOptionPane.showMessageDialog(MainMenu.this, "Please select both a basket and an object.", "Selection Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                postSelectionToDatabase(Login.username, selectedBasket, selectedObject);
            }
        });

        // Add components to menu panel
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Spacer
        menuPanel.add(basketLabel);
        menuPanel.add(basketComboBox);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Spacer
        menuPanel.add(objectLabel);
        menuPanel.add(objectComboBox);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Spacer
        menuPanel.add(playButton);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Spacer
        menuPanel.add(battleModeButton);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Spacer
        menuPanel.add(highScoreButton);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Spacer
        menuPanel.add(postSelectionButton);

        // Add menu panel to main panel
        mainPanel.add(menuPanel, BorderLayout.CENTER);

        // Add main panel to the frame
        add(mainPanel);
    }

    private String[] listImageFiles(String directoryPath) {
        File dir = new File(directoryPath);
        if (!dir.exists() || !dir.isDirectory()) {
            System.err.println("Directory not found or not a directory: " + dir.getAbsolutePath());
            return new String[0];
        }

        List<String> imageFiles = new ArrayList<>();
        for (File file : dir.listFiles()) {
            if (file.isFile() && (file.getName().endsWith(".png") || file.getName().endsWith(".jpg"))) {
                imageFiles.add(file.getName());
            }
        }
        if (imageFiles.isEmpty()) {
            System.err.println("No image files found in directory: " + dir.getAbsolutePath());
        }
        return imageFiles.toArray(new String[0]);
    }

    private void postSelectionToDatabase(String username, String basket, String object) {
        SwingUtilities.invokeLater(() -> {
            try {
                String serverUrl = "http://localhost:8000/updateSelection"; // Adjust if your server URL is different
                URL url = new URL(serverUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                // Create JSON object with username, basket, and object
                String jsonInputString = String.format("{\"username\": \"%s\", \"basket\": \"%s\", \"object\": \"%s\"}", username, basket, object);

                // Write JSON to request body
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                // Read response
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                        StringBuilder response = new StringBuilder();
                        String responseLine;
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }
                        System.out.println("Response: " + response.toString());
                        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(MainMenu.this, "Selection posted successfully."));
                    }
                } else {
                    System.err.println("Failed to post selection. Response code: " + responseCode);
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(MainMenu.this, "Failed to post selection.", "Error", JOptionPane.ERROR_MESSAGE));
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(MainMenu.this, "Failed to post selection: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // This is for testing purposes. In practice, the username would be passed from the Login class.
            MainMenu mainMenu = new MainMenu();
            mainMenu.setVisible(true);
        });
    }
}
