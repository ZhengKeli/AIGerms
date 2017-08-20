package zkl.AIGames.germs

import zkl.AIGames.germs.logic.Germ


object Conf {
	
	//basic
	val dishSize = 500.0
	
	val germCount = 5
	val germRadius = 20.0
	val germMaxVelocity = 1.0
	
	val germStarveToDeath = false
	
	val nutrientInterval = 30.0
	val nutrientMaxCount = 25
	val nutrientRange = 0.2..0.5
	val nutrientMaxVelocity = 0.1
	val nutrientDisturb = 1.0e-3
	
	val feelGermMax = 3.0
	val feelGermScale = 0.5e4
	val feelNutrientMax = 3.0
	val feelNutrientScale = 1e4
	val feelWallScale = 1e4
	
	
	//training
	val germEnergyCost = { germ: Germ ->
		Math.pow(germ.velocity.absolute() / germMaxVelocity, 2.0) * 1.0e-4 + 1.0e-4
	}
	val germRealLoss = { germ:Germ ->
		0.1 / (germ.energy + 0.1)
	}
	
	val logBufferSize = 1000
	val trainPatchSize = germCount*2
	
	val isTraining = false
	enum class DisturbMode {none, assign, offset }
	val disturbMode = DisturbMode.assign
	val disturbRate = 0.1
	val hopeTime = 100.0
	val actInterval = hopeTime/10.0
	
	
	
	//process & viewing
	val processCount = 5
	val processUnit = 1.0
	val frameInterval = 20L
	val energyLogBufferSize = 1280
	
	
}