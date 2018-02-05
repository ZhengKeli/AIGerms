package zkl.aiGames.germs.logic

import zkl.aiGames.germs.Conf
import zkl.aiGames.germs.nerveCore.GermFeel
import zkl.aiGames.germs.nerveCore.GermLog
import zkl.aiGames.germs.nerveCore.NerveCore
import zkl.tools.math.geometry.*
import zkl.tools.math.random.Random


class Dish(val nerveCore: NerveCore, val dishSize: Double) {
	
	//contents
	val germs = ArrayList<Germ>()
	
	@Synchronized
	fun putGerm(count: Int = 1) {
		repeat(count) { id ->
			val germ = Germ()
			germ.position = pointOf(Math.random() * dishSize, Math.random() * dishSize)
			germ.disturbRate = Conf.disturbRate.run { start + (endInclusive - start) / count * id }
			germs.add(germ)
		}
	}
	
	
	val nutrients = ArrayList<Nutrient>()
	
	@Synchronized
	fun putNutrient(amount: Double, position: Point2D) {
		val nutrient = Nutrient()
		nutrient.amount = amount
		nutrient.position = position
		nutrients.add(nutrient)
	}
	
	@Synchronized
	fun putRandomNutrients(count: Int = 1) {
		repeat(count) {
			if (nutrients.size < Conf.nutrientMaxCount) {
				val amount = Random.uniform(Conf.nutrientAmountRange.start, Conf.nutrientAmountRange.endInclusive)
				val position = pointOf(Math.random() * dishSize, Math.random() * dishSize)
				putNutrient(amount, position)
			}
		}
	}
	
	
	//processing
	var processedTime = 0.0
	
	@Synchronized
	fun process(time: Double = Conf.processUnit) {
		
		//nutrients move
		nutrients.forEach { nutrient ->
			nutrient.run {
				velocity += randomPoint2D(Conf.nutrientDisturbForce)
				position += velocity * time
			}
		}
		
		//move and eat
		germs.forEach { germ ->
			
			//move
			germ.run {
				position += velocity * time
				energy -= Conf.germEnergyCost(this) * time
			}
			
			//eat
			nutrients.removeIf { nutrient ->
				if ((nutrient.position - germ.position).absolute() < Conf.germRadius) {
					germ.energy += nutrient.amount
					true
				} else false
			}
			
		}
		
		//hit
		germs.forEach { germ1 ->
			germs.forEach { germ2 ->
				if (germ1 != germ2 && (germ1.position - germ2.position).absolute() < 2.0 * Conf.germRadius) {
					val middle = (germ1.position + germ2.position) / 2.0
					germ1.position = middle + (germ1.position - middle).unit() * Conf.germRadius
					germ2.position = middle + (germ2.position - middle).unit() * Conf.germRadius
				}
			}
		}
		
		processedTime += time
	}
	
	
	//training
	var trainedCount = 0
		private set
	
	@Synchronized
	fun runActor(isTraining: Boolean = true) {
		
		//feel
		val feels = germs.map { germ ->
			val feelNutrient = mutableZeroPoint2D().apply {
				nutrients.forEach { nutrient ->
					val d = nutrient.position - germ.position
					val r = Math.max(d.absolute(), 1.0)
					val m = Conf.feelNutrientScale * nutrient.amount / r / r - 0.05
					if (m > 0) selfOffset(d * (m / r))
				}
			}.limitRound(Conf.feelNutrientMax)
			val feelGerm = mutableZeroPoint2D().apply {
				germs.forEach { otherGerm ->
					if (otherGerm == germ) return@forEach
					val d = otherGerm.position - germ.position
					val r = Math.max(d.absolute(), Conf.germRadius)
					val m = Conf.feelGermScale / r / r - 0.05
					if (m > 0) selfOffset(d * (m / r))
				}
			}.limitRound(Conf.feelGermMax)
			val feelWall = mutableZeroPoint2D().apply {
				val dx1 = germ.position.x + 50.0
				val dx2 = dishSize - germ.position.x + 50.0
				val dy1 = germ.position.y + 50.0
				val dy2 = dishSize - germ.position.y + 50.0
				val const = Conf.feelWallScale
				selfOffset(
					x = -const / (dx1 * dx1) + const / (dx2 * dx2),
					y = -const / (dy1 * dy1) + const / (dy2 * dy2))
			}
			germ.feel = GermFeel(feelNutrient, feelGerm, feelWall)
			germ.feel
		}
		
		//run nerveCore
		val germActs = nerveCore.runActor(feels)
		
		//apply result
		germs.forEachIndexed { index, germ ->
			
			//apply act
			germ.act = germActs[index]
			if (isTraining) {
				germ.disturbAct += randomPoint2D(Conf.disturbForce)
				germ.act.velocity = germ.act.velocity * (1.0 - germ.disturbRate) + germ.disturbAct * germ.disturbRate
			}
			
			//apply velocity
			germ.velocity = germ.act.velocity
			
			//add log if is training
			if (isTraining && germ.log == null) {
				germ.log = GermLog(processedTime, germ.feel, germ.act, germ.energy)
			}
			
		}
		
	}
	
	
	private val logBuffer = ArrayList<GermLog>()
	
	@Synchronized
	fun maintainLogs() {
		germs.forEach { germ ->
			germ.log?.let { log ->
				log.feels.add(germ.feel)
				log.acts.add(germ.act)
				if (log.acts.size == Conf.hopeCount) {
					//take the available logs
					log.hopeTimeEnergy = germ.energy
					logBuffer.add(log)
					germ.log = null
				}
			}
		}
	}
	
	@Synchronized
	fun trainNerveCore() {
		if (logBuffer.size < Conf.logBufferSize) return
		if (Conf.trainCritic) nerveCore.trainCritic(logBuffer)
		if (Conf.trainActor) nerveCore.trainActor(logBuffer)
		trainedCount += logBuffer.size
		logBuffer.clear()
	}
	
	//logging
	@Synchronized
	fun getAverageEnergy(): Double {
		return germs.sumByDouble { it.energy } / germs.size
	}
	
}