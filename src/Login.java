import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class Login extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JTextArea responseArea;
    public static String username; // Add this line to store the username

    public Login() {
        setTitle("User Login");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Fullscreen mode
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Dispose on close to separate windows

        // Main panel for the whole window
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel for login components
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(new Color(245, 245, 245));
        loginPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1, true),
                "Login",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("SansSerif", Font.BOLD, 20)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Username label and field
        gbc.gridx = 0;
        gbc.gridy = 0;
        loginPanel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        usernameField = new JTextField(20);
        loginPanel.add(usernameField, gbc);

        // Password label and field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        loginPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        passwordField = new JPasswordField(20);
        loginPanel.add(passwordField, gbc);

        // Login button
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 15));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(loginButton, gbc);

        // Response area
        responseArea = new JTextArea(5, 30);
        responseArea.setEditable(false);
        responseArea.setLineWrap(true);
        responseArea.setWrapStyleWord(true);
        responseArea.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        JScrollPane scrollPane = new JScrollPane(responseArea);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(scrollPane, gbc);

        // Panel for signup button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton signupButton = new JButton("Sign Up");
        signupButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the current login window
                new SignUp().setVisible(true); // Open the signup window
            }
        });
        buttonPanel.add(signupButton);

        // Add login panel and button panel to main panel
        mainPanel.add(loginPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add main panel to the frame
        add(mainPanel);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (username.isEmpty() || password.isEmpty()) {
                    responseArea.setText("Username and password are required");
                    return;
                }

                try {
                    URL url = new URL("http://localhost:8000/login");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);

                    // Create JSON object
                    String jsonInputString = "{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}";
                    try (OutputStream os = conn.getOutputStream()) {
                        byte[] input = jsonInputString.getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }

                    // Read response
                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        responseArea.setText("Login successful");
                        // Show the main menu after successful login
                        SwingUtilities.invokeLater(() -> {
                            MainMenu mainMenu = new MainMenu();
                            mainMenu.setVisible(true);
                            dispose(); // Close the login window
                        });
                    } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        responseArea.setText("Invalid username or password");
                    } else {
                        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"))) {
                            StringBuilder response = new StringBuilder();
                            String responseLine;
                            while ((responseLine = br.readLine()) != null) {
                                response.append(responseLine.trim());
                            }
                            responseArea.setText("Error logging in: " + response.toString());
                        }
                    }

                    conn.disconnect();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    responseArea.setText("Error connecting to server: " + ex.getMessage());
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Login().setVisible(true);
            }
        });
    }
}
