package com.pacarius.userclienjava;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;

public class BookingPage extends AppCompatActivity {
    ApiHandler handler = MainActivity.handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_booking_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.yipee).setVisibility(View.INVISIBLE);
        findViewById(R.id.bookingSubmit).setOnClickListener(view -> {
                    handler.postNewBooking(
                            ((EditText) findViewById(R.id.streetNameField)).getText().toString(),
                            ((EditText) findViewById(R.id.coordinatesField)).getText().toString(),
                            ((EditText) findViewById(R.id.reasonField)).getText().toString()
                    );
            findViewById(R.id.yipee).setVisibility(View.VISIBLE);
                }
        );
    }
}