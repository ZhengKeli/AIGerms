package zkl.AIGames.germs.logic

import zkl.tools.math.Point2D

interface NerveCore {
	fun initialize()
	fun finalize()
	
	fun runActor(feelNutrient: List<Point2D>,feelGerms: List<Point2D>): List<Point2D>
	fun trainCritic(feelNutrient: List<Point2D>, feelGerm: List<Point2D>, actVelocity:List<Point2D>, realLoss:List<Float>)
	fun trainActor(feelNutrient: List<Point2D>, feelGerm: List<Point2D>)
	
}