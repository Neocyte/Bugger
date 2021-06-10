package edu.bloomu.bugger;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class SplashScreenActivity extends AppCompatActivity {

    private MediaPlayer introAudioPlayer, startAudioPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.app_icon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(this::showRules);
        introAudioPlayer = MediaPlayer.create(this, R.raw.intro);
        startAudioPlayer = MediaPlayer.create(this, R.raw.start);
        introAudioPlayer.start();
        introAudioPlayer.setLooping(true);
    }

    /**
     * Start button event handler
     */
    public void showRules(View view) {
        Intent intent = new Intent(this, RulesActivity.class);

        intent.putExtra("rule1", "Get to 50 points.");
        intent.putExtra("rule2", "Moves your character.");
        intent.putExtra("rule3", "Gives you one more life.");
        intent.putExtra("rule4", "Doubles score for 10 seconds.");
        intent.putExtra("rule5", "Slows bugs for 10 seconds.");
        intent.putExtra("rule6", "Shields you for 10 seconds.");
        intent.putExtra("select_player", "Select your player");

        introAudioPlayer.stop();
        startAudioPlayer.start();

        startActivity(intent);
    }
}
