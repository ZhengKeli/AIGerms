package zkl.AIGames.germs.logic

import zkl.AIGames.germs.Conf
import zkl.tools.math.InstantPoint2D
import zkl.tools.math.MT

class Germ {
	val position = InstantPoint2D()
	val velocity = InstantPoint2D()
	var energy: Double = 1.0
		set(value) { field = MT.valueLimit(value, 0.0, 1.0) }
	
	fun updateVelocity(){
		velocity.x+=MT.randomMirror(1.0)
		velocity.y+=MT.randomMirror(1.0)
	}
	
	fun process(time: Double) {
		updateVelocity()
		
		val velocityAbsolute = velocity.absolute()
		if (velocityAbsolute > Conf.maxGermVelocity) {
			velocity /= velocityAbsolute * Conf.maxGermVelocity
		}
		
		position += velocity * time
		
		energy -= Conf.movingEnergyCost * velocityAbsolute * time
		energy -= Conf.staticEnergyCost * time
	}
	
}
