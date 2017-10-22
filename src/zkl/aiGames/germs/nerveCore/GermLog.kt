package zkl.aiGames.germs.nerveCore

import zkl.tools.math.Point2D

data class GermLog(
	val actTime: Double,
	var feel: GermFeel,
	var act: GermAct,
	var realLoss: Double
)

data class GermAct(
	var velocity: Point2D
)

data class GermFeel(
	var nutrient: Point2D,
	var germ: Point2D,
	var wall: Point2D,
	var energy: Double
)