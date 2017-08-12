package zkl.AIGames.germs

import zkl.AIGames.germs.logic.Germ


object Conf {
	
	//basic
	val dishSize = 800.0
	
	val germCount = 20
	val germRadius = 20.0
	val germMaxVelocity = 1.0
	
	val germStarveToDeath = false
	
	val nutrientInterval = 40.0
	val nutrientMaxCount = 100
	val nutrientRange = 0.3..0.7
	val nutrientMaxVelocity = 0.1
	val nutrientDisturb = 1.0e-3
	
	val feelGermMax = 3.0
	val feelGermScale = 0.5e4
	val feelNutrientMax = 3.0
	val feelNutrientScale = 1e4
	val feelWallScale = 1e4
	
	
	//training
	val germEnergyCost = { germ: Germ ->
		Math.pow(germ.velocity.absolute() / germMaxVelocity, 2.0) * 5.0e-4 + 1.0e-4
	}
	val germRealLoss = { germ:Germ ->
		1.0 / (germ.energy + 0.1)
	}
	
	val isTraining = false
	
	enum class DisturbMode {none, assign, offset }
	val disturbMode = DisturbMode.offset
	val disturbRate = 0.5
	
	val hopeTime = 100.0
	val actInterval = hopeTime/10.0
	
	
	
	//process & viewing
	val processCount = 5
	val processUnit = 1.0
	val frameInterval = 20L
	val energyLogBufferSize = 128
	
	
}