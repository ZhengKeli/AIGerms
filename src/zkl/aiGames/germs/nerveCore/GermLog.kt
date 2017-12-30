package zkl.aiGames.germs.nerveCore

import zkl.aiGames.germs.Conf
import zkl.tools.math.Point2D

data class GermLog(
	val actTime: Double,
	val actTimeEnergy:Double,
	
	var feel: GermFeel,
	var act: GermAct
) {
	var hopeTimeEnergy: Double = actTimeEnergy
	val realLoss: Double get() = Conf.germRealLoss(actTimeEnergy, hopeTimeEnergy)
}

data class GermAct(
	var velocity: Point2D
)

data class GermFeel(
	var nutrient: Point2D,
	var germ: Point2D,
	var wall: Point2D
)