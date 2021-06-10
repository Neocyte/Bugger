package edu.bloomu.bugger;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class RulesActivity extends AppCompatActivity {

    private TextView rulesTextView, selectPlayerTextView;
    private MediaPlayer rulesAudioPlayer, playAudioPlayer, selectAudioPlayer;
    private ImageButton[] characters;
    private String selectedCharacter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_rules);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.app_icon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // set the text for the rules and character selection
        rulesTextView = findViewById(R.id.rules_textview);
        selectPlayerTextView = findViewById(R.id.select_player_textview);
        setText(Objects.requireNonNull(getIntent().getExtras()));

        // add play button and audio listeners
        Button playButton = findViewById(R.id.play_button);
        playButton.setOnClickListener(this::play);
        playAudioPlayer = MediaPlayer.create(this, R.raw.play);
        rulesAudioPlayer = MediaPlayer.create(this, R.raw.rules);
        selectAudioPlayer = MediaPlayer.create(this, R.raw.select);
        rulesAudioPlayer.start();
        rulesAudioPlayer.setLooping(true);

        // add character selection listeners
        ImageButton char_boy = findViewById(R.id.select_char_boy);
        ImageButton char_cat_girl = findViewById(R.id.select_char_cat_girl);
        ImageButton char_horn_girl = findViewById(R.id.select_char_horn_girl);
        ImageButton char_pink_girl = findViewById(R.id.select_char_pink_girl);
        ImageButton char_princess_girl = findViewById(R.id.select_char_princess_girl);
        char_boy.setBackground(this.getResources().getDrawable(R.drawable.selector));
        selectedCharacter = "./char_boy.png";

        characters = new ImageButton[] {
                char_boy,
                char_cat_girl,
                char_horn_girl,
                char_pink_girl,
                char_princess_girl
        };

        for (ImageButton character : characters) {
            character.setOnClickListener(this::highlightCharacter);
        }
    }

    /**
     * Play button handler
     */
    public void play(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("sprite", selectedCharacter); // pass sprite to main
        playAudioPlayer.start();
        rulesAudioPlayer.stop();
        startActivity(intent);
    }

    /**
     * Character selection handler
     */
    public void highlightCharacter(View view) {

        // highlight the selected character
        for (ImageButton character : characters)
        {
            character.setBackground(null);
        }
        view.setBackground(this.getResources().getDrawable(R.drawable.selector));
        selectAudioPlayer.start();

        // store the selected character's sprite
        switch (view.getId()) {
            case R.id.select_char_boy:
                selectedCharacter = "./char_boy.png";
                break;
            case R.id.select_char_cat_girl:
                selectedCharacter = "./char_cat_girl.png";
                break;
            case R.id.select_char_horn_girl:
                selectedCharacter = "./char_horn_girl.png";
                break;
            case R.id.select_char_pink_girl:
                selectedCharacter = "./char_pink_girl.png";
                break;
            case R.id.select_char_princess_girl:
                selectedCharacter = "./char_princess_girl.png";
        }
    }

    /**
     * Extracts and sets rules from the intent
     */
    private void setText(Bundle extras) {
        String r1 = extras.getString("rule1");
        String r2 = extras.getString("rule2");
        String r3 = extras.getString("rule3");
        String r4 = extras.getString("rule4");
        String r5 = extras.getString("rule5");
        String r6 = extras.getString("rule6");
        String rules = r1 + "\n" + r2 + "\n" + r3 + "\n" + r4 + "\n" + r5 + "\n" + r6;
        rulesTextView.setText(rules);

        String sp = extras.getString("select_player");
        selectPlayerTextView.setText(sp);
    }
}
