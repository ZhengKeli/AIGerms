package zkl.aiGames.germs.logic

import zkl.tools.math.geometry.*
import zkl.tools.math.random.Random

fun Point2D.limitRound(r: Double): Point2D {
	val absolute = this.absolute()
	if (absolute > r) {
		val rate = r / absolute
		return this * rate
	}
	return this
}

fun Point2D.limitRect(width: Double, height: Double): Point2D {
	val re = mutableClone()
	when {
		x < 0.0 -> {
			re.x = 0.0
		}
		x > width -> {
			re.x = width
		}
	}
	when {
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
	return when {
		x < 0.0 -> false
		x > width -> false
		y < 0.0 -> false
		y > height -> false
		else -> true
	}
}

fun randomPoint2D(max: Double) = pointOf(Random.mirror(max), Random.mirror(max))
fun gaussianRandomPoint2D(max: Double = 1.0) = pointOf(Random.normal(max / 3.0), Random.normal(max / 3.0))