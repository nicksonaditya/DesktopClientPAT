import javax.swing.*;
import java.awt.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class WaitingState extends JPanel {
    private Timer checkOpponentTimer;
    private boolean opponentReady;
    private BattleModeActivity battleModeActivity;

    public WaitingState(BattleModeActivity activity) {
        this.battleModeActivity = activity;
        initUI();
        startCheckOpponentTimer();
    }

    private void initUI() {
        setPreferredSize(new Dimension(500, 550));
        setBackground(Color.BLACK);
    }

    private void startCheckOpponentTimer() {
        checkOpponentTimer = new Timer();
        checkOpponentTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                checkOpponentReady();
            }
        }, 0, 3000);
    }

    private void checkOpponentReady() {
        SwingUtilities.invokeLater(() -> {
            try {
                URL url = new URL("http://localhost:8000/checkEnemyReady");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    opponentReady = true; // Assume response is true for simplicity
                    if (opponentReady) {
                        startGame();
                    }
                } else {
                    System.err.println("Failed to check opponent readiness. Response code: " + responseCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void startGame() {
        checkOpponentTimer.cancel();
        battleModeActivity.startGame();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Waiting for opponent...", 150, 275);
    }
}
