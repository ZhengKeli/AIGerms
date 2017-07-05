package zkl.AIGames.germs.tf

import zkl.AIGames.germs.logic.NerveCore
import zkl.tools.math.Point2D
import java.io.DataInputStream
import java.io.DataOutputStream


class TFNerveCore : NerveCore {
	lateinit var process:Process
	lateinit var dataInput:DataInputStream
	lateinit var dataOutput:DataOutputStream
	
	val COM_INITIALIZE = 0
	val COM_FINALIZE = 1
	val COM_RUN_ACTOR = 2
	val COM_TRAIN_CRITIC = 3
	val COM_TRAIN_ACTOR = 4
	
	val STU_SUCCEED = 0
	val STU_FAILED = 1
	
	override fun initialize() {
		process = Runtime.getRuntime().exec("python ./py/main.py")
		dataInput = DataInputStream(process.inputStream)
		dataOutput = DataOutputStream(process.outputStream)
		
		dataOutput.writeInt(COM_INITIALIZE)
		dataOutput.flush()
		dataInput.readInt()
	}
	
	override fun finalize() {
		dataOutput.writeInt(COM_FINALIZE)
		dataOutput.flush()
		dataInput.readInt()
		process.waitFor()
	}
	
	override fun runActor(feelNutrient: List<Point2D>,feelGerms: List<Point2D>): List<Point2D> {
		dataOutput.writeInt(COM_RUN_ACTOR)
		dataOutput.writePoint2Ds(feelNutrient)
		dataOutput.writePoint2Ds(feelGerms)
		dataOutput.flush()
//		val status = dataInput.readInt()
		val pointsArray = dataInput.readPoint2Ds()
		return pointsArray.asList()
	}
	
	override fun trainCritic(feelNutrient: List<Point2D>, feelGerm: List<Point2D>,
	                         actVelocity: List<Point2D>, realLoss: List<Float>) {
		dataOutput.writeInt(COM_TRAIN_CRITIC)
		dataOutput.writePoint2Ds(feelNutrient)
		dataOutput.writePoint2Ds(feelGerm)
		dataOutput.writePoint2Ds(actVelocity)
		dataOutput.writeFloats(realLoss)
		dataOutput.flush()
//		val status = dataInput.readInt()
	}
	
	override fun trainActor(feelNutrient: List<Point2D>, feelGerm: List<Point2D>) {
		dataOutput.writeInt(COM_TRAIN_ACTOR)
		dataOutput.writePoint2Ds(feelNutrient)
		dataOutput.writePoint2Ds(feelGerm)
		dataOutput.flush()
//		val status = dataInput.readInt()
	}
}
