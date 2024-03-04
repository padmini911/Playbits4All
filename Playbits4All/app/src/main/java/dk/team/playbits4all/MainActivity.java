package dk.team.playbits4all;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;

import dk.team.playbits4all.Modules.AlphabetsActivity;
import dk.team.playbits4all.Modules.ColorsActivity;
import dk.team.playbits4all.Modules.GamesActivity;
import dk.team.playbits4all.Modules.MusicActivity;
import dk.team.playbits4all.Modules.NumbersActivity;
import dk.team.playbits4all.Modules.SmartMathActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        Button colorsButton = findViewById(R.id.colorsButton);
        Button musicButton = findViewById(R.id.musicButton);
        Button alphabetsButton = findViewById(R.id.alphabetsButton);
        Button numbersButton = findViewById(R.id.numbersButton);
        Button smartMathButton = findViewById(R.id.smartMathButton);
        Button gamesButton = findViewById(R.id.gamesButton);

        colorsButton.setOnClickListener(v -> startColorsActivity());

        musicButton.setOnClickListener(v -> startMusicActivity());

        alphabetsButton.setOnClickListener(v -> startAlphabetsActivity());

        numbersButton.setOnClickListener(v -> startNumbersActivity());

        smartMathButton.setOnClickListener(v -> startSmartMathActivity());

        gamesButton.setOnClickListener(v -> startGamesActivity());
    }

    private void startColorsActivity() {
        Intent intent = new Intent(this, ColorsActivity.class);
        startActivity(intent);
    }

    private void startMusicActivity() {
        Intent intent = new Intent(this, MusicActivity.class);
        startActivity(intent);
    }

    private void startAlphabetsActivity() {
        Intent intent = new Intent(this, AlphabetsActivity.class);
        startActivity(intent);
    }

    private void startNumbersActivity() {
        Intent intent = new Intent(this, NumbersActivity.class);
        startActivity(intent);
    }

    private void startSmartMathActivity() {
        Intent intent = new Intent(this, SmartMathActivity.class);
        startActivity(intent);
    }

    private void startGamesActivity() {
        Intent intent = new Intent(this, GamesActivity.class);
        startActivity(intent);
    }
}
