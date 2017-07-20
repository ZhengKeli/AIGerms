package zkl.AIGames.germs

import zkl.AIGames.germs.logic.Germ


object Conf {
	
	//process
	val processCount = 5
	val processUnit = 0.1
	val frameInterval= 20L
	
	
	//dish
	val dishSize = 500.0
	
	//germ
	val germCount = 10
	val germRadius = 20.0
	val germMaxVelocity = 10.0
	val germStaticEnergyCost = 0.01
	val germMovingEnergyCost = 0.001
	val germFieldConstant = 0.5e4
	val germStarveToDeath = false
	
	//nutrient
	val nutrientInterval = 5.0
	val nutrientAmount = 0.5
	val nutrientMaxVelocity = 3.0
	val nutrientFieldConstant = 1e4
	val nutrientDisturbAcceleration = 0.0
	
	
	//training
	val actInterval = 3.0
	val hopeTime = 20.0
	val realLoss: (Germ)->Double = { germ ->
		1.0/(germ.energy+0.1)
	}
	val disturbRate = 0.0
	
}