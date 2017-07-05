package zkl.AIGames.germs.logic

import zkl.AIGames.germs.Conf
import zkl.AIGames.germs.tf.TFNerveCore
import zkl.tools.math.MT
import zkl.tools.math.Point2D
import zkl.tools.math.mutableZeroPoint2D
import zkl.tools.math.pointOf

class Dish {
	val size:Double get() =  Conf.dishSize
	
	val germs: List<Germ> get() = _germs
	private val _germs = ArrayList<Germ>()
	val nutrients: List<Nutrient> get() = _nutrients
	private val _nutrients = ArrayList<Nutrient>()
	
	
	fun initialize(germsCount: Int = Conf.germCount) {
		println("creating germs")
		for (i in 0 until germsCount) {
			val germ = Germ()
			germ.position = pointOf(Math.random() * size, Math.random() * size)
			_germs.add(germ)
		}
		
		println("initializing nerve network")
		nerveCore.initialize()
		
		println("initialization done")
		
	}
	
	fun process(time: Double= Conf.processTime) {
		
		//nutrients move
		_nutrients.forEach {
			it.run {
				velocity += pointOf(MT.randomMirror(0.01), MT.randomMirror(0.01))
				velocity = velocity.limitRound(Conf.maxNutrientVelocity)
				position += velocity * time
			}
		}
		
		//feel
		_germs.forEach { germ ->
			germ.feelNutrient = mutableZeroPoint2D().apply {
				_nutrients.forEach { nutrient ->
					val d = nutrient.position - germ.position
					val r = Math.max(d.absolute(),1.0)
					val m = Conf.nutrientFieldConstant * nutrient.amount / r / r
					selfOffset(d * (m / r))
				}
			}
			germ.feelGerm = mutableZeroPoint2D().apply {
				_germs.forEach { otherGerm ->
					if (otherGerm != germ) {
						val d = otherGerm.position - germ.position
						val r = Math.max(d.absolute(),1.0)
						val m = Conf.germFieldConstant / r / r
						selfOffset(d * (m / r))
					}
				}
			}
		}
		
		//think
		runActor()
		
		//move and eat
		_germs.forEachIndexed { index,germ ->
			
			//apply result of thinking
			germ.run {
				actVelocity.let {
					velocity = it[index] * 1.0
				}
			}
			
			//move
			germ.run {
				position += velocity * time
				energy -= (Conf.movingEnergyCost * velocity.absolute() + Conf.staticEnergyCost) * time
			}
			
			//eat
			_nutrients.removeIf { nutrient ->
				if ((nutrient.position - germ.position).absolute() < Conf.germRadius) {
					germ.energy += nutrient.amount
					true
				} else false
			}
			
		}
		
		//rethink
		trainActor()
		
		//die
//		_germs.removeIf { germ-> germ.energy == 0.0 }
//		if(_germs.size==0) throw RuntimeException("The germs died out!")
		
		//put nutrient
		if (Math.random() < time/Conf.nutrientInterval) {
			val nutrient = Nutrient()
			nutrient.amount = Conf.nutrientAmount
			nutrient.position = pointOf(Math.random() * size, Math.random() * size)
			_nutrients.add(nutrient)
		}
		
	}
	
	val nerveCore:NerveCore = TFNerveCore()
	val feelNutrient = object :AbstractList<Point2D>(){
		override val size: Int get() = germs.size
		override fun get(index: Int) = germs[index].feelNutrient*1.0e5
	}
	val feelGerm = object :AbstractList<Point2D>(){
		override val size: Int get() = germs.size
		override fun get(index: Int) = germs[index].feelGerm*1.0e5
	}
	val realLoss = object :AbstractList<Float>(){
		override val size: Int get() = germs.size
		override fun get(index: Int): Float {
			return (actVelocity[index]-feelNutrient[index]).absolute().toFloat()
//			return (1.0-germs[index].energy/Conf.maxEnergy).toFloat()
		}
	}
	lateinit var actVelocity: List<Point2D>
	fun runActor() {
		actVelocity = nerveCore.runActor(feelNutrient, feelGerm)
	}
	fun trainActor() {
//		nerveCore.trainCritic(feelNutrient, feelGerm, actVelocity, realLoss)
//		nerveCore.trainActor(feelNutrient, feelGerm)
	}
}