import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.io.File;
import java.net.URISyntaxException;
import java.util.Arrays;

public class Player {
    private int x;
    private int y;
    private int dx;
    private final int WIDTH = 80; // Desired width
    private final int HEIGHT = 45; // Desired height
    private final int SPEED = 4;  // Adjust the speed as needed
    private Image image;

    public Player(String basketImage) {
        loadImage(basketImage);
        x = 175; // Starting x position
        y = 500; // Starting y position
    }

    private void loadImage(String basketImage) {
        URL imageUrl = getClass().getClassLoader().getResource("resources/basket/" + basketImage);
        System.out.println("Player image URL: " + imageUrl); // Debugging
        if (imageUrl != null) {
            ImageIcon ii = new ImageIcon(imageUrl);
            image = ii.getImage().getScaledInstance(WIDTH, HEIGHT, Image.SCALE_SMOOTH);
        } else {
            System.err.println("Player image not found: " + basketImage);
            // List files in the resources/basket directory
            try {
                File dir = new File(getClass().getClassLoader().getResource("resources/basket/").toURI());
                System.out.println("Files in resources/basket/: " + Arrays.toString(dir.listFiles()));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    public void move() {
        x += dx;

        // Ensure the basket stays within the window boundaries
        if (x < 0) {
            x = 0;
        }

        if (x > 385 - WIDTH) { // Adjusted to fit perfectly within the window
            x = 385 - WIDTH;
        }
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            dx = -SPEED;
        }

        if (key == KeyEvent.VK_RIGHT) {
            dx = SPEED;
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT) {
            dx = 0;
        }
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

    public void update() {
        move();
    }
}
