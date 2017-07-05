package zkl.AIGames.germs.logic

import zkl.AIGames.germs.Conf
import zkl.tools.math.MT
import zkl.tools.math.Point2D
import zkl.tools.math.zeroPoint2D

class Germ{
	
	var position: Point2D = zeroPoint2D()
		set(value) { field = value.limitRect(Conf.dishSize, Conf.dishSize) }
	
	var velocity: Point2D = zeroPoint2D()
		set(value) { field = value.limitRound(Conf.maxGermVelocity) }
	
	var energy: Double = 1.0
		set(value) { field = MT.valueLimit(value, 0.0, Conf.maxEnergy) }
	
	
	var feelNutrient: Point2D = zeroPoint2D()
	var feelGerm: Point2D = zeroPoint2D()
	var disturbVelocity: Point2D = zeroPoint2D()
}
