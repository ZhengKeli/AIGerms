package zkl.AIGames.germs.logic

import zkl.AIGames.germs.Conf
import zkl.tools.math.MT

class Dish(val width: Double = Conf.dishSize, val height: Double = Conf.dishSize) {
	
	val germs: List<Germ> get() = _germs
	val nutrients: List<Nutrient> get() = _nutrients
	
	
	private val _germs = ArrayList<Germ>()
	private val _nutrients = ArrayList<Nutrient>()
	
	fun initialCreation(germsCount: Int = Conf.germCount) {
		for (i in 0 until germsCount) {
			val germ = Germ()
			germ.position.set(Math.random() * width, Math.random() * height)
			_germs.add(germ)
		}
	}
	
	fun process(time: Double= Conf.processTime) {
		val germIterator=_germs.iterator()
		while (germIterator.hasNext()) {
			val germ=germIterator.next()
			
			germ.process(time)
			germ.position.x = MT.valueLimit(germ.position.x, 0.0, this.width)
			germ.position.y = MT.valueLimit(germ.position.y, 0.0, this.height)
			
			val nutrientIterator=_nutrients.iterator()
			while (nutrientIterator.hasNext()) {
				val nutrient = nutrientIterator.next()
				if((nutrient.position-germ.position).absolute()<Conf.germRadius){
					germ.energy += nutrient.amount
					nutrientIterator.remove()
				}
			}
			
			if (germ.energy == 0.0) {
				germIterator.remove()
			}
		}
		
		nutrients.forEach { nutrient->
			nutrient.process(time)
			nutrient.position.x = MT.valueLimit(nutrient.position.x, 0.0, this.width)
			nutrient.position.y = MT.valueLimit(nutrient.position.y, 0.0, this.height)
		}
		if (Math.random() < time/Conf.nutrientInterval) {
			putNutrient()
		}
	}
	
	fun putNutrient() {
		val nutrient = Nutrient()
		nutrient.amount = Math.random()*0.3+0.1
		nutrient.position.set(Math.random() * width, Math.random() * height)
		_nutrients.add(nutrient)
	}
}