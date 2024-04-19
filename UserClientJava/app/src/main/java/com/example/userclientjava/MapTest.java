package com.example.userclientjava;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;

public class MapTest extends AppCompatActivity {
    ImageView iv;
    MapTestPlot mv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_map_test);

        iv = findViewById(R.id.mapImage);
        iv.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.street));
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
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newIp = input.getText().toString();
                        mv.getApiHandler().setIp(newIp);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();

        dialog.show();
    }
}