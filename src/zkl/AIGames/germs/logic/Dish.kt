package zkl.AIGames.germs.logic

import zkl.AIGames.germs.Conf
import zkl.AIGames.germs.nerveCore.NerveCore
import zkl.AIGames.germs.nerveCore.TFNerveCore
import zkl.tools.math.MT
import zkl.tools.math.mutableZeroPoint2D
import zkl.tools.math.pointOf


class Dish(val size:Double = Conf.dishSize) {
	
	val germs: List<Germ> get() = _germs
	private val _germs = ArrayList<Germ>()
	val nutrients: List<Nutrient> get() = _nutrients
	private val _nutrients = ArrayList<Nutrient>()
	
	
	//processing
	var processedTime = 0.0
	fun initialize() {
		println("initializing nerve network")
		nerveCore.initialize()
		println("initialization done")
	}
	fun finalize() {
		nerveCore.finalize()
		println("graph saved")
	}
	fun process(time: Double= Conf.processUnit) {
		
		//nutrients move
		_nutrients.forEach {
			it.run {
				velocity += randomPoint2D(Conf.nutrientDisturbAcceleration)
				position += velocity * time
			}
		}
		
		//move and eat
		_germs.forEach { germ ->
			
			//move
			germ.run {
				position += velocity * time
				energy -= (Conf.germMovingEnergyCost * velocity.absolute() + Conf.germStaticEnergyCost) * time
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
	
	fun putGerm(count:Int=1) {
		repeat(count){
			val germ = Germ(this)
			germ.position = pointOf(Math.random() * size, Math.random() * size)
			_germs.add(germ)
		}
	}
	fun putNutrient(count:Int=1) {
		repeat(count){
			if (_nutrients.size < Conf.nutrientMaxCount) {
				val nutrient = Nutrient()
				nutrient.amount = MT.random(Conf.nutrientMinAmount,Conf.nutrientMaxAmount)
				nutrient.position = pointOf(Math.random() * size, Math.random() * size)
				_nutrients.add(nutrient)
			}
		}
	}
	
	
	//training
	val nerveCore: NerveCore = TFNerveCore()
	var trainedCount = 0
	fun runActor(isTraining:Boolean=true) {
		
		//feel
		val feels = _germs.map { germ ->
			val feelNutrient = mutableZeroPoint2D().apply {
				_nutrients.forEach { nutrient ->
					val d = nutrient.position - germ.position
					val r = Math.max(d.absolute(), 1.0)
					val m = Conf.nutrientFieldConstant * nutrient.amount / r / r
					selfOffset(d * (m / r))
				}
			}.limitRound(Conf.maxFeelNutrient)
			val feelGerm = mutableZeroPoint2D().apply {
				_germs.forEach { otherGerm ->
					if (otherGerm != germ) {
						val d = otherGerm.position - germ.position
						val r = Math.max(d.absolute(), Conf.germRadius)
						val m = Conf.germFieldConstant / r / r
						selfOffset(d * (m / r))
					}
				}
			}.limitRound(Conf.maxFeelGerm)
			val energy = germ.energy
			germ.feel = GermFeel(feelNutrient, feelGerm, energy)
			germ.feel
		}
		
		//run nerveCore
		val actVelocities = nerveCore.runActor(feels)
		
		//apply result
		_germs.forEachIndexed { index, germ ->
			val wantVelocity = actVelocities[index].limitRound(1.0)
			if (isTraining) {
				germ.actVelocity =
					if (Math.random() > Conf.disturbRate) wantVelocity
					else randomPoint2D(1.0)
				germ.velocity = germ.actVelocity * Conf.germMaxVelocity
				
				GermLog(processedTime, germ.feel, germ.actVelocity, Conf.instantRealLoss(germ))
					.let { germ.logs.addLast(it) }
			}else{
				germ.actVelocity = wantVelocity
				germ.velocity = germ.actVelocity * Conf.germMaxVelocity
			}
			
		}
		
	}
	fun trainActor() {
		val availableTime = processedTime - Conf.hopeTime
		val availableLogs = ArrayList<GermLog>(_germs.size)
		_germs.forEach { germ->
			val nowRealLoss = Conf.instantRealLoss(germ)
			
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
	
	fun getAverageEnergy(): Double {
		return _germs.sumByDouble { it.energy } / _germs.size
	}
	
}