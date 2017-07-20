package zkl.AIGames.germs.logic

import zkl.AIGames.germs.Conf
import zkl.tools.math.MT
import zkl.tools.math.Point2D
import zkl.tools.math.zeroPoint2D
import java.util.*

class Germ(val dish:Dish) {
	
	//live in dish
	var position: Point2D = zeroPoint2D()
		set(value) {
			field = value.limitRect(dish.size, dish.size)
		}
	var velocity: Point2D = zeroPoint2D()
		set(value) {
			field = value.limitRound(Conf.germMaxVelocity)
		}
	var energy: Double = 0.5
		set(value) {
			field = MT.valueLimit(value, 0.0, 1.0)
		}
	
	
	//think in nerve
	var feel: GermFeel = GermFeel(zeroPoint2D(), zeroPoint2D(), 0.0)
	var actVelocity: Point2D = zeroPoint2D() // 0..1
	val logs = LinkedList<GermLog>()
	
}

data class GermFeel(var nutrient: Point2D, var germ: Point2D, var energy: Double)
data class GermLog(val actTime:Double,var feel: GermFeel, var actVelocity: Point2D, var realLoss: Double)