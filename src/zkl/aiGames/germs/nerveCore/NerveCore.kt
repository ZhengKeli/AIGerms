package zkl.aiGames.germs.nerveCore

import zkl.tools.math.Point2D

interface NerveCore {
	fun initialize()
	fun finalize(saveGraph:Boolean = true)
	
	fun runActor(feels:List<GermFeel>):List<Point2D>
	fun trainCritic(germLog:List<GermLog>)
	fun trainActor(feels:List<GermFeel>)
	
}

abstract class NerveCoreException(message:String):Exception(message)
