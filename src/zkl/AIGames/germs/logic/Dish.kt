package zkl.AIGames.germs.logic

import zkl.AIGames.germs.Conf
import zkl.AIGames.germs.nerveCore.NerveCore
import zkl.AIGames.germs.nerveCore.TFNerveCore
import zkl.tools.math.MT
import zkl.tools.math.Point2D
import zkl.tools.math.mutableZeroPoint2D
import zkl.tools.math.pointOf


class Dish(val dishSize:Double = Conf.dishSize) {
	
	val germs: List<Germ> get() = _germs
	private val _germs = ArrayList<Germ>()
	val nutrients: List<Nutrient> get() = _nutrients
	private val _nutrients = ArrayList<Nutrient>()
	
	
	//processing
	var processedTime = 0.0
	@Synchronized fun initialize() {
		println("initializing nerve network")
		nerveCore.initialize()
		println("initialization done")
	}
	@Synchronized fun finalize(saveGraph:Boolean = Conf.isTraining) {
		nerveCore.finalize(saveGraph)
		if(saveGraph) println("graph saved")
	}
	@Synchronized fun process(time: Double= Conf.processUnit) {
		
		//nutrients move
		_nutrients.forEach {
			it.run {
				velocity += randomPoint2D(Conf.nutrientDisturbForce)
				position += velocity * time
			}
		}
		
		//move and eat
		_germs.forEach { germ ->
			
			//move
			germ.run {
				position += velocity * time
				energy -= Conf.germEnergyCost(this) * time
			}
			
			//eat
			_nutrients.removeIf { nutrient ->
				if ((nutrient.position - germ.position).absolute() < Conf.germRadius) {
					germ.energy += nutrient.amount
					true
				} else false
			}
			
		}
		
		//die
		if (Conf.germStarveToDeath) {
			var removedCount = 0
			_germs.removeIf { germ->
				if(germ.energy <= 0.01){
					removedCount++
					true
				}else false
			}
			for(i in 1..removedCount){
				putGerm()
			}
		}
		
		processedTime+=time
	}
	
	@Synchronized fun putGerm(count:Int=1) {
		repeat(count){
			val germ = Germ()
			germ.position = pointOf(Math.random() * dishSize, Math.random() * dishSize)
			_germs.add(germ)
		}
	}
	@Synchronized fun putRandomNutrients(count:Int=1) {
		repeat(count){
			if (_nutrients.size < Conf.nutrientMaxCount) {
				val amount = MT.random(Conf.nutrientAmountRange.start, Conf.nutrientAmountRange.endInclusive)
				val position = pointOf(Math.random() * dishSize, Math.random() * dishSize)
				putNutrient(amount, position)
			}
		}
	}
	@Synchronized fun putNutrient(amount:Double,position: Point2D) {
		val nutrient = Nutrient()
		nutrient.amount = amount
		nutrient.position = position
		_nutrients.add(nutrient)
	}
	
	
	//training
	private val nerveCore: NerveCore = TFNerveCore()
	var trainedCount = 0
		private set
	
	private val logBuffer = ArrayList<GermLog>(Conf.logBufferSize)
	@Synchronized private fun addLog(germLog: GermLog) {
		if (logBuffer.size < Conf.logBufferSize) {
			logBuffer.add(germLog)
		}else{
			val randomIndex = (Math.random() * logBuffer.size).toInt()
			logBuffer[randomIndex] = germLog
		}
	}
	@Synchronized private fun getRandomLog(): GermLog {
		val randomIndex = (Math.random() * logBuffer.size).toInt()
		return logBuffer[randomIndex]
	}
	@Synchronized private fun getPatchLogs(patchSize: Int = Conf.trainPatchSize):List<GermLog>?{
		if(logBuffer.size< patchSize*10) return null
		return Array(patchSize){ getRandomLog() }.asList()
	}
	@Synchronized fun maintainLogs(){
		val availableTime = processedTime - Conf.hopeTime
		_germs.forEach { germ->
			val nowRealLoss = Conf.germRealLoss(germ)
			val iterator = germ.logs.iterator()
			while (iterator.hasNext()) {
				val log = iterator.next()
				if (log.actTime < availableTime) {
					//take the available logs
					logBuffer.add(log)
					iterator.remove()
				} else {
					//maintain other logs
					log.realLoss = nowRealLoss
				}
			}
		}
	}
	
	@Synchronized fun runActor(isTraining:Boolean=true) {
		
		//feel
		val feels = _germs.map { germ ->
			val feelNutrient = mutableZeroPoint2D().apply {
				_nutrients.forEach { nutrient ->
					val d = nutrient.position - germ.position
					val r = Math.max(d.absolute(), 1.0)
					val m = Conf.feelNutrientScale * nutrient.amount / r / r - 0.05
					if(m>0) selfOffset(d * (m / r))
				}
			}.limitRound(Conf.feelNutrientMax)
			val feelGerm = mutableZeroPoint2D().apply {
				_germs.forEach { otherGerm ->
					if (otherGerm == germ) return@forEach
					val d = otherGerm.position - germ.position
					val r = Math.max(d.absolute(), Conf.germRadius)
					val m = Conf.feelGermScale / r / r - 0.05
					if (m > 0) selfOffset(d * (m / r))
				}
			}.limitRound(Conf.feelGermMax)
			val feelWall = mutableZeroPoint2D().apply {
				val dx1 = germ.position.x + 50.0
				val dx2 = dishSize - germ.position.x+ 50.0
				val dy1 = germ.position.y+ 50.0
				val dy2 = dishSize - germ.position.y+ 50.0
				val const = Conf.feelWallScale
				selfOffset(
					x = -const / (dx1 * dx1) + const / (dx2 * dx2),
					y = -const / (dy1 * dy1) + const / (dy2 * dy2))
			}
			val energy = germ.energy
			germ.feel = GermFeel(feelNutrient, feelGerm, feelWall, energy)
			germ.feel
		}
		
		//run nerveCore
		val actVelocities = nerveCore.runActor(feels)
		
		//apply result
		_germs.forEachIndexed { index, germ ->
			
			//apply act
			germ.act = actVelocities[index].limitRound(1.0)
			if (isTraining) {
				germ.disturbAct += randomPoint2D(Conf.disturbForce)
				germ.act += germ.disturbAct
			}
			
			//apply velocity
			germ.velocity = germ.act * Conf.germMaxVelocity
			
			//add log if is training
			if (isTraining) {
				val realLoss = Conf.germRealLoss(germ)
				val germLog = GermLog(processedTime, germ.feel, germ.act, realLoss)
				germ.logs.addLast(germLog)
			}
			
		}
		
	}
	@Synchronized fun trainActor() {
		val trainLogs = getPatchLogs()?:return
		nerveCore.trainCritic(trainLogs)
		nerveCore.trainActor(trainLogs.map { it.feel })
		
		trainedCount += trainLogs.size
		
	}
	
	
	//logging
	@Synchronized fun getAverageEnergy(): Double {
		return _germs.sumByDouble { it.energy } / _germs.size
	}
	
}