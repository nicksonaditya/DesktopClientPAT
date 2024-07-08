import javax.swing.*;

public class BattleModeActivity extends JFrame {

    private String basketImage;
    private String objectImage;

    public BattleModeActivity(String basketImage, String objectImage) {
        this.basketImage = basketImage;
        this.objectImage = objectImage;
        initUI();
    }

    private void initUI() {
        add(new WaitingState(this));
        setTitle("Catch the Object Game");
        setSize(500, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public void startGame() {
        SwingUtilities.invokeLater(() -> {
            getContentPane().removeAll();
            add(new GameBoard(basketImage, objectImage));
            revalidate();
            repaint();
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BattleModeActivity battleModeActivity = new BattleModeActivity("basket.png", "object.png");
            battleModeActivity.setVisible(true);
        });
    }
}
