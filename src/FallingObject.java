import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.io.File;
import java.net.URISyntaxException;
import java.util.Arrays;

public class FallingObject {
    private int x;
    private int y;
    private final int WIDTH = 20; // Desired width
    private final int HEIGHT = 20; // Desired height
    private final int SPEED = 2;
    private Image image;

    public FallingObject(String objectImage) {
        loadImage(objectImage);
        resetPosition();
    }

    private void loadImage(String objectImage) {
        URL imageUrl = getClass().getClassLoader().getResource("resources/object/" + objectImage);
        System.out.println("Falling object image URL: " + imageUrl); // Debugging
        if (imageUrl != null) {
            ImageIcon ii = new ImageIcon(imageUrl);
            image = ii.getImage().getScaledInstance(WIDTH, HEIGHT, Image.SCALE_SMOOTH);
        } else {
            System.err.println("Falling object image not found: " + objectImage);
            // List files in the resources/object directory
            try {
                File dir = new File(getClass().getClassLoader().getResource("resources/object/").toURI());
                System.out.println("Files in resources/object/: " + Arrays.toString(dir.listFiles()));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    public void fall() {
        y += SPEED;
    }

    public void resetPosition() {
        x = (int) (Math.random() * (390 - WIDTH));
        y = 0;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return WIDTH;
    }

    public int getHeight() {
        return HEIGHT;
    }

    public Image getImage() {
        return image;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }

    public void draw(Graphics g) {
        g.drawImage(image, x, y, null);
    }
}
