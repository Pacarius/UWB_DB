package com.example.userclientjava;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class MapTestPlot extends View {
    public MapTestPlot(Context context) throws MalformedURLException {
        super(context);
        Grid[10][17] = CordType.Lamppost;
        Grid[46][17] = CordType.Lamppost;
        handler.addListener(() -> {
            for(CordType[] i : Grid){
                for(CordType j : i){
                    j = CordType.Empty;
                }
            }
            for(Coordinates i : vehicles){
                Grid[i.getX()][i.getY()] = i.getType();
            }
            for(Coordinates i : lampposts){
                Grid[i.getX()][i.getY()] = i.getType();
            }
            redraw();
        });
    }
    ImageView imageView;
    int[] pngSize = {1670, 868};
    int[] Vecs = {53, 28};
    int[] imgPos;
    CordType[][] Grid = new CordType[Vecs[0]][Vecs[1]];
    List<Coordinates> vehicles = new ArrayList<>();
    List<Coordinates> lampposts = new ArrayList<>();

    public ApiHandler getApiHandler() {
        return handler;
    }

    ApiHandler handler = new ApiHandler(vehicles, lampposts);
    float[] getSize(){
        return new float[]{(float)pngSize[0], (float)pngSize[1]};
    }
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

    }
    public void redraw() {invalidate();}
    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
        getDimensions();
    }
    private void getDimensions() {
        Drawable drawable = imageView.getDrawable();
        if (drawable != null) {
            int intrinsicWidth = drawable.getIntrinsicWidth();
            int intrinsicHeight = drawable.getIntrinsicHeight();
            int imageViewWidth = imageView.getWidth() - imageView.getPaddingLeft() - imageView.getPaddingRight();
            int imageViewHeight = imageView.getHeight() - imageView.getPaddingTop() - imageView.getPaddingBottom();
            float scaleFactor = Math.min(
                    (float) imageViewWidth / intrinsicWidth,
                    (float) imageViewHeight / intrinsicHeight
            );
            int imageWidth = (int) (intrinsicWidth * scaleFactor);
            int imageHeight = (int) (intrinsicHeight * scaleFactor);
            int imageLeft = (int) ((imageViewWidth - imageWidth) / 2f) + imageView.getPaddingLeft();
            int imageTop = (int) ((imageViewHeight - imageHeight) / 2f) + imageView.getPaddingTop();
            setDimensions(new Pair<>(imageWidth, imageHeight));
            setPosition(new Pair<>(imageLeft, imageTop));
        }
    }
    private void setDimensions(Pair<Integer, Integer> a) {
        int[] tmp = new int[]{a.first, a.second};
        pngSize[0] = Math.max(tmp[0], tmp[1]);
        pngSize[1] = Math.min(tmp[0], tmp[1]);
    }

    private void setPosition(Pair<Integer, Integer> a) {
        imgPos = new int[]{a.first, a.second};
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = getHeight() - event.getY(); // Flip the y-coordinate

            // Calculate the grid cell that the user has tapped on
            int gridX = (int) ((x - imgPos[0]) / (pngSize[0] / (float) Vecs[0]));
            int gridY = (int) ((y - imgPos[1]) / (pngSize[1] / (float) Vecs[1]));

            // Check if the tap event is within the grid
            if (gridX >= 0 && gridX < Vecs[0] && gridY >= 0 && gridY < Vecs[1]) {
                CordType cellContent = Grid[gridX][gridY];
                String tmp = "Square at: (" + gridX + ", " + gridY + ") contains: ";
                if(cellContent != CordType.Empty){
                    Toast.makeText(getContext(), tmp + cellContent, Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getContext(), tmp + "Nothing", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        }
        return super.onTouchEvent(event);
    }
}
