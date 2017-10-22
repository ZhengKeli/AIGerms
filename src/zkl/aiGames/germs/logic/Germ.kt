package zkl.aiGames.germs.logic

import zkl.aiGames.germs.Conf
import zkl.aiGames.germs.nerveCore.GermAct
import zkl.aiGames.germs.nerveCore.GermFeel
import zkl.aiGames.germs.nerveCore.GermLog
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
		set(value) { field = value.limitRound(1.0) }
	var energy: Double = 0.5
		set(value) { field = MT.valueLimit(value, 0.0, 1.0) }
	
	
	//think in nerve
	var feel: GermFeel = GermFeel(zeroPoint2D(), zeroPoint2D(), zeroPoint2D(), 0.0)
	var act: GermAct = GermAct(zeroPoint2D())
	val logs = LinkedList<GermLog>()
	
	
	var disturbRate :Double = 0.0
	var disturbAct: Point2D = zeroPoint2D()
		set(value) { field = value.limitRound(1.0) }
	
	
}

