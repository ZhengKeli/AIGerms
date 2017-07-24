package zkl.AIGames.germs

import zkl.AIGames.germs.logic.Germ


object Conf {
	
	
	//dish
	val dishSize = 500.0
	
	//germ
	val germCount = 6
	val germRadius = 20.0
	val germMaxVelocity = 1.0
	val germStaticEnergyCost = 0.001
	val germMovingEnergyCost = 0.001
	val germFieldConstant = 0.5e4
	val germStarveToDeath = false
	
	//nutrient
	val nutrientInterval = 20.0
	val nutrientMaxCount = 200
	val nutrientMaxAmount = 0.5
	val nutrientMinAmount = 0.1
	val nutrientMaxVelocity = 0.1
	val nutrientDisturbAcceleration = 0.0
	val nutrientFieldConstant = 1e4
	
	
	//training
	val maxFeelNutrient = 3.0
	val maxFeelGerm = 3.0
	val energyLogBufferSize = 128
	val instantRealLoss: (Germ) -> Double = { germ ->
		1.0 / (germ.energy + 0.1)
	}
	
	val isTraining = false
	val hopeTime = 200.0
	val actInterval = 20.0
	val disturbRate = 0.1
	
	//process
	val processCount = 5
	val processUnit = 1.0
	val frameInterval = 20L
	
}