package com.pacarius.userclienjava;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

import java.io.InputStream;

public class GifPlayer extends View {

    private Movie movie;
    private long movieStart;

    public GifPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeView();
    }

    private void initializeView() {
        @SuppressLint("ResourceType") InputStream is = getContext().getResources().openRawResource(R.drawable.yipee1080);
        movie = Movie.decodeStream(is);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        long now = android.os.SystemClock.uptimeMillis();
        if (movieStart == 0) {
            movieStart = now;
        }
        if (movie != null) {
            int relTime = (int)((now - movieStart) % movie.duration());
            movie.setTime(relTime);
            movie.draw(canvas, 0, 0);
            this.invalidate();
        }
    }
}
