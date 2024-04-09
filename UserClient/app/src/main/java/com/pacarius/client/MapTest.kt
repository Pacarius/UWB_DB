package com.pacarius.client

import android.os.Bundle
import android.text.InputType
import android.view.ViewTreeObserver
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MapTest : AppCompatActivity() {
    lateinit var iv: ImageView
    lateinit var mv: MapTestPlot
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_map_test)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        iv = findViewById(R.id.mapImage)
        iv.setImageDrawable(getDrawable(R.drawable.street))
        iv.requestLayout()
        mv = findViewById(R.id.mapTestPlotView)
        mv.imageView = iv
        setIp()
        mv.redraw()
    }
    private fun setIp(){
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT

        // Create an AlertDialog
        val dialog = AlertDialog.Builder(this)
            .setTitle("Change IP")
            .setMessage("Enter the new IP address:")
            .setView(input)
            .setPositiveButton("OK") { _, _ ->
                // Get the text entered by the user
                val newIp = input.text.toString()

                // Pass the new IP address to the MapTestPlot view
                mv.handler.ip = newIp
            }
            .setNegativeButton("Cancel", null)
            .create()

        // Show the dialog
        dialog.show()
    }
}