package edu.bloomu.bugger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.Toast;
import com.erz.joysticklibrary.JoyStick;
import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private JoyStick joyStick;
    private String selectedCharacter;
    private boolean music = true;
    private MediaPlayer gameAudioPlayer,
                        greenGemButtonAudioPlayer,
                        blueGemButtonAudioPlayer,
                        orangeGemButtonAudioPlayer;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.app_icon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Enable JS for the WebView
        webView = findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new WebAppInterface(this), "Android");

        // Have links open in the WebView instead of an external browser
        // Also, set the selected character when the WebView finishes loading
        webView.setWebViewClient(new WebViewClient(){
            public void onPageFinished(WebView view, String url){
                setPlayerSprite(Objects.requireNonNull(getIntent().getExtras()));
            }
        });

        // Load the WebView
        webView.loadUrl("file:///android_res/raw/index.html");

        // Set up joystick with snap-back and four axes
        joyStick = findViewById(R.id.joy_stick);
        joyStick.enableStayPut(false);
        joyStick.setType(JoyStick.TYPE_4_AXIS);

        // Schedule a thread that continually handles joystick movement
        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
        exec.scheduleAtFixedRate(() -> {
            // based on joystick direction, call handleJoystick() located in app.js
            switch(joyStick.getDirection()) {
                case 0:
                    webView.post(() -> webView.loadUrl("javascript:handleJoyStick('left')"));
                    break;
                case 4:
                    webView.post(() -> webView.loadUrl("javascript:handleJoyStick('right')"));
                    break;
                case 2:
                    webView.post(() -> webView.loadUrl("javascript:handleJoyStick('up')"));
                    break;
                case 6:
                    webView.post(() -> webView.loadUrl("javascript:handleJoyStick('down')"));
                    break;
            }
        }, 0, 50, TimeUnit.MILLISECONDS);

        // Add gem button listeners
        ImageButton green_gem_button = findViewById(R.id.green_gem_button);
        ImageButton blue_gem_button = findViewById(R.id.blue_gem_button);
        ImageButton orange_gem_button = findViewById(R.id.orange_gem_button);
        green_gem_button.setOnClickListener(this::greenBonus);
        blue_gem_button.setOnClickListener(this::blueBonus);
        orange_gem_button.setOnClickListener(this::orangeBonus);

        // Add audio players
        gameAudioPlayer = MediaPlayer.create(this, R.raw.game);
        greenGemButtonAudioPlayer = MediaPlayer.create(this, R.raw.gem_green_button);
        blueGemButtonAudioPlayer = MediaPlayer.create(this, R.raw.slow_down);
        orangeGemButtonAudioPlayer = MediaPlayer.create(this, R.raw.shield);
        gameAudioPlayer.start();
        gameAudioPlayer.setLooping(true);
    }

    /**
     * Green gem button event handler
     */
    private void greenBonus(View view) {
        if (view.getAnimation() != null) {
            view.clearAnimation();
            greenGemButtonAudioPlayer.start();
            webView.post(() -> webView.loadUrl("javascript:doubleScoreBonus()"));
            toast("DOUBLE SCORE");
        }
    }

    /**
     * Blue gem button event handler
     */
    private void blueBonus(View view) {
        if (view.getAnimation() != null) {
            view.clearAnimation();
            blueGemButtonAudioPlayer.start();
            webView.post(() -> webView.loadUrl("javascript:slowEnemiesBonus()"));
            toast("SLOW ENEMIES");
        }
    }

    /**
     * Orange gem button event handler
     */
    private void orangeBonus(View view) {
        if (view.getAnimation() != null) {
            view.clearAnimation();
            orangeGemButtonAudioPlayer.start();
            webView.post(() -> webView.loadUrl("javascript:gainInvincibilityBonus('"
                                                + selectedCharacter + "')"));
            toast("SHIELD");
        }
    }

    /**
     * Displays custom toast
     */
    public void toast(String msg) {
        Context context = getApplicationContext();
        SpannableStringBuilder biggerText = new SpannableStringBuilder(msg);
        biggerText.setSpan(new RelativeSizeSpan(2f), 0, msg.length(), 0);
        Toast toast = Toast.makeText(context, biggerText, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.TOP, 0, 0);
        toast.show();
    }

    /**
     * Extracts and sets the selected character
     */
    private void setPlayerSprite(Bundle extras) {
        selectedCharacter = extras.getString("sprite");
        webView.post(() -> webView.loadUrl("javascript:setPlayerSprite('"
                                           + selectedCharacter + "')"));
    }

    /**
     * Turns music on/off
     */
    public void toggleMusic(MenuItem item) {
        if (music) {
            music = false;
            gameAudioPlayer.pause();
            item.setIcon(ContextCompat.getDrawable(this, R.drawable.music_off));
        } else {
            music = true;
            gameAudioPlayer.start();
            item.setIcon(ContextCompat.getDrawable(this, R.drawable.music_on));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@SuppressWarnings("NullableProblems") MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }
}
