package com.pacarius.client

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import kotlin.random.Random

class CoordinateView : View {
    private val paint = Paint()
    private var points: List<Pair<Float, Float>> = emptyList()
    private val squares = mutableListOf<RectF>()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun updatePoints(newPoints: List<Pair<Float, Float>>) {
        points = newPoints
        invalidate() // Redraw the view
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw x and y axes
        paint.color = Color.BLACK
        paint.strokeWidth = 2f
        canvas.drawLine(0f, height / 2f, width.toFloat(), height / 2f, paint) // x-axis
        canvas.drawLine(width / 2f, 0f, width / 2f, height.toFloat(), paint) // y-axis

        // Draw points
        paint.color = Color.RED
        paint.style = Paint.Style.FILL
        for (point in points) {
            canvas.drawCircle(point.first, point.second, 5f, paint)
        }

        // Draw 10 squares at random positions
        paint.color = Color.GREEN
        squares.clear()
        for (i in 1..10) {
            val x = Random.nextFloat() * width
            val y = Random.nextFloat() * height
            val squareSize = 50f // size of the square
            val rect = RectF(x, y, x + squareSize, y + squareSize)
            squares.add(rect)
            canvas.drawRect(rect, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val x = event.x
            val y = event.y
            for (square in squares) {
                if (square.contains(x, y)) {
                    Toast.makeText(context, "Square at: (${square.left}, ${square.top})", Toast.LENGTH_SHORT).show()
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
    }
}