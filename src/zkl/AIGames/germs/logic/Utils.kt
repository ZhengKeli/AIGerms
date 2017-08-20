package zkl.AIGames.germs.logic

import zkl.tools.math.MT
import zkl.tools.math.Point2D
import zkl.tools.math.mutableClone
import zkl.tools.math.pointOf
import java.util.*

fun Point2D.limitRound(r: Double):Point2D {
	val absolute = this.absolute()
	if (absolute > r) {
		val rate = r / absolute
		return this*rate
	}
	return this
}
fun Point2D.limitRect(width: Double, height: Double): Point2D {
	val re = mutableClone()
	when{
		x < 0.0 -> {
			re.x = 0.0
		}
		x > width -> {
			re.x = width
		}
	}
	when{
		y < 0.0 -> {
			re.y = 0.0
		}
		y > height -> {
			re.y = height
		}
	}
	return re
}
fun Point2D.isInRect(width: Double, height: Double): Boolean {
	return when{
		x < 0.0 -> false
		x > width -> false
		y < 0.0 -> false
		y > height -> false
		else -> true
	}
}

private val random = Random()
fun randomPoint2D(max:Double) = pointOf(MT.randomMirror(max), MT.randomMirror(max))
fun gaussianRandomPoint2D(max:Double = 1.0) = pointOf(max *random.nextGaussian()/3.0, max *random.nextGaussian()/3.0)