import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.border.TitledBorder;

public class SignUp extends JFrame {
    private JPanel signupPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton actionButton; // Dynamic action button
    private JTextArea responseArea;
    private JButton signupButton;

    public SignUp() {
        setTitle("User Signup");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Fullscreen mode
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main panel for the whole window
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel for signup components
        signupPanel = new JPanel(new GridBagLayout());
        signupPanel.setBackground(new Color(245, 245, 245));
        signupPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1, true),
                "Sign Up",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("SansSerif", Font.BOLD, 20)
        ));
        GridBagConstraints gbcSignup = new GridBagConstraints();
        gbcSignup.insets = new Insets(10, 10, 10, 10);

        // Username label and field
        gbcSignup.gridx = 0;
        gbcSignup.gridy = 0;
        signupPanel.add(new JLabel("Username:"), gbcSignup);

        gbcSignup.gridx = 1;
        gbcSignup.gridy = 0;
        gbcSignup.fill = GridBagConstraints.HORIZONTAL;
        usernameField = new JTextField(20);
        signupPanel.add(usernameField, gbcSignup);

        // Password label and field
        gbcSignup.gridx = 0;
        gbcSignup.gridy = 1;
        gbcSignup.fill = GridBagConstraints.NONE;
        signupPanel.add(new JLabel("Password:"), gbcSignup);

        gbcSignup.gridx = 1;
        gbcSignup.gridy = 1;
        gbcSignup.fill = GridBagConstraints.HORIZONTAL;
        passwordField = new JPasswordField(20);
        signupPanel.add(passwordField, gbcSignup);

        // Signup button (initially "Sign Up")
        actionButton = new JButton("Sign Up");
        actionButton.setFont(new Font("SansSerif", Font.BOLD, 15));
        gbcSignup.gridx = 0;
        gbcSignup.gridy = 2;
        gbcSignup.gridwidth = 2;
        gbcSignup.anchor = GridBagConstraints.CENTER;
        signupPanel.add(actionButton, gbcSignup);

        // Response area for signup
        responseArea = new JTextArea(5, 30);
        responseArea.setEditable(false);
        responseArea.setLineWrap(true);
        responseArea.setWrapStyleWord(true);
        responseArea.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        JScrollPane scrollPane = new JScrollPane(responseArea);
        gbcSignup.gridx = 0;
        gbcSignup.gridy = 3;
        gbcSignup.gridwidth = 2;
        gbcSignup.anchor = GridBagConstraints.CENTER;
        signupPanel.add(scrollPane, gbcSignup);

        // Button to switch to login window
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("SansSerif", Font.PLAIN, 15));
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the current signup window
                new Login().setVisible(true); // Open the login window
            }
        });

        // Panel for login button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(245, 245, 245));
        buttonPanel.add(loginButton);

        // Add signup panel and button panel to main panel
        mainPanel.add(signupPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add main panel to the frame
        add(mainPanel);

        // Action listener for signup button
        actionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (username.isEmpty() || password.isEmpty()) {
                    responseArea.setText("Username and password are required");
                    return;
                }

                try {
                    // Send signup data to Node.js server
                    URL url = new URL("http://localhost:8000/signup");
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
                        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                            StringBuilder response = new StringBuilder();
                            String responseLine;
                            while ((responseLine = br.readLine()) != null) {
                                response.append(responseLine.trim());
                            }
                            responseArea.setText(response.toString());
                        }
                    } else {
                        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"))) {
                            StringBuilder response = new StringBuilder();
                            String responseLine;
                            while ((responseLine = br.readLine()) != null) {
                                response.append(responseLine.trim());
                            }
                            responseArea.setText("Error signing up: " + response.toString());
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
                new SignUp().setVisible(true);
            }
        });
    }
}
