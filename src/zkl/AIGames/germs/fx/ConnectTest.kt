package zkl.AIGames.germs.fx

import zkl.AIGames.germs.logic.GermFeel
import zkl.AIGames.germs.nerveCore.TFNerveCore
import zkl.tools.math.pointOf

fun main(args: Array<String>) {
	val nerveCore = TFNerveCore()
	nerveCore.initialize()
	
	val result= nerveCore.runActor(listOf(
		GermFeel(nutrient = pointOf(0.0,0.0), germ = pointOf(0.0,0.0), energy = 0.5),
		GermFeel(nutrient = pointOf(0.1,0.2), germ = pointOf(0.2,0.1), energy = 0.9)
	))
	result.forEach {
		println(it)
	}
	nerveCore.finalize()
}
