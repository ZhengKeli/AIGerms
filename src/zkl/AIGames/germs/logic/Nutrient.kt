package zkl.AIGames.germs.logic

import zkl.AIGames.germs.Conf
import zkl.tools.math.InstantPoint2D
import zkl.tools.math.MT


class Nutrient {
	
	val position: InstantPoint2D = InstantPoint2D()
	val velocity: InstantPoint2D = InstantPoint2D()
	var amount: Double = 1.0
	
	
	fun process(dish: Dish, time: Double) {
		velocity.offset(MT.randomMirror(1.0),MT.randomMirror(1.0))
		
		val velocityAbsolute = velocity.absolute()
		if (velocityAbsolute > Conf.maxGermVelocity) {
			velocity /= velocityAbsolute * Conf.maxGermVelocity
		}
		
		position += velocity * time
		position.set(
			x = MT.valueLimit(position.x, 0.0, dish.width),
			y = MT.valueLimit(position.y, 0.0, dish.height))
	}
	
}