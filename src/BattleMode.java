import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class BattleMode extends JPanel implements ActionListener {
    private Timer gameTimer;
    private Timer countdownTimer;
    private Player player;
    private List<FallingObject> fallingObjects;
    private boolean inGame;
    private int score;
    private int opponentScore;
    private int timeLeft;
    private final int GAME_DURATION = 30;
    private final int B_WIDTH = 500;
    private final int B_HEIGHT = 550;
    private final int DELAY = 10;
    private Image backgroundImage;
    private WebSocketClient webSocketClient;

    public BattleMode(String basketImage, String objectImage) {
        addKeyListener(new TAdapter());
        setFocusable(true);
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        setDoubleBuffered(true);

        initWebSocket();

        player = new Player(basketImage);
        fallingObjects = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            fallingObjects.add(new FallingObject(objectImage));
        }
        inGame = false;
        score = 0;
        opponentScore = 0;
        timeLeft = GAME_DURATION;
        loadBackgroundImage();
    }

    private void initWebSocket() {
        try {
            webSocketClient = new WebSocketClient(new URI("ws://localhost:8000/websocket")) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    System.out.println("Connected to server");
                    webSocketClient.send("{\"type\": \"ready\", \"username\": \"SwingUser\"}");
                }

                @Override
                public void onMessage(String message) {
                    System.out.println("Received message: " + message);
                    // Handle incoming messages (similar to Android)
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("Connection closed with exit code " + code + " additional info: " + reason);
                }

                @Override
                public void onError(Exception ex) {
                    ex.printStackTrace();
                }
            };
            webSocketClient.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void loadBackgroundImage() {
        ImageIcon ii = new ImageIcon("src/resources/background.jpg");
        backgroundImage = ii.getImage();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawObjects(g);
    }

    private void drawObjects(Graphics g) {
        if (inGame) {
            g.drawImage(backgroundImage, 0, 0, null);
            player.draw(g);
            for (FallingObject obj : fallingObjects) {
                obj.draw(g);
            }

            g.setColor(Color.WHITE);
            g.drawString("Score: " + score, 10, 20);
            g.drawString("Time: " + timeLeft + " seconds", 400, 20);
            g.drawString("Opponent Score: " + opponentScore, 10, 40);
        } else {
            String msg = "Waiting for enemy...";
            Font small = new Font("Helvetica", Font.BOLD, 14);
            FontMetrics metr = getFontMetrics(small);

            g.setColor(Color.WHITE);
            g.setFont(small);
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 2);
        }
    }

    private void startGame() {
        inGame = true;
        gameTimer = new Timer(DELAY, this);
        gameTimer.start();
        countdownTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeLeft--;
                if (timeLeft <= 0) {
                    ((Timer) e.getSource()).stop();
                    inGame = false;
                    webSocketClient.send("{\"type\": \"score\", \"username\": \"SwingUser\", \"score\": " + score + "}");
                    // Handle end of game (navigate to Endgame screen)
                }
                repaint();
            }
        });
        countdownTimer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        updateGame();
        repaint();
    }

    private void updateGame() {
        player.update();
        for (FallingObject obj : fallingObjects) {
            obj.fall();
            if (obj.getY() > B_HEIGHT) {
                obj.resetPosition();
            }
            if (obj.getBounds().intersects(player.getBounds())) {
                score++;
                obj.resetPosition();
            }
        }
    }

    private class TAdapter extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent e) {
            player.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            player.keyPressed(e);
        }
    }
}
