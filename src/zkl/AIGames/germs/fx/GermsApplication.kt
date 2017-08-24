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
import zkl.AIGames.germs.logic.randomPoint2D
import zkl.tools.math.MT
import zkl.tools.math.pointOf
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
		stage.setOnHidden {
			stopProcess()
			dish.finalize()
		}
		stage.show()
	}
	private fun initDishView(): Parent {
		dishView = DishView()
		
		rootNote = Pane(dishView)
		rootNote.setOnMouseClicked {
			initLogic()
			startProcess()
			rootNote.setOnMouseClicked { e->
				synchronized(dish) {
					val clickPosition = pointOf(e.x,e.y)
					repeat(10){
						val position = clickPosition + randomPoint2D(30.0)
						val amount = MT.random(Conf.nutrientAmountRange.start, Conf.nutrientAmountRange.endInclusive)
						dish.putNutrient(amount, position)
					}
				}
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
			e.printStackTrace()
			Platform.runLater {
				stopProcess()
			}
		}
		
	}
	
	
	
	//logic
	val dish = Dish()
	var lastTimePutNutrient = 0.0
	var lastTimeRunActor = 0.0
	var energyLogs = ArrayList<Double>(Conf.energyLogBufferSize)
	private fun initLogic(){
		dish.initialize()
		dish.putGerm(Conf.germCount)
	}
	private fun processLogic(){
		repeat(Conf.processCount){
			dish.process()
			if (dish.processedTime - lastTimePutNutrient > Conf.nutrientInterval) {
				dish.putRandomNutrients()
				lastTimePutNutrient = dish.processedTime
			}
			if (dish.processedTime - lastTimeRunActor > Conf.actInterval) {
				dish.runActor(Conf.isTraining)
				lastTimeRunActor = dish.processedTime
			}
			if (Conf.isTraining) {
				dish.maintainLogs()
				dish.trainActor()
			}
			
			energyLogs.add(dish.getAverageEnergy())
			if (energyLogs.size > Conf.energyLogBufferSize) {
				println("average energy: ${energyLogs.average()}")
				if (Conf.isTraining) println("trained sample [${dish.trainedCount}]")
				energyLogs.clear()
			}
		}
		
	}
	
}

