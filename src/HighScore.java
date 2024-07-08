import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

public class HighScore extends JDialog {

    private static final String HIGH_SCORE_URL = "http://localhost:8000/high_scores"; // Replace with your actual API endpoint

    public HighScore(JFrame parent) {
        super(parent, "High Scores", true); // Make this dialog modal
        initUI();
    }

    private void initUI() {
        setSize(400, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(getParent());

        // Create a table model with columns: Rank, Username, Score
        DefaultTableModel model = new DefaultTableModel();
        JTable table = new JTable(model);
        model.addColumn("Rank");
        model.addColumn("Username");
        model.addColumn("Score");

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);

        fetchAndDisplayHighScores(model);

        pack();
    }

    private void fetchAndDisplayHighScores(DefaultTableModel model) {
        try {
            URL url = new URL(HIGH_SCORE_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Parse JSON response
                JSONArray jsonArray = new JSONArray(response.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String username = jsonObject.getString("username");
                    int score = jsonObject.getInt("score");
                    model.addRow(new Object[]{i + 1, username, score});
                }
            } else {
                model.addRow(new Object[]{"Error fetching scores", "", ""});
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addRow(new Object[]{"Error fetching scores", "", ""});
        }
    }
}
