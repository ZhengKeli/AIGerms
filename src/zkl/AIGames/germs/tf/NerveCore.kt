package zkl.AIGames.germs.tf

import zkl.tools.math.Point2D


interface NerveCore {
	fun initialize()
	fun finalize()
	
	fun runActor(list: List<Point2D>)
	
}

class TFNerveCore : NerveCore {
	override fun initialize() {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}
	
	override fun finalize() {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}
	
	override fun runActor(list: List<Point2D>) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}
	
	val apiPath = "./python/api.py"
	
}
