package zkl.AIGames.germs


object Conf {
	
	//process
	val processCount = 25
	val processTime = 0.1
	val frameInterval= 50L
	
	
	//dish
	val dishSize = 500.0
	
	//germ
	val germCount =10
	val germRadius = 20.0
	val maxGermVelocity = 10.0
	val maxEnergy = 1.0
	val staticEnergyCost = 0.001
	val movingEnergyCost = 0.001
	val germFieldConstant = 0.1
	
	//nutrient
	val nutrientInterval = 5.0
	val nutrientAmount = 0.5
	val maxNutrientVelocity = 3.0
	val nutrientFieldConstant = 1.0
	
	
}