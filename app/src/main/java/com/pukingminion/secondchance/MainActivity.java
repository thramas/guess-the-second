package com.pukingminion.secondchance;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int TARGET = 1000;
    private long currentTimeInMs;
    private long cumulativeOffset;
    private long currentCount = 0;
    private long previousTimeInMs;
    private long noOfPerfects;
    private double accuracy;
    private TextView offsetTv;
    private TextView counterTv;
    private TextView accuracyTv;
    private TextView perfectsTv;
    private RelativeLayout shareLayout;
    private LinearLayout introLayout;
    private LinearLayout resultLayout;
    private String mPath;
    private TextView introTv;
    private TextView resultsTv;
    private MediaPlayer mMediaPlayer;
    private View arenaView;
    private TextView startBtn;
    private TextView restartTv;
    private TextView shareBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initUiElements();
    }

    private void initUiElements() {
        arenaView = findViewById(R.id.arena_view);
        shareLayout = (RelativeLayout) findViewById(R.id.share_layout);
        introLayout = (LinearLayout) findViewById(R.id.intro_layout);
        resultLayout = (LinearLayout) findViewById(R.id.result_layout);
        offsetTv = (TextView) findViewById(R.id.difference_from_perfect);
        counterTv = (TextView) findViewById(R.id.counter_tv);
        introTv = (TextView) findViewById(R.id.intro_tv);
        resultsTv = (TextView) findViewById(R.id.results_tv);
        perfectsTv = (TextView) findViewById(R.id.total_perfects);
        accuracyTv = (TextView) findViewById(R.id.accuracy);
        shareBtn = (TextView) findViewById(R.id.share_btn);
        restartTv = (TextView) findViewById(R.id.restart_game);
        startBtn = (TextView) findViewById(R.id.start_button);
        setListeners();
        setTypefaces();
        resetGame();
    }

    private void setListeners() {
        arenaView.setOnClickListener(this);
        startBtn.setOnClickListener(this);
        shareBtn.setOnClickListener(this);
        restartTv.setOnClickListener(this);
    }

    private void resetGame() {
        shareLayout.setVisibility(View.VISIBLE);
        resultLayout.setVisibility(View.GONE);
        introLayout.setVisibility(View.VISIBLE);
        counterTv.setVisibility(View.GONE);
        offsetTv.setVisibility(View.GONE);
        currentTimeInMs = 0;
        resetScore();
        play("banana.mp3");
    }

    private void setTypefaces() {
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Amatic-Bold.ttf");
        Typeface cfNew = Typeface.createFromAsset(getAssets(), "fonts/Lobster_1.3.otf");

        Typeface btnFont = Typeface.createFromAsset(getAssets(), "fonts/ostrich-regular.ttf");
        shareBtn.setTypeface(custom_font);
        restartTv.setTypeface(custom_font);
        startBtn.setTypeface(custom_font);


        counterTv.setTypeface(cfNew);
        counterTv.setTextSize(150);

        offsetTv.setTypeface(custom_font);
        perfectsTv.setTypeface(custom_font);
        perfectsTv.setTextSize(50);
        accuracyTv.setTypeface(custom_font);
        accuracyTv.setTextSize(50);
        introTv.setTypeface(custom_font);
        introTv.setTextSize(50);
        resultsTv.setTypeface(custom_font);
        resultsTv.setTextSize(50);
        offsetTv.setTextSize(50);
    }

    private void play(String fileName) {
        try {
            AssetFileDescriptor descriptor = getAssets().openFd(fileName);
            long start = descriptor.getStartOffset();
            long end = descriptor.getLength();
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(descriptor.getFileDescriptor(), start, end);
            mMediaPlayer.prepare();
            mMediaPlayer.setVolume(1.0f, 1.0f);
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.arena_view:
                currentTimeInMs = System.currentTimeMillis();
                if (currentCount > 0) {
                    cumulativeOffset += currentTimeInMs - previousTimeInMs - TARGET;
                    changeScore();
                    showLayout(R.id.counter_tv);
                } else {
                    calculateScore();
                    showLayout(R.id.result_layout);
                }
                play("tick.mp3");
                break;
            case R.id.start_button:
                previousTimeInMs = System.currentTimeMillis();
                if (mMediaPlayer != null) {
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                }
                showLayout(R.id.counter_tv);
                break;
            case R.id.share_btn:
                new TakeScreenshotTask().execute();
                break;
            case R.id.restart_game:
                resetGame();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mMediaPlayer != null) {
            if(mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
            } else {
                mMediaPlayer.stop();
                mMediaPlayer.start();
            }
        }
    }

    private String takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            Bitmap bitmap = getScreenShot(shareLayout);
            mPath = store(bitmap, String.valueOf(System.currentTimeMillis()));
        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            e.printStackTrace();
        }
        return mPath;
    }

    public Bitmap getScreenShot(View view) {
        View screenView = view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        return bitmap;
    }


    public String store(Bitmap bm, String fileName) {
        final String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Screenshots";
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dirPath, fileName);
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    private void changeScore() {
        currentCount--;
        counterTv.setText(String.valueOf(currentCount));
        long offset = currentTimeInMs - previousTimeInMs - TARGET;
        if (Math.abs(offset) <= 30) {
            offsetTv.setText("Perfect!!!");
            noOfPerfects++;
        } else {
            offsetTv.setText(String.valueOf(offset));
        }

        previousTimeInMs = currentTimeInMs;
    }

    private void resetScore() {
        currentCount = 10;
        cumulativeOffset = 0;
        previousTimeInMs = 0;
        noOfPerfects = 0;
        accuracy = 0.0;
        counterTv.setText(String.valueOf(currentCount));
    }

    private void calculateScore() {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        accuracy = (1 - (Math.abs(cumulativeOffset) / (TARGET * 10.0))) * 100;
        perfectsTv.setText("Perfects : " + String.valueOf(noOfPerfects));
        accuracyTv.setText("Accuracy : " + String.valueOf(df.format(accuracy) + "%"));
    }

    private void showLayout(int id) {
        switch (id) {
            case R.id.counter_tv:
                shareLayout.setVisibility(View.GONE);
                counterTv.setVisibility(View.VISIBLE);
                offsetTv.setVisibility(currentCount == 10 ? View.GONE : View.VISIBLE);
                ObjectAnimator animator = getObjectAnimator(counterTv, 1, 1.05f, 500);
                ObjectAnimator animatorTwo = getObjectAnimator(counterTv, 1.05f, 1, 500);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playSequentially(animator, animatorTwo);
                animatorSet.start();
                break;
            case R.id.result_layout:
                shareLayout.setVisibility(View.VISIBLE);
                introLayout.setVisibility(View.GONE);
                resultLayout.setVisibility(View.VISIBLE);
                counterTv.setVisibility(View.GONE);
                offsetTv.setVisibility(View.GONE);
                break;
            case R.id.intro_layout:
                shareLayout.setVisibility(View.VISIBLE);
                introLayout.setVisibility(View.VISIBLE);
                resultLayout.setVisibility(View.GONE);
                counterTv.setVisibility(View.GONE);
                offsetTv.setVisibility(View.GONE);

        }
    }

    private ObjectAnimator getObjectAnimator(final View imageView, float originalScale, float newScale, int duration) {
        PropertyValuesHolder zoomX = PropertyValuesHolder.ofFloat(View.SCALE_X, originalScale, newScale);
        PropertyValuesHolder zoomY = PropertyValuesHolder.ofFloat(View.SCALE_Y, originalScale, newScale);
        ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(imageView, zoomX, zoomY);
        animation.setDuration(duration);
        return animation;
    }

    private class TakeScreenshotTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            takeScreenshot();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/png");
            share.putExtra(Intent.EXTRA_STREAM, Uri.parse(mPath));
            startActivity(Intent.createChooser(share, "Share Image"));
            super.onPostExecute(aVoid);
        }
    }
}
