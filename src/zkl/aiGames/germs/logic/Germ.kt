package zkl.aiGames.germs.logic

import zkl.aiGames.germs.Conf
import zkl.tools.math.*
import java.util.*

class Germ {
	
	//live in dish
	var position: Point2D = zeroPoint2D()
		set(value) {
			val re = value.mutableClone()
			when {
				value.x < 0.0 -> {
					re.x = 0.0
					disturbAct = pointOf(-disturbAct.x, disturbAct.y)
				}
				value.x > Conf.dishSize -> {
					re.x = Conf.dishSize
					disturbAct = pointOf(-disturbAct.x, disturbAct.y)
				}
			}
			when {
				value.y < 0.0 -> {
					re.y = 0.0
					disturbAct = pointOf(disturbAct.x, -disturbAct.y)
				}
				value.y > Conf.dishSize -> {
					re.y = Conf.dishSize
					disturbAct = pointOf(disturbAct.x, -disturbAct.y)
				}
			}
			field = re
		}
	var velocity: Point2D = zeroPoint2D()
		set(value) { field = value.limitRound(Conf.germMaxVelocity) }
	var energy: Double = 0.5
		set(value) { field = MT.valueLimit(value, 0.0, 1.0) }
	
	
	//think in nerve
	var feel: GermFeel = GermFeel(zeroPoint2D(), zeroPoint2D(), zeroPoint2D(),0.0)
	var act: Point2D = zeroPoint2D() // 0..1
		set(value) { field = value.limitRound(1.0) }
	val logs = LinkedList<GermLog>()
	
	
	//disturb
	var disturbRate:Double = 0.0
	var disturbAct: Point2D = zeroPoint2D()
		set(value) { field = value.limitRound(1.0) }
	
	
}

data class GermFeel(var nutrient: Point2D, var germ: Point2D, var wall: Point2D, var energy: Double)
data class GermLog(val actTime:Double, var feel: GermFeel, var act: Point2D, var realLoss: Double)