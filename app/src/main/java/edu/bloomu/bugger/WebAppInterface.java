package edu.bloomu.bugger;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.JavascriptInterface;
import android.widget.ImageButton;
import com.erz.joysticklibrary.JoyStick;

/**
 * An interface that allows Javascript code to call Android methods
 */
public class WebAppInterface {
    private Context mContext;
    private Activity activity;

    /**
     * Instantiate the interface and set the context and activity
      */
    WebAppInterface(Context c) {
        mContext = c;
        activity = (Activity) mContext;
    }

    /**
     *  Highlight a specific gem power-up
     *  Called in bonus() of app.js
     */
    @JavascriptInterface
    public void highlightGem(String gem) {
        activity.runOnUiThread(() -> {
            ImageButton imageButton = null;
            Animation ANIMATION_SHAKE = AnimationUtils.loadAnimation(mContext, R.anim.shake);

            switch(gem) {
                case "green":
                    imageButton =  activity.findViewById(R.id.green_gem_button);
                    break;
                case "blue":
                    imageButton =  activity.findViewById(R.id.blue_gem_button);
                    break;
                case "orange":
                    imageButton =  activity.findViewById(R.id.orange_gem_button);
                    break;
            }

            if (imageButton != null) {
                imageButton.startAnimation(ANIMATION_SHAKE);
            }
        });
    }

    /**
     * Disables the joystick
     * Called in gameOver() of app.js
     */
    @JavascriptInterface
    public void disableJoyStick() {
        activity.runOnUiThread(() -> {
            JoyStick joyStick = activity.findViewById(R.id.joy_stick);
            joyStick.setListener(null);
        });
    }

    /**
     * Ensures all gem buttons are reset
     * Called in gameOver() of app.js)
      */
    @JavascriptInterface
    public void resetGems() {
        activity.runOnUiThread(() -> {
            ImageButton green_gem_button = activity.findViewById(R.id.green_gem_button);
            ImageButton blue_gem_button = activity.findViewById(R.id.blue_gem_button);
            ImageButton orange_gem_button = activity.findViewById(R.id.orange_gem_button);
            green_gem_button.clearAnimation();
            blue_gem_button.clearAnimation();
            orange_gem_button.clearAnimation();
        });
    }

    /**
     * Plays water audio
     * Called in player.update(dt) of app.js
     */
    @JavascriptInterface
    public void startWaterAudio() {
        MediaPlayer waterAudioPlayer = MediaPlayer.create(mContext, R.raw.water);
        waterAudioPlayer.start();
    }

    /**
     * Plays gem audio
     * Called in gem.update(dt) of app.js
     */
    @JavascriptInterface
    public void startGemAudio() {
        MediaPlayer gemAudioPlayer = MediaPlayer.create(mContext, R.raw.gem);
        gemAudioPlayer.start();
    }

    /**
     * Plays hit audio
     * Called in enemy.update(dt) of app.js
     */
    @JavascriptInterface
    public void startHitAudio() {
        MediaPlayer hitAudioPlayer = MediaPlayer.create(mContext, R.raw.hit);
        hitAudioPlayer.start();
    }

    /**
     * Plays speed-up audio
     * Called in slowEnemiesBonus() of app.js
     */
    @JavascriptInterface
    public void startSpeedUpAudio() {
        MediaPlayer speedUpAudioPlayer = MediaPlayer.create(mContext, R.raw.speed_up);
        speedUpAudioPlayer.start();
    }

    /**
     * Plays game-over audio
     * Called in gameOver() of app.js
     */
    @JavascriptInterface
    public void startGameOverAudio() {
        MediaPlayer gameOverAudioPlayer = MediaPlayer.create(mContext, R.raw.gameover);
        gameOverAudioPlayer.start();
    }

    /**
     * Plays win audio
     * Called in gameOver() of app.js
     */
    @JavascriptInterface
    public void startWinAudio() {
        MediaPlayer winAudioPlayer = MediaPlayer.create(mContext, R.raw.win);
        winAudioPlayer.start();
    }
}
