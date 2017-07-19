package zkl.AIGames.germs.logic

import zkl.AIGames.germs.Conf
import zkl.AIGames.germs.nerveCore.NerveCore
import zkl.AIGames.germs.nerveCore.TFNerveCore
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
			val nutrient = Nutrient()
			nutrient.amount = Conf.nutrientAmount
			nutrient.position = pointOf(Math.random() * size, Math.random() * size)
			_nutrients.add(nutrient)
		}
	}
	
	
	//training
	val nerveCore: NerveCore = TFNerveCore()
	var trainedCount = 0
	fun runActor() {
		
		//feel
		val feels = _germs.map { germ ->
			val feelNutrient = mutableZeroPoint2D().apply {
				_nutrients.forEach { nutrient ->
					val d = nutrient.position - germ.position
					val r = Math.max(d.absolute(), 1.0)
					val m = Conf.nutrientFieldConstant * nutrient.amount / r / r
					selfOffset(d * (m / r))
				}
			}
			val feelGerm = mutableZeroPoint2D().apply {
				_germs.forEach { otherGerm ->
					if (otherGerm != germ) {
						val d = otherGerm.position - germ.position
						val r = Math.max(d.absolute(), Conf.germRadius)
						val m = Conf.germFieldConstant / r / r
						selfOffset(d * (m / r))
					}
				}
			}
			val energy = germ.energy
			germ.feel = GermFeel(feelNutrient, feelGerm, energy)
			germ.feel
		}
		
		//run nerveCore
		val actVelocity = nerveCore.runActor(feels)
		
		//apply result
		_germs.forEachIndexed { index, germ ->
			germ.wantVelocity = actVelocity[index]
			germ.disturbVelocity = randomPoint2D(Conf.germMaxDisturbVelocity)
			germ.velocity = germ.actVelocity * Conf.germMaxVelocity
			
			germ.logs.addLast(
				GermLog(processedTime, germ.feel, germ.actVelocity, Conf.realLoss(germ))
			)
			
		}
		
	}
	fun maintainGermLogs(){
		_germs.forEach { germ ->
			val nowRealLoss = Conf.realLoss(germ)
			germ.logs.forEach { log ->
				if (nowRealLoss < log.realLoss) {
					log.realLoss = nowRealLoss
				}
			}
		}
	}
	fun trainActor() {
		
		val trainableLogs = ArrayList<GermLog>(_germs.size)
		_germs.forEach { germ->
			//take the available logs
			while(germ.logs.size>Conf.maxLogCount){
				trainableLogs.add(germ.logs.removeLast())
			}
		}
		
		//train nerveCore if there are available logs
		if (trainableLogs.isEmpty()) return
		
		nerveCore.trainCritic(trainableLogs)
		nerveCore.trainActor(trainableLogs.map { it.feel })
		
		trainedCount += trainableLogs.size
		println("trained sample [$trainedCount]")
	}
}