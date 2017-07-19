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
	
	//ui
	lateinit var stage: Stage
	lateinit var rootNote: Pane
	lateinit var dishView:DishView
	override fun start(stage: Stage) {
		this.stage = stage
		initStage()
	}
	private fun initStage() {
		stage.scene = Scene(initDishView(), Conf.dishSize, Conf.dishSize)
		stage.title = "germs"
		stage.isIconified = false
		stage.setOnHidden { wantProcess=false }
		stage.show()
	}
	private fun initDishView(): Parent {
		dishView = DishView()
		
		rootNote = Pane(dishView)
		rootNote.setOnMouseClicked {
			if(!wantProcess){
				initLogic()
				startProcess()
			}else{
				stopProcess()
				dish.nerveCore.finalize()
				println("graph saved")
				stage.close()
			}
		}
		rootNote.background= Background(BackgroundFill(Color.DARKGRAY,null,null))
		
		return rootNote
	}
	
	private fun updateFrame(){
		dishView.update(dish)
	}
	
	
	
	//process
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
		try {
			processLogic()
			Platform.runLater {
				updateFrame()
			}
		} catch (e: Exception) {
			Platform.runLater {
				stopProcess()
			}
		}
		
	}
	
	
	
	//logic
	val dish = Dish()
	val lastTimePutNutrient = 0.0
	private fun initLogic(){
		dish.initialize()
		dish.putGerm(Conf.germCount)
	}
	private fun processLogic(){
		for (i in 0 until Conf.processCount) {
			dish.process()
		}
		if (dish.processedTime - lastTimePutNutrient > Conf.nutrientInterval) {
			dish.putNutrient()
		}
	}
	
}

