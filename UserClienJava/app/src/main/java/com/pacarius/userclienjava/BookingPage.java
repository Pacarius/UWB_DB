package com.pacarius.userclienjava;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.w3c.dom.Text;

public class BookingPage extends AppCompatActivity {
    ApiHandler handler = MainActivity.handler;
    GifPlayer yipee;
    EditText[] editTexts = new EditText[5];
    int[] layoutIds = new int[]{R.id.streetNameField, R.id.coordinatesField, R.id.dateField, R.id.timeField, R.id.reasonField};
    int[] layoutParents = new int[]{R.id.tInputLayout1, R.id.tInputLayout2, R.id.tInputLayout3, R.id.tInputLayout4, R.id.tInputLayout5};
    Button bookingSubmit;
    TextView bookingResultText;
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
        yipee = findViewById(R.id.yipee);
        yipee.setVisibility(View.INVISIBLE);
        for(int i = 0; i < 5; i++){
            editTexts[i] = findViewById(layoutIds[i]);
        }
        bookingSubmit = findViewById(R.id.bookingSubmit);
        bookingSubmit.setOnClickListener(submitBooking);
        yipee.setVisibility(View.INVISIBLE);
        bookingResultText = findViewById(R.id.bookingResultText);
        bookingResultText.setVisibility(View.INVISIBLE);
    }
    private final View.OnClickListener submitBooking = view -> {
        handler.postNewBooking(
                ((EditText) findViewById(R.id.streetNameField)).getText().toString(),
                ((EditText) findViewById(R.id.coordinatesField)).getText().toString(),
                ((EditText) findViewById(R.id.dateField)).getText().toString(),
                ((EditText) findViewById(R.id.timeField)).getText().toString(),
                ((EditText) findViewById(R.id.reasonField)).getText().toString()
        );
        yipee.setVisibility(View.VISIBLE);
        for(int id: layoutParents){
            findViewById(id).setVisibility(View.INVISIBLE);
        }
        bookingSubmit.setVisibility(View.INVISIBLE);
        bookingResultText.setText(String.format("Booking submitted. You have booked:\nLocation: %s\nCoordinates: %s\nDate: %s\nTime: %s\nReason: %s\n It may take a while for the booking to process.",
                ((EditText) findViewById(R.id.streetNameField)).getText().toString(),
                ((EditText) findViewById(R.id.coordinatesField)).getText().toString(),
                ((EditText) findViewById(R.id.dateField)).getText().toString(),
                ((EditText) findViewById(R.id.timeField)).getText().toString(),
                ((EditText) findViewById(R.id.reasonField)).getText().toString()));
        bookingResultText.setVisibility(View.VISIBLE);
    };
}