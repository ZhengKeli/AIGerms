package zkl.AIGames.germs.logic

import zkl.AIGames.germs.Conf

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
			
			germ.process(this, time)
			
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
		
		nutrients.forEach { nutrient-> nutrient.process(this, time) }
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