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
				velocity += randomPoint2D(Conf.nutrientDisturb)
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
			val germ = Germ(this)
			germ.position = pointOf(Math.random() * dishSize, Math.random() * dishSize)
			_germs.add(germ)
		}
	}
	@Synchronized fun putRandomNutrients(count:Int=1) {
		repeat(count){
			if (_nutrients.size < Conf.nutrientMaxCount) {
				val amount = MT.random(Conf.nutrientRange.start, Conf.nutrientRange.endInclusive)
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
	val nerveCore: NerveCore = TFNerveCore()
	var trainedCount = 0
	@Synchronized fun runActor(isTraining:Boolean=true) {
		
		//feel
		val feels = _germs.map { germ ->
			val feelNutrient = mutableZeroPoint2D().apply {
				_nutrients.forEach { nutrient ->
					val d = nutrient.position - germ.position
					val r = Math.max(d.absolute(), 1.0)
					val m = Conf.feelNutrientScale * nutrient.amount / r / r
					selfOffset(d * (m / r))
				}
			}.limitRound(Conf.feelNutrientMax)
			val feelGerm = mutableZeroPoint2D().apply {
				_germs.forEach { otherGerm ->
					if (otherGerm != germ) {
						val d = otherGerm.position - germ.position
						val r = Math.max(d.absolute(), Conf.germRadius)
						val m = Conf.feelGermScale / r / r
						selfOffset(d * (m / r))
					}
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
			val wantAct = actVelocities[index].limitRound(1.0)
			val disturbMode = if(isTraining) Conf.disturbMode else Conf.DisturbMode.none
			germ.act = when (disturbMode) {
				Conf.DisturbMode.none -> wantAct
				Conf.DisturbMode.assign ->
					if (Math.random() > Conf.disturbRate) wantAct
					else gaussianRandomPoint2D()
				Conf.DisturbMode.offset ->
					wantAct + gaussianRandomPoint2D(Conf.germMaxVelocity * Conf.disturbRate)
			}
			
			//apply velocity
			germ.velocity = germ.act * Conf.germMaxVelocity
			
			//add log if is training
			if (Conf.isTraining) {
				val realLoss = Conf.germRealLoss(germ)
				val germLog = GermLog(processedTime, germ.feel, germ.act, realLoss)
				germ.logs.addLast(germLog)
			}
			
		}
		
	}
	@Synchronized fun trainActor() {
		val availableTime = processedTime - Conf.hopeTime
		val availableLogs = ArrayList<GermLog>(_germs.size)
		_germs.forEach { germ->
			val nowRealLoss = Conf.germRealLoss(germ)
			
			val iterator = germ.logs.iterator()
			while (iterator.hasNext()) {
				val log = iterator.next()
				if (log.actTime < availableTime) {
					//take the available logs
					availableLogs.add(log)
					iterator.remove()
				} else {
					//maintain other logs
					log.realLoss = nowRealLoss
				}
			}
			
		}
		
		//train nerveCore if there are available logs
		if (availableLogs.isEmpty()) return
		
		nerveCore.trainCritic(availableLogs)
		nerveCore.trainActor(availableLogs.map { it.feel })
		
		trainedCount += availableLogs.size
		println("trained sample [$trainedCount]")
	}
	
	@Synchronized fun getAverageEnergy(): Double {
		return _germs.sumByDouble { it.energy } / _germs.size
	}
	
}