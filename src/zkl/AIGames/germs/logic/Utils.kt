package zkl.AIGames.germs.logic

import zkl.tools.math.Point2D
import zkl.tools.math.mutableClone

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
		y > width -> {
			re.y = height
		}
	}
	return re
}
