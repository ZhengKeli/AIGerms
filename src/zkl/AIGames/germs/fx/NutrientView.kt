package zkl.AIGames.germs.fx

import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import zkl.AIGames.germs.logic.Nutrient

class NutrientView(centerX: Double = 0.0, centerY: Double = 0.0, radius: Double = 5.0)
	: Circle(centerX, centerY, radius, Color.BLUE) {
	fun update(nutrient: Nutrient) {
		centerX = nutrient.position.x
		centerY = nutrient.position.y
		radius = Math.sqrt(nutrient.amount)*5.0
	}
	
}

