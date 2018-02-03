package zkl.aiGames.germs.nerveCore

import zkl.aiGames.germs.Conf
import zkl.tools.math.geometry.Point2D

data class GermLog(
	val actTime: Double,
	val feels: ArrayList<GermFeel>,
	val acts: ArrayList<GermAct>,
	val actTimeEnergy: Double,
	var hopeTimeEnergy: Double
) {
	constructor(actTime: Double, actTimeFeel: GermFeel, actTimeAct: GermAct, actTimeEnergy: Double)
		: this(actTime, arrayListOf(actTimeFeel), arrayListOf(actTimeAct), actTimeEnergy, actTimeEnergy)
	
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