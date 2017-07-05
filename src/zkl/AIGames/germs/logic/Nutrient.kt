package zkl.AIGames.germs.logic

import zkl.AIGames.germs.Conf
import zkl.tools.math.Point2D
import zkl.tools.math.zeroPoint2D


class Nutrient{
	var position: Point2D = zeroPoint2D()
		set(value) { field = value.limitRect(Conf.dishSize,Conf.dishSize) }
	
	var velocity: Point2D = zeroPoint2D()
		set(value) { field = value.limitRound(Conf.maxNutrientVelocity) }
	
	var amount: Double = 1.0
}