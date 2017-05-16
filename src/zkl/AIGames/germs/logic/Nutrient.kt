package zkl.AIGames.germs.logic

import zkl.AIGames.germs.Conf
import zkl.AIGames.germs.fx.NutrientView
import zkl.tools.math.InstantPoint2D
import zkl.tools.math.MT


class Nutrient{
	
	val position: InstantPoint2D = InstantPoint2D()
	val velocity: InstantPoint2D = InstantPoint2D()
	var amount: Double = 1.0
	
	
	fun process(time: Double) {
		
		velocity.x+= MT.randomMirror(1.0)
		velocity.y+= MT.randomMirror(1.0)
		
		val velocityAbsolute = velocity.absolute()
		if (velocityAbsolute > Conf.maxGermVelocity) {
			velocity /= velocityAbsolute * Conf.maxGermVelocity
		}
		
		position += velocity * time
		
	}
	
}