package zkl.aiGames.germs.logic

import zkl.aiGames.germs.Conf
import zkl.aiGames.germs.nerveCore.GermAct
import zkl.aiGames.germs.nerveCore.GermFeel
import zkl.aiGames.germs.nerveCore.GermLog
import zkl.tools.math.geometry.Point2D
import zkl.tools.math.geometry.mutableClone
import zkl.tools.math.geometry.pointOf
import zkl.tools.math.geometry.zeroPoint2D

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
	var energy: Double = 0.0
	
	
	//think in nerve
	var feel: GermFeel = GermFeel(zeroPoint2D(), zeroPoint2D(), zeroPoint2D())
	var act: GermAct = GermAct(zeroPoint2D())
	var log:GermLog? = null
	
	
	var disturbRate :Double = 0.0
	var disturbAct: Point2D = zeroPoint2D()
		set(value) { field = value.limitRound(1.0) }
	
	
}

