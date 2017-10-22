package zkl.aiGames.germs.nerveCore

interface NerveCore {
	fun initialize()
	fun finalize(saveGraph:Boolean = true)
	
	fun runActor(feels:List<GermFeel>): List<GermAct>
	fun trainCritic(germLog:List<GermLog>)
	fun trainActor(feels:List<GermFeel>)
	
}

abstract class NerveCoreException(message:String):Exception(message)
