package zkl.aiGames.germs.fx

import zkl.aiGames.germs.nerveCore.GermFeel
import zkl.aiGames.germs.nerveCore.TFNerveCore
import zkl.tools.math.pointOf

fun main(args: Array<String>) {
	val nerveCore = TFNerveCore()
	nerveCore.initialize()
	
	val result= nerveCore.runActor(listOf(
		GermFeel(nutrient = pointOf(0.0, 0.0), germ = pointOf(0.0, 0.0), wall = pointOf(0.0, 0.0)),
		GermFeel(nutrient = pointOf(0.1, 0.2), germ = pointOf(0.2, 0.1), wall = pointOf(0.0, 0.0))
	))
	result.forEach {
		println(it)
	}
	nerveCore.finalize()
}
