package zkl.AIGames.germs.fx

import javafx.application.Application
import javafx.application.Platform
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.stage.Stage
import zkl.AIGames.germs.Conf
import zkl.AIGames.germs.logic.Dish
import kotlin.concurrent.thread

fun main(args: Array<String>) {
	Application.launch(GermsApplication::class.java,*args)
}

class GermsApplication : Application() {
	
	lateinit var stage: Stage
	lateinit var rootNote: Pane
	override fun start(stage: Stage) {
		this.stage = stage
		initStage()
	}
	
	
	//init
	private fun initStage() {
		stage.scene = Scene(initRootNote(), Conf.dishSize, Conf.dishSize)
		stage.title = "germs"
		stage.isIconified = false
		stage.setOnHidden { wantProcess=false }
		stage.show()
	}
	private fun initRootNote(): Parent {
		rootNote = Pane()
		rootNote.setOnMouseClicked {
			rootNote.onMouseClicked = null
			initContent()
			startProcess()
		}
		rootNote.background= Background(BackgroundFill(Color.DARKGRAY,null,null))
		return rootNote
	}
	private fun initContent(){
		dish.initialize()
	}
	
	
	var wantProcess:Boolean=false
	var processThread:Thread?=null
	private fun startProcess(){
		stopProcess()
		wantProcess=true
		processThread= thread {
			while (wantProcess) {
					onProcess()
				try {
					Thread.sleep(Conf.frameInterval)
				} catch (e: InterruptedException) {
					break
				}
			}
		}
	}
	private fun stopProcess(){
		wantProcess =false
		processThread?.join()
	}
	private fun onProcess() {
		for (i in 0 until Conf.processCount) {
			dish.process()
		}
		Platform.runLater {
			updateFrame()
		}
	}
	
	
	//draw
	val germViewBuffers = ArrayList<GermView>()
	val nutrientViewBuffers = ArrayList<NutrientView>()
	private fun updateFrame(){
		if (germViewBuffers.size < dish.germs.size) {
			for (i in germViewBuffers.size .. dish.germs.size){
				val germView = GermView()
				germView.isVisible=false
				rootNote.children.add(germView)
				germViewBuffers.add(germView)
			}
		}
		if (nutrientViewBuffers.size < dish.nutrients.size) {
			for (i in nutrientViewBuffers.size .. dish.nutrients.size){
				val nutrientView = NutrientView()
				nutrientView.isVisible=false
				rootNote.children.add(nutrientView)
				nutrientViewBuffers.add(nutrientView)
			}
		}
		
		germViewBuffers.forEachIndexed{index,view->
			if (index < dish.germs.size) {
				view.update(dish.germs[index])
				view.isVisible=true
			}else{
				view.isVisible=false
			}
		}
		nutrientViewBuffers.forEachIndexed{index,view->
			if (index < dish.nutrients.size) {
				view.update(dish.nutrients[index])
				view.isVisible=true
			}else{
				view.isVisible=false
			}
		}
	}
	
	
	//logic
	val dish = Dish()
	
}

