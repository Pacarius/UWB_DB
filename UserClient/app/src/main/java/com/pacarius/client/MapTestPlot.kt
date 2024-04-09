package com.pacarius.client

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.media.Image
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import kotlin.math.min

class MapTestPlot : View {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )
    public var imageView: ImageView? = null
    private val pngSize = intArrayOf(1670, 868)
    private val Vecs = intArrayOf(53, 28)
    private var imgPos: IntArray? = null
    public val Grid = Array(Vecs[0]) { Array(Vecs[1]) { CoordType.Empty } }
    val vehicles = mutableListOf<Coordinates>()
    val lampposts = mutableListOf<Coordinates>()
    public val handler = ApiHandler(lampposts, vehicles)
    val Size
        get() = floatArrayOf(pngSize[0] / Vecs[0].toFloat(), pngSize[1] / Vecs[1].toFloat())
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        getDimensions()
        drawAxes(canvas)
        drawSquares(canvas)
    }
    init{
        Grid[10][17] = CoordType.Lamppost
        Grid[46][17] = CoordType.Lamppost
        handler.OnApiFire = {
            for (i in Grid) {
                for (index in i.indices) {
                    i[index] = CoordType.Empty
                }
            }
            for (i in lampposts) {
                Grid[i.x][i.y] = CoordType.Lamppost
            }
            for (i in vehicles) {
                Grid[i.x][i.y] = CoordType.Vehicle
            }
            redraw()
        }
    }
    fun redraw() {
        invalidate() // Redraw the view
    }
    private fun getDimensions() {
        val drawable = imageView!!.drawable
        drawable.let {
            val intrinsicWidth = it.intrinsicWidth
            val intrinsicHeight = it.intrinsicHeight
            val imageViewWidth =
                imageView!!.width - imageView!!.paddingLeft - imageView!!.paddingRight
            val imageViewHeight =
                imageView!!.height - imageView!!.paddingTop - imageView!!.paddingBottom
            val scaleFactor = min(
                imageViewWidth / intrinsicWidth.toFloat(),
                imageViewHeight / intrinsicHeight.toFloat()
            )
            val imageWidth = intrinsicWidth * scaleFactor
            val imageHeight = intrinsicHeight * scaleFactor
            val imageLeft = (imageViewWidth - imageWidth) / 2 + imageView!!.paddingLeft
            val imageTop = (imageViewHeight - imageHeight) / 2 + imageView!!.paddingTop
            setDimensions(Pair(imageWidth.toInt(), imageHeight.toInt()))
            setPosition(Pair(imageLeft.toInt(), imageTop.toInt()))
        }
    }
    private fun setDimensions(a: Pair<Int, Int>) {
        val tmp = intArrayOf(a.first, a.second)
        pngSize[0] = tmp.max()
        pngSize[1] = tmp.min()
    }
    private fun setPosition(a: Pair<Int, Int>) {
        val tmp = intArrayOf(a.first, a.second)
        imgPos = tmp
    }
    private fun drawAxes(canvas: Canvas) {
    val paint = Paint()
    paint.color = Color.BLACK
    paint.strokeWidth = 2f
    // Padding to ensure lines are fully visible
    val padding = paint.strokeWidth / 2

    // Draw axes at the edges of each grid cell
    for (x in 0 until Vecs[0] + 1) {
        for (y in 0 until Vecs[1] + 1) {
            val posX = imgPos!![0] + x * pngSize[0] / Vecs[0]
            val posY = imgPos!![1] + y * pngSize[1] / Vecs[1]
            if (x < Vecs[0]) {
                // Draw horizontal grid lines
                canvas.drawLine(posX.toFloat(), posY.toFloat(),
                    (posX + pngSize[0] / Vecs[0]).toFloat(), posY.toFloat(), paint)
            }
            if (y < Vecs[1]) {
                // Draw vertical grid lines
                canvas.drawLine(posX.toFloat(), posY.toFloat(),
                    posX.toFloat(), (posY + pngSize[1] / Vecs[1]).toFloat(), paint)
            }
        }
    }
}
    private fun drawSquares(canvas: Canvas){
    for((x, i)in Grid.withIndex()){
        for((y, j)in i.withIndex()){
            if(j != CoordType.Empty){
                val color = when(j){
                    CoordType.Lamppost -> Color.RED
                    CoordType.Vehicle -> Color.CYAN
                    else -> Color.WHITE
                }
                val left = imgPos!![0] + x * pngSize[0] / Vecs[0].toFloat()
                val top = imgPos!![1] + (Vecs[1] - y + 1) * pngSize[1] / Vecs[1].toFloat() // Invert the y-coordinate
                val right = imgPos!![0] + (x + 1) * pngSize[0] / Vecs[0].toFloat()
                val bottom = imgPos!![1] + (Vecs[1] - y) * pngSize[1] / Vecs[1].toFloat() // Invert the y-coordinate
                val paint = Paint()
                paint.color = color
                canvas.drawRect(
                    left,
                    top,
                    right,
                    bottom,
                    paint
                )
            }
        }
    }
}
    override fun onTouchEvent(event: MotionEvent): Boolean {
    if (event.action == MotionEvent.ACTION_DOWN) {
        val x = event.x
        val y = height - event.y // Flip the y-coordinate

        // Calculate the grid cell that the user has tapped on
        val gridX = ((x - imgPos!![0]) / (pngSize[0] / Vecs[0])).toInt()
        val gridY = ((y - imgPos!![1]) / (pngSize[1] / Vecs[1])).toInt()

        // Check if the tap event is within the grid
        if (gridX in 0 until Vecs[0] && gridY in 0 until Vecs[1]) {
            val cellContent = Grid[gridX][gridY]
            val tmp = "Square at: ($gridX, $gridY) contains: "
            if(cellContent != CoordType.Empty){
                Toast.makeText(context, tmp + cellContent, Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(context, tmp + "Nothing", Toast.LENGTH_SHORT).show()
            }
            return true
        }
    }
    return super.onTouchEvent(event)
}
}