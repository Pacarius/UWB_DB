package com.pacarius.userclienjava;

import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{
    private final static List<Coordinates> vehicles = new ArrayList<>();
    private final static List<Coordinates> lampposts = new ArrayList<>();
    public static List<Coordinates> getVehicleArray(){ return vehicles;}
    public static List<Coordinates> getLamppostArray(){ return lampposts;}
    public final static ApiHandler handler = new ApiHandler(vehicles, lampposts);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.liveMapButton).setOnClickListener(view -> startActivity(new Intent(MainActivity.this, MapTest.class)));
        findViewById(R.id.vehicleInfoButton).setOnClickListener(view -> startActivity(new Intent(MainActivity.this, Default.class)));
        findViewById(R.id.settingsButton).setOnClickListener(view -> startActivity(new Intent(MainActivity.this, Default.class)));
        findViewById(R.id.bookingButton).setOnClickListener(view -> startActivity(new Intent(MainActivity.this, BookingPage.class)));
//        startActivity(new Intent(this, MapTest.class));
//        GridView mgv = findViewById(R.id.mainGridView);
//        mgv.setAdapter(
//                new SimpleAdapter(
//
//                )
//        );
//                .setOnClickListener(view -> startActivity(new Intent(MainActivity.this, MapTest.class)));
    }
}