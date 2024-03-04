package dk.team.playbits4all.Modules;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import dk.team.playbits4all.R;
public class ColorsActivity extends AppCompatActivity {

    private static final String TAG_FIND_2 = "find**";
    private static final String TAG_FIND_3 = "find***";
    private static final String TAG_RESET = "reset";
    private static final String TAG_FOLLOW_ME = "followme";
    private static final String TAG_TACO_SAYS = "taco says";
    private static final String TAG_COLOR_MIX = "color mix";
    private TextView colorTextView;
    private NfcAdapter nfcAdapter;
    private ImageView colorView;
    private String[] selectedColors;
    private String followMeColor;

    private int colorIndex;

    private MediaPlayer mediaPlayer;
    private boolean find2GameInProgress = false;
    private boolean find3GameInProgress = false;
    private boolean followMeGameInProgress = false;
    private boolean tacoSaysGameInProgress = false;
    private boolean colorMixGameInProgress = false;
    private List<String> tacoSaysSequence;
    private int tacoSaysIndex;
    private List<String> ColorMixSequence = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_colors);

        colorTextView = findViewById(R.id.colorNameTextView);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        colorView = findViewById(R.id.colorView);

        if (nfcAdapter == null) {
            showToast("NFC is not available on this device.");
        }

        ImageView backArrowImageView = findViewById(R.id.backArrow);
        backArrowImageView.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        enableForegroundDispatch();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disableForegroundDispatch();
    }

    private void enableForegroundDispatch() {
        Intent intent = new Intent(this, ColorsActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
    }

    private void disableForegroundDispatch() {
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action) ||
                NfcAdapter.ACTION_TECH_DISCOVERED.equals(action) ||
                NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            String colorName = readColorFromNFC(intent);

            switch (colorName) {
                case TAG_FIND_2:
                    // Start the "Find 2" game
                    initializeFind2Game();
                    break;
                case TAG_FIND_3:
                    // Start the "Find 3" game
                    initializeFind3Game();
                    break;
                case TAG_FOLLOW_ME:
                    // Start the "Follow Me" game
                    initializeFollowMeGame();
                    break;
                case TAG_TACO_SAYS:
                    // Start the "Taco Says" game
                    initializeTacoSaysGame();
                    break;
                case TAG_COLOR_MIX:
                    // Start the "ColorMix" game
                    initializeColorMixGame();
                case TAG_RESET:
                    // Restart the game
                    stopGames();
                    displayColor("Name");
                    break;
                default:
                    if (find3GameInProgress) {
                        // Handle "Find 3" game logic
                        handleFind3Game(intent);
                    } else if (find2GameInProgress) {
                        // Handle "Find 2" game logic
                        handleFind2Game(intent);
                    } else if (followMeGameInProgress) {
                        // Handle "Follow Me" game logic
                        handleFollowMeGame(intent);
                    } else if (tacoSaysGameInProgress) {
                        // Handle "Taco Says" game logic
                        handleTacoSaysGame(colorName);
                    } else if (colorMixGameInProgress){
                        // Handle "ColorMix" game logic
                        handleColorMixGame(intent);
                    } else if (isSupportedColor(colorName)) {
                        // Only display if the color is supported for normal color detection
                        displayColor(colorName);
                    } else {
                        // Handle unsupported colors or conditions as needed
                        Toast.makeText(this, "Unsupported color: " + colorName, Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    }

    private void stopGames() {
        // Stop any ongoing games and reset game flags
        find2GameInProgress = false;
        find3GameInProgress = false;
        followMeGameInProgress = false;
        tacoSaysGameInProgress = false;
        colorMixGameInProgress = false;
    }
    private void displayGameColor(String colorName) {
        if (isSupportedColor(colorName)) {
            displayColor(colorName);
        } else {
            showToast("Unsupported color: " + colorName);
        }
    }
    private boolean isSupportedColor(String colorName) {
        // Define a set of supported colors
        Set<String> supportedColors = new HashSet<>(Arrays.asList("blue", "red", "green", "yellow", "pink"));

        // Check if the colorName is in the supportedColors set (case-insensitive)
        return supportedColors.contains(colorName.toLowerCase());
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private String readColorFromNFC(Intent intent) {
        NdefMessage ndefMessage = (NdefMessage) Objects.requireNonNull(intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES))[0];
        NdefRecord record = ndefMessage.getRecords()[0];

        byte[] payload = record.getPayload();
        int languageCodeLength = payload[0] & 0x1F;

        return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, StandardCharsets.UTF_8);
    }

    private void displayColor(String colorName) {

        ImageView colorView = findViewById(R.id.colorView);

        colorTextView.setText(String.format("Color: %s", colorName));

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        switch (colorName.toLowerCase()) {
            case "blue":
                colorView.setImageResource(R.drawable.blue_circle);
                mediaPlayer = MediaPlayer.create(this, R.raw.blue);
                break;
            case "red":
                colorView.setImageResource(R.drawable.red_triangle);
                mediaPlayer = MediaPlayer.create(this, R.raw.red);
                break;
            case "green":
                colorView.setImageResource(R.drawable.green_star);
                mediaPlayer = MediaPlayer.create(this, R.raw.green);
                break;
            case "pink":
                colorView.setImageResource(R.drawable.pink_square);
                mediaPlayer = MediaPlayer.create(this, R.raw.pink);
                break;
            case "yellow":
                colorView.setImageResource(R.drawable.yellow_oval);
                mediaPlayer = MediaPlayer.create(this, R.raw.yellow);
                break;
            default:
                colorView.setImageResource(R.drawable.empty_shape);
                break;
        }

        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }
    private void initializeColorMixGame() {
        // Display instructions to the user for ColorMix game
        Toast.makeText(this, "Tap on two colors to see their combination!", Toast.LENGTH_SHORT).show();

        // Start the "ColorMix" game
        colorMixGameInProgress = true;
    }

    private void handleColorMixGame(Intent intent) {
        String colorName = readColorFromNFC(intent);
        if (isSupportedColor(colorName)) {
            // Only display if the color is supported
            displayColor(colorName);

            // Check if two colors have been selected for mixing
            if (ColorMixSequence.size() < 2) {
                // Add the selected color to the list
                ColorMixSequence.add(colorName);
                Toast.makeText(this, "Selected color: " + colorName, Toast.LENGTH_SHORT).show();

                // Check if two colors are now selected
                if (ColorMixSequence.size() == 2) {
                    // Mix the colors and display the result
                    mixAndDisplayColors(ColorMixSequence.get(0), ColorMixSequence.get(1));
                    // Clear the selected colors for the next round
                    ColorMixSequence.clear();

                    initializeColorMixGame();
                }
            }
        } else {
            // Handle unsupported colors or conditions as needed
            Toast.makeText(this, "Unsupported color: " + colorName, Toast.LENGTH_SHORT).show();
        }
    }

    private void mixAndDisplayColors(String color1, String color2) {
        // Mix the colors and get the color value for the mixed color
        int mixedColorValue = mixColors(color1, color2);

        colorView.setImageResource(R.drawable.empty_shape);
        // Update the background color based on the mixed color
        colorView.setBackgroundColor(mixedColorValue);
    }
    private int mixColors(String color1, String color2) {
        // Add more combinations as needed
        if (("red".equalsIgnoreCase(color1) && "pink".equalsIgnoreCase(color2)) ||
                ("pink".equalsIgnoreCase(color1) && "red".equalsIgnoreCase(color2))) {
            return Color.parseColor("#ff0080");
        } else if (("red".equalsIgnoreCase(color1) && "green".equalsIgnoreCase(color2)) ||
                ("green".equalsIgnoreCase(color1) && "red".equalsIgnoreCase(color2))) {
            return Color.parseColor("#808026");
        } else if (("red".equalsIgnoreCase(color1) && "blue".equalsIgnoreCase(color2)) ||
                ("blue".equalsIgnoreCase(color1) && "red".equalsIgnoreCase(color2))) {
            return Color.parseColor("#800080");
        } else if (("red".equalsIgnoreCase(color1) && "yellow".equalsIgnoreCase(color2)) ||
                ("yellow".equalsIgnoreCase(color1) && "red".equalsIgnoreCase(color2))) {
            return Color.parseColor("#ff8000");
        } else if (("pink".equalsIgnoreCase(color1) && "green".equalsIgnoreCase(color2)) ||
                ("green".equalsIgnoreCase(color1) && "pink".equalsIgnoreCase(color2))) {
            return Color.parseColor("#8080a6");
        } else if (("pink".equalsIgnoreCase(color1) && "blue".equalsIgnoreCase(color2)) ||
                ("blue".equalsIgnoreCase(color1) && "pink".equalsIgnoreCase(color2))) {
            return Color.parseColor("#8000ff");
        } else if (("pink".equalsIgnoreCase(color1) && "yellow".equalsIgnoreCase(color2)) ||
                ("yellow".equalsIgnoreCase(color1) && "pink".equalsIgnoreCase(color2))) {
            return Color.parseColor("#ff8080");
        } else if (("green".equalsIgnoreCase(color1) && "blue".equalsIgnoreCase(color2)) ||
                ("blue".equalsIgnoreCase(color1) && "green".equalsIgnoreCase(color2))) {
            return Color.parseColor("#0080a6");
        } else if (("green".equalsIgnoreCase(color1) && "yellow".equalsIgnoreCase(color2)) ||
                ("yellow".equalsIgnoreCase(color1) && "green".equalsIgnoreCase(color2))) {
            return Color.parseColor("#80ff26");
        } else if (("blue".equalsIgnoreCase(color1) && "yellow".equalsIgnoreCase(color2)) ||
                ("yellow".equalsIgnoreCase(color1) && "blue".equalsIgnoreCase(color2))) {
            return Color.parseColor("#80ff26");
        } else {
            // Default case, return a default color or handle it as needed
            return Color.parseColor("#000000"); // Replace with your default color
        }
    }


    private void initializeTacoSaysGame() {
        tacoSaysSequence = new ArrayList<>();
        tacoSaysIndex = 0;
        startNextTacoSaysRound();
        Toast.makeText(this, "Taco Says: " + tacoSaysSequence.get(tacoSaysIndex), Toast.LENGTH_SHORT).show();
        tacoSaysGameInProgress = true;
    }
    private void startNextTacoSaysRound() {
        String newColor = getRandomColor();
        tacoSaysSequence.add(newColor);
    }
    private String getRandomColor() {
        String[] allColors = {"blue", "red", "green", "yellow", "pink"};
        Random random = new Random();
        return allColors[random.nextInt(allColors.length)];
    }
    private void handleTacoSaysGame(String tappedColor) {
        if (tappedColor.equalsIgnoreCase(tacoSaysSequence.get(tacoSaysIndex))) {
            // User tapped the correct color, continue to the next in the sequence
            tacoSaysIndex++;

            // Check if the user has successfully followed the entire sequence
            if (tacoSaysIndex >= tacoSaysSequence.size()) {
                // User successfully followed the sequence, increase the sequence
                startNextTacoSaysRound();
                Toast.makeText(this, "Taco Says: " + getTacoSaysSequenceString(), Toast.LENGTH_SHORT).show();

                // Reset the index for the user's input
                tacoSaysIndex = 0;
            }
        } else {
            // User tapped the wrong color, game over
            Toast.makeText(this, "Oops! You didn't follow Taco's instructions. Game over.", Toast.LENGTH_SHORT).show();

            // Reset the Taco Says game
            initializeTacoSaysGame();
        }
    }
    private String getTacoSaysSequenceString() {
        StringBuilder sequenceStringBuilder = new StringBuilder();
        for (String color : tacoSaysSequence) {
            sequenceStringBuilder.append(color).append(", ");
        }
        // Remove the trailing comma and space
        return sequenceStringBuilder.substring(0, sequenceStringBuilder.length() - 2);
    }

    private void initializeFollowMeGame() {
        // Implement the logic to initialize the "Follow Me" game
        // For example, you can randomly select a color for the user to follow
        // and then display instructions to the user.
        String[] allColors = {"blue", "red", "green", "yellow", "pink"};
        String followColor = allColors[new Random().nextInt(allColors.length)];

        // Display instructions to the user
        Toast.makeText(this, "Follow the color: " + followColor, Toast.LENGTH_SHORT).show();

        // Set the color for the "Follow Me" game
        followMeColor = followColor;

        // Start the "Follow Me" game
        followMeGameInProgress = true;
    }
    private void handleFollowMeGame(Intent intent) {
        // Handle logic for the "Follow Me" game
        String colorName = readColorFromNFC(intent);
        if (isSupportedColor(colorName)) {
            // Only display if the color is supported
            displayColor(colorName);

            // Check if the tapped color matches the expected color in the game
            if (colorName.equalsIgnoreCase(followMeColor)) {
                // User tapped the correct color, continue the game
                Toast.makeText(this, "Correct! Now follow the next color.", Toast.LENGTH_SHORT).show();

                // Initialize the next color for the user to follow
                initializeFollowMeGame();
            } else {
                // User tapped the wrong color, game over
                Toast.makeText(this, "Wrong color! Game over.", Toast.LENGTH_SHORT).show();

                // Restart the "Follow Me" game
                initializeFollowMeGame();
            }
        }
    }


        private void initializeFind2Game() {
        String[] allColors = {"blue", "red", "green", "yellow", "pink"};
        selectedColors = new String[2];
        Random random = new Random();

        selectedColors[0] = allColors[random.nextInt(allColors.length)];

        do {
            selectedColors[1] = allColors[random.nextInt(allColors.length)];
        } while (selectedColors[1].equals(selectedColors[0]));

        colorIndex = 0;

        showToast("Find the colors in order: " + selectedColors[0] + ", " + selectedColors[1]);

        find2GameInProgress = true;
    }

    private void handleFind2Game(Intent intent) {
        String colorName = readColorFromNFC(intent);
        if (isSupportedColor(colorName)) {
            displayGameColor(colorName);

            if (colorName.equalsIgnoreCase(selectedColors[colorIndex])) {
                colorIndex++;

                if (colorIndex >= selectedColors.length) {
                    showToast("Congratulations! You found both colors.");
                    initializeFind2Game();
                } else {
                    showToast("Correct color! Now find: " + selectedColors[colorIndex]);
                }
            } else {
                showToast("Wrong color! Game over.");
                initializeFind2Game();
            }
        } else {
            showToast("Unsupported color: " + colorName);
        }
    }

    private void initializeFind3Game() {
        String[] allColors = {"blue", "red", "green", "yellow", "pink"};
        selectedColors = new String[3];
        Random random = new Random();

        for (int i = 0; i < selectedColors.length; i++) {
            selectedColors[i] = allColors[random.nextInt(allColors.length)];
        }

        colorIndex = 0;

        showToast("Find the colors in order: " +
                selectedColors[0] + ", " + selectedColors[1] + ", " + selectedColors[2]);

        find3GameInProgress = true;
    }

    private void handleFind3Game(Intent intent) {
        String colorName = readColorFromNFC(intent);
        if (isSupportedColor(colorName)) {
            displayGameColor(colorName);

            if (colorName.equalsIgnoreCase(selectedColors[colorIndex])) {
                colorIndex++;

                if (colorIndex >= selectedColors.length) {
                    showToast("Congratulations! You found all three colors.");
                    initializeFind3Game();
                } else {
                    showToast("Correct color! Now find: " + selectedColors[colorIndex]);
                }
            } else {
                showToast("Wrong color! Game over.");
                initializeFind3Game();
            }
        } else {
            showToast("Unsupported color: " + colorName);
        }
    }
}