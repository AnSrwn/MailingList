package com.example.mailinglist.ui.shared

import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.max
import kotlin.math.min

class SnapToPositionHelper(private val onSnapped: ((Int) -> Unit)? = null) : LinearSnapHelper() {
    var snappedPosition = 0
    private var snapToNext = false
    private var snapToPrevious = false
    var recyclerView: RecyclerView? = null

    override fun attachToRecyclerView(recyclerView: RecyclerView?) {
        super.attachToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun findTargetSnapPosition(
        layoutManager: RecyclerView.LayoutManager,
        velocityX: Int,
        velocityY: Int
    ): Int {
        if (velocityX < 0)
            snapToPrevious = true
        else
            snapToNext = true

        when {
            snapToNext -> {
                snapToNext = false
                snappedPosition =
                    min(recyclerView?.adapter?.itemCount ?: 0, snappedPosition + 1)
            }
            snapToPrevious -> {
                snapToPrevious = false
                snappedPosition = max(0, snappedPosition - 1)
            }
            else -> {
                snappedPosition = super.findTargetSnapPosition(layoutManager, velocityX, velocityY)
            }
        }

        onSnapped?.invoke(snappedPosition)
        return snappedPosition
    }
}
