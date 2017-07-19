package zkl.AIGames.germs.fx

import javafx.scene.Parent
import zkl.AIGames.germs.logic.Dish


class DishView:Parent() {
	
	val germViewBuffers = ArrayList<GermView>()
	val nutrientViewBuffers = ArrayList<NutrientView>()
	
	fun update(dish: Dish) {
		if (germViewBuffers.size < dish.germs.size) {
			for (i in germViewBuffers.size..dish.germs.size) {
				val germView = GermView()
				germView.isVisible = false
				this.children.add(germView)
				germViewBuffers.add(germView)
			}
		}
		if (nutrientViewBuffers.size < dish.nutrients.size) {
			for (i in nutrientViewBuffers.size..dish.nutrients.size) {
				val nutrientView = NutrientView()
				nutrientView.isVisible = false
				this.children.add(nutrientView)
				nutrientViewBuffers.add(nutrientView)
			}
		}
		
		germViewBuffers.forEachIndexed { index, view ->
			if (index < dish.germs.size) {
				view.update(dish.germs[index])
				view.isVisible = true
			} else {
				view.isVisible = false
			}
		}
		nutrientViewBuffers.forEachIndexed { index, view ->
			if (index < dish.nutrients.size) {
				view.update(dish.nutrients[index])
				view.isVisible = true
			} else {
				view.isVisible = false
			}
		}
		
	}
	
}