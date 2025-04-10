package com.example.electricianapp.presentation.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.electricianapp.presentation.viewmodel.lightinglayout.SpacingResult
import kotlin.math.min

class LightingLayoutView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var spacingResult: SpacingResult? = null
    private var roomLengthFt: Double = 0.0
    private var roomWidthFt: Double = 0.0

    private val roomPaint = Paint().apply {
        color = Color.DKGRAY
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }

    private val luminairePaint = Paint().apply {
        color = Color.YELLOW
        style = Paint.Style.FILL
    }

    private val luminaireRadius = 10f // Radius for drawing luminaires

    // Function to update the view with new calculation results
    fun updateLayout(result: SpacingResult?, lengthFt: Double?, widthFt: Double?) {
        this.spacingResult = result
        this.roomLengthFt = lengthFt ?: 0.0
        this.roomWidthFt = widthFt ?: 0.0
        invalidate() // Request redraw
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val currentResult = spacingResult ?: return // Don't draw if no result
        if (roomLengthFt <= 0 || roomWidthFt <= 0) return // Don't draw if invalid dimensions

        // --- Calculate drawing parameters ---
        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()
        val padding = 30f // Padding around the drawing

        // Determine the scale factor to fit the room within the view bounds
        val availableWidth = viewWidth - 2 * padding
        val availableHeight = viewHeight - 2 * padding
        val scaleX = availableWidth / roomLengthFt.toFloat()
        val scaleY = availableHeight / roomWidthFt.toFloat()
        val scale = min(scaleX, scaleY) // Use the smaller scale to fit both dimensions

        // Calculate the actual drawing dimensions based on scale
        val drawWidth = roomLengthFt.toFloat() * scale
        val drawHeight = roomWidthFt.toFloat() * scale

        // Calculate top-left corner coordinates to center the drawing
        val drawStartX = padding + (availableWidth - drawWidth) / 2f
        val drawStartY = padding + (availableHeight - drawHeight) / 2f

        // --- Draw Room Outline ---
        canvas.drawRect(drawStartX, drawStartY, drawStartX + drawWidth, drawStartY + drawHeight, roomPaint)

        // --- Draw Luminaires ---
        if (currentResult.numCols > 0 && currentResult.numRows > 0) {
            for (row in 0 until currentResult.numRows) {
                for (col in 0 until currentResult.numCols) {
                    // Calculate center coordinates for each luminaire dot
                    val centerX = drawStartX + (currentResult.borderSpacingLength + col * currentResult.spacingLength).toFloat() * scale
                    val centerY = drawStartY + (currentResult.borderSpacingWidth + row * currentResult.spacingWidth).toFloat() * scale

                    canvas.drawCircle(centerX, centerY, luminaireRadius, luminairePaint)
                }
            }
        }
    }
}
