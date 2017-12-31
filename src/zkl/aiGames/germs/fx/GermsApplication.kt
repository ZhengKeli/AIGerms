package zkl.aiGames.germs.fx

import javafx.application.Application
import javafx.application.Platform
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.stage.Stage
import zkl.aiGames.germs.Conf
import zkl.aiGames.germs.logic.Dish
import zkl.aiGames.germs.logic.randomPoint2D
import zkl.aiGames.germs.nerveCore.NerveCore
import zkl.tools.math.MT
import zkl.tools.math.pointOf
import kotlin.concurrent.thread

fun main(args: Array<String>) {
	Application.launch(GermsApplication::class.java, *args)
}

class GermsApplication : Application() {
	
	//ui
	lateinit var stage: Stage
	lateinit var rootNote: Pane
	lateinit var dishView: DishView
	override fun start(stage: Stage) {
		this.stage = stage
		initStage()
	}
	
	private fun initStage() {
		stage.scene = Scene(initDishView(), Conf.stageSize, Conf.stageSize)
		stage.title = "germs"
		stage.isIconified = false
		stage.setOnHidden {
			stopProcess()
			nerveCore.finish()
		}
		stage.show()
	}
	
	private fun initDishView(): Parent {
		dishView = DishView()
		
		rootNote = Pane(dishView)
		rootNote.setOnMouseClicked {
			initDish()
			startProcess()
			rootNote.setOnMouseClicked { e ->
				synchronized(dish) {
					val clickPosition = pointOf(e.x - Conf.viewPadding, e.y - Conf.viewPadding)
					repeat(10) {
						val position = clickPosition + randomPoint2D(30.0)
						val amount = MT.random(Conf.nutrientAmountRange.start, Conf.nutrientAmountRange.endInclusive)
						dish.putNutrient(amount, position)
					}
				}
			}
		}
		rootNote.background = Background(BackgroundFill(Color.DARKGRAY, null, null))
		
		return rootNote
	}
	
	private fun updateFrame() {
		dishView.update(dish)
	}
	
	
	//process
	var wantProcess: Boolean = false
	var processThread: Thread? = null
	private fun startProcess() {
		stopProcess()
		wantProcess = true
		processThread = thread {
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
	
	private fun stopProcess() {
		wantProcess = false
		processThread?.join()
	}
	
	private fun onProcess() {
		try {
			processDish()
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
	
	
	//nerveCore
	val nerveCore = NerveCore()
	
	
	//logic
	val dish = Dish(nerveCore, Conf.dishSize)
	var lastTimePutNutrient = 0.0
	var lastTimeRunActor = 0.0
	var energyLogs = ArrayList<Double>(Conf.energyLogBufferSize)
	
	private fun initDish() {
		dish.putGerm(Conf.germCount)
	}
	
	private fun processDish() {
		repeat(Conf.processCount) {
			if (dish.processedTime - lastTimePutNutrient >= Conf.nutrientInterval) {
				dish.putRandomNutrients()
				lastTimePutNutrient = dish.processedTime
			}
			if (dish.processedTime - lastTimeRunActor >= Conf.actInterval) {
				dish.runActor(Conf.isTraining)
				lastTimeRunActor = dish.processedTime
			}
			
			dish.process()
			
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

