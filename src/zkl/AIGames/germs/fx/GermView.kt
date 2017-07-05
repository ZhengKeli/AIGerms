package zkl.AIGames.germs.fx

import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import zkl.AIGames.germs.Conf
import zkl.AIGames.germs.logic.Germ

class GermView(centerX: Double=0.0, centerY: Double=0.0, radius: Double = Conf.germRadius)
	: Circle(centerX,centerY, radius, Color.WHITE){
	fun update(germ: Germ) {
		centerX = germ.position.x
		centerY = germ.position.y
		
		val colorRate = (germ.energy/Conf.maxEnergy) * 0.9 + 0.1
		fill = Color(1.0, 1.0, 1.0, colorRate)
	}
	
}

