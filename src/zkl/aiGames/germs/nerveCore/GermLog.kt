package zkl.aiGames.germs.nerveCore

import zkl.tools.math.Point2D

data class GermLog(
	val actTime:Double,
	var feel: GermFeel,
	var act: Point2D,
	var realLoss: Double
)