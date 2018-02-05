package zkl.aiGames.germs

import zkl.aiGames.germs.logic.Germ


object Conf {
	
	//basic
	val dishSize = 400.0
	
	val germCount = 10
	val germRadius = 20.0
	
	val nutrientInterval = 1e4 / (dishSize * germCount) / 0.05
	val nutrientMaxCount = germCount * 1.0
	val nutrientAmountRange = 10.0..10.0
	val nutrientMaxVelocity = 0.05
	val nutrientDisturbForce = 1e-4
	
	val feelGermMax = 3.0
	val feelGermScale = 0.5e4
	val feelNutrientMax = 3.0
	val feelNutrientScale = 1e3
	val feelWallScale = 0.3e4
	
	
	//training
	val germEnergyCost = { germ: Germ ->
		germ.velocity.run { x * x + y * y } * 0.10
	}
	val germRealLoss = { actTimeEnergy: Double, hopeTimeEnergy: Double ->
		((actTimeEnergy - hopeTimeEnergy) * 0.01).coerceIn(-1.0, 1.0)
	}
	val logBufferSize = 50
	val energyLogBufferSize = 1000
	
	val isTraining = false
	val trainCritic = true
	val trainActor = true
	val actInterval = 100.0
	val hopeCount = 4
	val disturbRate = 0.5..1.0
	val disturbForce = 0.3
	
	
	//process & viewing
	val trainProcessCount = 100
	val viewProcessCount = 5
	val processCount = if (isTraining) trainProcessCount else viewProcessCount
	val processUnit = 1.0
	val frameInterval = 20L
	val viewPadding = 30.0
	val stageSize = dishSize + 2.0 * viewPadding
	
}