package zkl.AIGames.germs.logic

import zkl.AIGames.germs.Conf
import zkl.tools.math.InstantPoint2D
import zkl.tools.math.MT
import zkl.tools.math.Point2D

class Germ {
	val position = InstantPoint2D()
	val velocity = InstantPoint2D()
	var energy: Double = 1.0
		set(value) { field = MT.valueLimit(value, 0.0, 1.0) }
	
	fun updateVelocity(dish: Dish){
		val feel = feel(dish)
		velocity.offset(feel.nutrientField*0.5e4)
		velocity/=1.003
	}
	
	fun process(dish: Dish, time: Double) {
		updateVelocity(dish)
		
		val velocityAbsolute = velocity.absolute()
		if (velocityAbsolute > Conf.maxGermVelocity) {
			velocity.set(velocity / velocityAbsolute * Conf.maxGermVelocity)
		}
		
		position += velocity * time
		position.set(
			x = MT.valueLimit(position.x, 0.0, dish.width),
			y = MT.valueLimit(position.y, 0.0, dish.height))
		
		energy -= (Conf.movingEnergyCost * velocityAbsolute + Conf.staticEnergyCost) * time
	}
	
	fun feel(dish: Dish):FeelResult{
		val germFiled = InstantPoint2D()
		dish.germs.forEach {
			val d = it.position - this.position
			val r = d.absolute()
			val m = Conf.germFieldConstant / r / r
			germFiled.offset(
				x = m * d.x / r,
				y = m * d.y / r)
		}
		
		val nutrientFiled = InstantPoint2D()
		dish.nutrients.forEach {
			val d = it.position - this.position
			val r = d.absolute()
			val m = Conf.nutrientFieldConstant*it.amount / r / r
			nutrientFiled.offset(
				x = m * d.x / r,
				y = m * d.y / r)
		}
		nutrientFiled.offset(MT.randomMirror(1e-5),MT.randomMirror(1e-5))
		
		return FeelResult(nutrientFiled, germFiled, this.energy)
	}
	
	data class FeelResult(val nutrientField: Point2D, val germField: Point2D,val energy:Double)
	
	
}
