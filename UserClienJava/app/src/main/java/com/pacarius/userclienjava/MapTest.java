package com.pacarius.userclienjava;

import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MapTest extends AppCompatActivity {
    ImageView iv;
    MapTestPlot mv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map_test);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        iv = findViewById(R.id.mapImage);
        iv.setImageDrawable(getDrawable(R.drawable.street));
        iv.requestLayout();
        mv = findViewById(R.id.mapTestPlotView);
        mv.setImageView(iv);
        setIp();
        mv.redraw();
    }

    private void setIp() {
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Change IP")
                .setMessage("Enter the new IP address:")
                .setView(input)
                .setPositiveButton("OK", (dialog1, which) -> {
                    String newIp = input.getText().toString();
                    mv.handler().setIp(newIp);
                })
                .setNegativeButton("Cancel", null)
                .create();

        dialog.show();
    }
}