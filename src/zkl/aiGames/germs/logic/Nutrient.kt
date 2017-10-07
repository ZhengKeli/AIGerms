package zkl.aiGames.germs.logic

import zkl.aiGames.germs.Conf
import zkl.tools.math.Point2D
import zkl.tools.math.mutableClone
import zkl.tools.math.pointOf
import zkl.tools.math.zeroPoint2D


class Nutrient{
	var position: Point2D = zeroPoint2D()
		set(value) {
			val re = value.mutableClone()
			when{
				value.x < 0.0 -> {
					re.x = 0.0
					velocity= pointOf(-velocity.x,velocity.y)
				}
				value.x > Conf.dishSize -> {
					re.x = Conf.dishSize
					velocity= pointOf(-velocity.x,velocity.y)
				}
			}
			when{
				value.y < 0.0 -> {
					re.y = 0.0
					velocity= pointOf(velocity.x,-velocity.y)
				}
				value.y > Conf.dishSize -> {
					re.y = Conf.dishSize
					velocity= pointOf(velocity.x,-velocity.y)
				}
			}
			field = re
		}
	
	var velocity: Point2D = zeroPoint2D()
		set(value) { field = value.limitRound(Conf.nutrientMaxVelocity) }
	
	var amount: Double = 1.0
}