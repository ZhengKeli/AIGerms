package zkl.AIGames.germs.nerveCore

import zkl.AIGames.germs.logic.GermFeel
import zkl.AIGames.germs.logic.GermLog
import zkl.tools.math.Point2D

interface NerveCore {
	fun initialize()
	fun finalize()
	
	fun runActor(feels:List<GermFeel>):List<Point2D>
	fun trainCritic(germLog:List<GermLog>)
	fun trainActor(feels:List<GermFeel>)
	
}

abstract class NerveCoreException(message:String):Exception(message)
