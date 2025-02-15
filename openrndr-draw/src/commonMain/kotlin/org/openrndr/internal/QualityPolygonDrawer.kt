package org.openrndr.internal

import org.openrndr.draw.DrawContext
import org.openrndr.draw.DrawStyle
import org.openrndr.math.Vector2

class QualityPolygonDrawer {
    private val expansionDrawer = ExpansionDrawer()

    fun drawPolygon(drawContext: DrawContext,
                    drawStyle: DrawStyle,
                    loops: List<List<Vector2>>,
                    corners: List<List<Boolean>>,
                    fringeWidth: Double) {

        if (drawStyle.fill != null) {
            val path = Path.fromLineLoops(loops.mapIndexed { index, it ->
                it.let { it.subList(0, it.size) }
                    .let { if (index == 0) it else it }
            }, corners)
            val alpha = drawStyle.stroke?.a?:0.0
            val fw = if (drawStyle.smooth) { fringeWidth * 1.5 * (1.0-alpha) }
            else 0.0
            val fillExpansions = path.expandFill(fw, fw, drawStyle.lineJoin, 2.4)
            expansionDrawer.renderFill(drawContext, drawStyle, fillExpansions, path.convex, fw)
        }
    }

    fun drawPolygons(drawContext: DrawContext,
                     drawStyle: DrawStyle,
                     loops: List<List<List<Vector2>>>,
                     corners: List<List<List<Boolean>>>,
                     fringeWidth: Double = 1.0) {

        val fw = if (drawStyle.smooth) fringeWidth / 2.0 else 0.0
        if (drawStyle.fill != null) {
            val paths =
                    loops.mapIndexed { loopIndex, loop ->
                        Path.fromLineLoops(loop.mapIndexed { index, it ->
                            it.let { it.subList(0, it.size) }
                                .let { if (index == 0) it else it.reversed() }
                        }, corners[loopIndex])
                    }
            val fillExpansions = paths.flatMap { path -> path.expandFill(fw, 0.25, drawStyle.lineJoin, 2.4) }
            expansionDrawer.renderFills(drawContext, drawStyle, fillExpansions, fw)
        }
    }

}
