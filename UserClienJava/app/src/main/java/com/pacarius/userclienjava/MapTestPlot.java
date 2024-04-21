package com.pacarius.userclienjava;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MapTestPlot extends View {
    private ImageView imageView;
    private int[] pngSize = new int[]{1670, 868};
    private int[] Vecs = new int[]{53, 28};
    private int[] imgPos;
    private CoordType[][] Grid = new CoordType[Vecs[0]][Vecs[1]];
    private List<Coordinates> vehicles = new ArrayList<>();
    private List<Coordinates> lampposts = new ArrayList<>();
    private ApiHandler handler = new ApiHandler(lampposts, vehicles);
    Pair<Float, Float> Size = new Pair<>(pngSize[0] / (float) Vecs[0], pngSize[1] / (float) Vecs[1]);
    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        getDimensions();
        drawAxes(canvas);
        drawSquares(canvas);
    }
    public MapTestPlot(Context context) {
        super(context);
        init();
    }
    public MapTestPlot(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public MapTestPlot(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init() {
        for (int i = 0; i < Grid.length; i++) {
            for (int j = 0; j < Grid[i].length; j++) {
                Grid[i][j] = CoordType.Empty;
            }
        }
        handler.setOnApiFire(() -> {
            for (int i = 0; i < Grid.length; i++) {
                for (int j = 0; j < Grid[i].length; j++) {
                    Grid[i][j] = CoordType.Empty;
                }
            }
            for (Coordinates coord : lampposts) {
                Grid[coord.getX()][coord.getY()] = CoordType.Lamppost;
            }
            for (Coordinates coord : vehicles) {
                Grid[coord.getX()][coord.getY()] = CoordType.Vehicle;
            }
            redraw();
        });
    }

    public void redraw() {
        invalidate(); // Redraw the view
    }
    public ApiHandler handler() {
        return handler;
    }
    private void getDimensions() {
        Drawable drawable = imageView.getDrawable();
        if (drawable != null) {
            int intrinsicWidth = drawable.getIntrinsicWidth();
            int intrinsicHeight = drawable.getIntrinsicHeight();
            int imageViewWidth = imageView.getWidth() - imageView.getPaddingLeft() - imageView.getPaddingRight();
            int imageViewHeight = imageView.getHeight() - imageView.getPaddingTop() - imageView.getPaddingBottom();
            float scaleFactor = Math.min(
                    imageViewWidth / (float) intrinsicWidth,
                    imageViewHeight / (float) intrinsicHeight
            );
            int imageWidth = (int) (intrinsicWidth * scaleFactor);
            int imageHeight = (int) (intrinsicHeight * scaleFactor);
            int imageLeft = (imageViewWidth - imageWidth) / 2 + imageView.getPaddingLeft();
            int imageTop = (imageViewHeight - imageHeight) / 2 + imageView.getPaddingTop();
            setDimensions(new int[]{imageWidth, imageHeight});
            setPosition(new int[]{imageLeft, imageTop});
        }
    }

    private void setDimensions(int[] a) {
        pngSize[0] = Math.max(a[0], a[1]);
        pngSize[1] = Math.min(a[0], a[1]);
    }

    private void setPosition(int[] a) {
        imgPos = a;
    }

    private void drawAxes(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(2f);
        float padding = paint.getStrokeWidth() / 2;

        for (int x = 0; x < Vecs[0] + 1; x++) {
            for (int y = 0; y < Vecs[1] + 1; y++) {
                int posX = imgPos[0] + x * pngSize[0] / Vecs[0];
                int posY = imgPos[1] + y * pngSize[1] / Vecs[1];
                if (x < Vecs[0]) {
                    canvas.drawLine(posX, posY, posX + pngSize[0] / Vecs[0], posY, paint);
                }
                if (y < Vecs[1]) {
                    canvas.drawLine(posX, posY, posX, posY + pngSize[1] / Vecs[1], paint);
                }
            }
        }
    }

    private void drawSquares(Canvas canvas) {
        for (int x = 0; x < Grid.length; x++) {
            for (int y = 0; y < Grid[x].length; y++) {
                if (Grid[x][y] != CoordType.Empty) {
                    int color;
                    switch (Grid[x][y]) {
                        case Lamppost:
                            color = Color.RED;
                            break;
                        case Vehicle:
                            color = Color.CYAN;
                            break;
                        default:
                            color = Color.WHITE;
                            break;
                    }
                    float left = imgPos[0] + x * pngSize[0] / (float) Vecs[0];
                    float top = imgPos[1] + (Vecs[1] - y + 1) * pngSize[1] / (float) Vecs[1];
                    float right = imgPos[0] + (x + 1) * pngSize[0] / (float) Vecs[0];
                    float bottom = imgPos[1] + (Vecs[1] - y) * pngSize[1] / (float) Vecs[1];
                    Paint paint = new Paint();
                    paint.setColor(color);
                    canvas.drawRect(left, top, right, bottom, paint);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = getHeight() - event.getY();

            int gridX = (int) ((x - imgPos[0]) / (pngSize[0] / (float) Vecs[0]));
            int gridY = (int) ((y - imgPos[1]) / (pngSize[1] / (float) Vecs[1]));

            if (gridX >= 0 && gridX < Vecs[0] && gridY >= 0 && gridY < Vecs[1]) {
                CoordType cellContent = Grid[gridX][gridY];
                String tmp = "Square at: (" + gridX + ", " + gridY + ") contains: ";
                if (cellContent != CoordType.Empty) {
                    Toast.makeText(getContext(), tmp + cellContent, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), tmp + "Nothing", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    public void setImageView(ImageView iv) {
        imageView = iv;
    }
}
