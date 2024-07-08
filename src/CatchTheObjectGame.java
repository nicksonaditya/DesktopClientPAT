import javax.swing.*;

public class CatchTheObjectGame extends JFrame {

    public CatchTheObjectGame(String basketImage, String objectImage) {
        initUI(basketImage, objectImage);
    }

    private void initUI(String basketImage, String objectImage) {
        add(new GameBoard(basketImage, objectImage));
        setTitle("Catch the Object Game");
        setSize(500, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainMenu mainMenu = new MainMenu();
            mainMenu.setVisible(true);
        });
    }
}
