package zkl.AIGames.germs

import zkl.AIGames.germs.logic.Germ


object Conf {
	
	
	//dish
	val dishSize = 500.0
	
	//germ
	val germCount = 6
	val germRadius = 20.0
	val germMaxVelocity = 1.0
	val germEnergyCost = { germ: Germ ->
		Math.pow(germ.velocity.absolute() / germMaxVelocity, 2.0) * 10e-4 + 1.0e-4
	}
	val germFieldConstant = 0.5e4
	val germStarveToDeath = false
	
	//nutrient
	val nutrientInterval = 130.0
	val nutrientMaxCount = 20
	val nutrientMaxAmount = 0.7
	val nutrientMinAmount = 0.3
	val nutrientMaxVelocity = 0.1
	val nutrientDisturbAcceleration = 1.0e-4
	val nutrientFieldConstant = 1e4
	
	
	//training
	val maxFeelNutrient = 3.0
	val maxFeelGerm = 3.0
	val energyLogBufferSize = 128
	val instantRealLoss: (Germ) -> Double = { germ ->
		1.0 / (germ.energy + 0.1)
	}
	
	val isTraining = false
	val hopeTime = 100.0
	val actInterval = hopeTime/2.0
	val disturbRate = 0.9
	
	//process
	val processCount = 1
	val processUnit = 1.0
	val frameInterval = 20L
	
}