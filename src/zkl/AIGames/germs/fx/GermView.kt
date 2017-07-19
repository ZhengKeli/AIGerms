package zkl.AIGames.germs.fx

import javafx.scene.Parent
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Line
import zkl.AIGames.germs.Conf
import zkl.AIGames.germs.logic.Germ

class GermView : Parent() {
	
	val circle_body = Circle()
	val line_velocity = Line()
	init {
		
		circle_body.fill = Color.WHITE
		circle_body.radius = Conf.germRadius
		
		line_velocity.strokeWidth = 3.0
		line_velocity.stroke = Color.RED
		
		this.children.addAll(circle_body,line_velocity)
		
	}
	
	
	fun update(germ: Germ) {
		
		circle_body.centerX = germ.position.x
		circle_body.centerY = germ.position.y
		val colorRate = germ.energy * 0.9 + 0.1
		circle_body.fill = Color(1.0, 1.0, 1.0, colorRate)
		
		line_velocity.startX = germ.position.x
		line_velocity.startY = germ.position.y
		val endPosition = germ.position + germ.velocity *3.0
		line_velocity.endX = endPosition.x
		line_velocity.endY = endPosition.y
		
	}
	
}

