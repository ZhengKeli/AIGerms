package zkl.aiGames.germs

import zkl.aiGames.germs.logic.Germ


object Conf {
	
	//basic
	val dishSize = 100.0
	
	val germCount = 1
	val germRadius = 20.0
	
	val nutrientInterval = 1e3/1.0
	val nutrientMaxCount = 1
	val nutrientAmountRange = 0.5..0.5
	val nutrientMaxVelocity = 0.1
	val nutrientDisturbForce = 1e-4
	
	val feelGermMax = 3.0
	val feelGermScale = 0.5e4
	val feelNutrientMax = 3.0
	val feelNutrientScale = 2e4
	val feelWallScale = 0.3e4
	
	
	//training
	val germEnergyCost = { germ: Germ ->
		germ.velocity.absolute() * 5.0e-4
	}
	val germRealLoss = { actTimeEnergy: Double, hopeTimeEnergy: Double ->
		(actTimeEnergy - hopeTimeEnergy)*2.0
	}
	val energyLogBufferSize = 1280
	
	val isTraining = false
	val hopeTime = 100.0
	val actInterval = hopeTime/1.0
	val disturbRate = 1.0..1.0
	val disturbForce = 0.4
	
	
	//process & viewing
	val trainProcessCount = 500
	val viewProcessCount = 10
	val processCount = if(isTraining) trainProcessCount else viewProcessCount
	val processUnit = 1.0
	val frameInterval = 20L
	val viewPadding = 30.0
	val stageSize = dishSize + 2.0 * viewPadding
	
}