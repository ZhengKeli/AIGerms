package zkl.aiGames.germs.fx

import javafx.scene.Parent
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Line
import zkl.aiGames.germs.Conf
import zkl.aiGames.germs.logic.Germ

class GermView : Parent() {
	
	val circle_body = Circle()
	val line_actVelocity = Line()
	val line_feelNutrient = Line()
	val line_feelGerm = Line()
	init {
		
		circle_body.run {
			fill = Color.WHITE
			radius = Conf.germRadius
		}
		line_actVelocity.run {
			strokeWidth = 3.0
			stroke = Color.RED
		}
		line_feelNutrient.run {
			strokeWidth = 1.0
			stroke = Color.BLUE
		}
		line_feelGerm.run {
			strokeWidth = 1.0
			stroke = Color.WHITE
		}
		
		this.children.addAll(circle_body, line_feelNutrient, line_feelGerm, line_actVelocity)
		
	}
	
	
	fun update(germ: Germ) {
		circle_body.run {
			centerX = germ.position.x
			centerY = germ.position.y
			fill = Color(1.0, 1.0, 1.0, 0.2)
		}
		line_actVelocity.run{
			startX = germ.position.x
			startY = germ.position.y
			val endPosition = germ.position + germ.act.velocity * 20.0
			endX = endPosition.x
			endY = endPosition.y
		}
		line_feelNutrient.run {
			startX = germ.position.x
			startY = germ.position.y
			val endPosition = germ.position + germ.feel.nutrient *10.0
			endX = endPosition.x
			endY = endPosition.y
		}
		line_feelGerm.run {
			startX = germ.position.x
			startY = germ.position.y
			val endPosition = germ.position + germ.feel.germ *10.0
			endX = endPosition.x
			endY = endPosition.y
		}
	}
	
}

