package com.uniquepaybd.uniquepay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        // Load the animation
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.home_screen);

        // Find the animation view
        ImageView splashAnimation = findViewById(R.id.splash_animation);

        // Start the animation
        splashAnimation.startAnimation(animation);

        // Set a listener for when the animation finishes
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (isLoggedIn()) {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                }
                // After the animation finishes, navigate to the main activity

                finish(); // Finish the splash screen activity
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
            private boolean isLoggedIn() {
                String deviceKey = LoginManager.getUserEmail(SplashActivity.this);
                String uid = LoginManager.getSavedUid(SplashActivity.this);
                if (!deviceKey.isEmpty() && !uid.isEmpty()) {
                    return true;
                } else {
                    return false;
                }
            }
        });
    }
}
