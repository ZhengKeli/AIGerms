package zkl.AIGames.germs.logic

import zkl.tools.math.Point2D

interface NerveCore {
	fun initialize()
	fun finalize()
	
	fun runActor(list: List<Point2D>)
	
}