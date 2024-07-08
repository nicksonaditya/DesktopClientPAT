import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GameBoard extends JPanel implements ActionListener {
    private Timer gameTimer;
    private Timer countdownTimer;
    private Player player;
    private List<FallingObject> fallingObjects;
    private boolean inGame;
    private int score;
    private int timeLeft;
    private final int GAME_DURATION = 30;
    private final int B_WIDTH = 500; // Increased width
    private final int B_HEIGHT = 550; // Increased height
    private final int DELAY = 10;
    private Image backgroundImage;

    public GameBoard(String basketImage, String objectImage) {
        initBoard(basketImage, objectImage);
    }

    private void initBoard(String basketImage, String objectImage) {
        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new TAdapter());

        inGame = true;
        score = 0;
        timeLeft = GAME_DURATION;

        player = new Player(basketImage);
        fallingObjects = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            fallingObjects.add(new FallingObject(objectImage));
        }

        gameTimer = new Timer(DELAY, this);
        gameTimer.start();
        startCountdownTimer();

        // Load the background image
        loadBackgroundImage();
    }

    private void loadBackgroundImage() {
        URL imageUrl = getClass().getClassLoader().getResource("resources/backgroundgame.jpg");
        if (imageUrl != null) {
            backgroundImage = new ImageIcon(imageUrl).getImage();
        } else {
            System.err.println("Background image not found.");
        }
    }

    private void startCountdownTimer() {
        countdownTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeLeft--;
                if (timeLeft <= 0) {
                    inGame = false;
                    gameTimer.stop();
                    countdownTimer.stop();
                    showGameOver();
                }
            }
        });
        countdownTimer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, B_WIDTH, B_HEIGHT, this);
        }

        if (inGame) {
            drawObjects(g);
        }

        Toolkit.getDefaultToolkit().sync();
    }

    private void drawObjects(Graphics g) {
        g.drawImage(player.getImage(), player.getX(), player.getY(), this);

        for (FallingObject obj : fallingObjects) {
            g.drawImage(obj.getImage(), obj.getX(), obj.getY(), this);
        }

        // Outline color
        g.setColor(Color.BLACK);
        g.drawString("Score: " + score, 11, 21);
        g.drawString("Score: " + score, 9, 21);
        g.drawString("Score: " + score, 11, 19);
        g.drawString("Score: " + score, 9, 19);

        g.drawString("Time: " + timeLeft + " seconds", B_WIDTH - 209, 21);
        g.drawString("Time: " + timeLeft + " seconds", B_WIDTH - 211, 21);
        g.drawString("Time: " + timeLeft + " seconds", B_WIDTH - 209, 19);
        g.drawString("Time: " + timeLeft + " seconds", B_WIDTH - 211, 19);

        // Main text color
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 10, 20);
        g.drawString("Time: " + timeLeft + " seconds", B_WIDTH - 210, 20); // Adjusted for the wider screen
    }

    private void showGameOver() {
        saveScoreToDatabase(Login.username, score); // Use stored username

        String msg = "Game Over\nFinal Score: " + score;
        int option = JOptionPane.showOptionDialog(this, msg, "Game Over",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, new Object[]{"Main Menu"}, null);

        if (option == 0) {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(GameBoard.this);
            topFrame.dispose(); // Close the game window
            MainMenu mainMenu = new MainMenu(); // Assuming MainMenu is the class for the main menu
            mainMenu.setVisible(true);
        }
    }

    private void saveScoreToDatabase(String username, int score) {
        SwingUtilities.invokeLater(() -> {
            try {
                URL url = new URL("http://localhost:8000/score");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                // Create JSON object with username and score
                String jsonInputString = "{\"username\": \"" + username + "\", \"score\": " + score + "}";

                // Write JSON to request body
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                // Read response
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println("Response: " + response.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        updatePlayer();
        updateFallingObjects();
        checkCollisions();
        repaint();
    }

    private void updatePlayer() {
        player.move();
    }

    private void updateFallingObjects() {
        for (FallingObject obj : fallingObjects) {
            obj.fall();
            if (obj.getY() > B_HEIGHT) {
                obj.resetPosition();
            }
        }
    }

    private void checkCollisions() {
        Rectangle playerBounds = player.getBounds();

        for (FallingObject obj : fallingObjects) {
            Rectangle objectBounds = obj.getBounds();

            if (playerBounds.intersects(objectBounds)) {
                score++;
                obj.resetPosition();
            }
        }
    }

    private class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            player.keyPressed(e);
        }

        @Override
        public void keyReleased(KeyEvent e) {
            player.keyReleased(e);
        }
    }
}
