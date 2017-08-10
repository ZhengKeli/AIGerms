package zkl.AIGames.germs.nerveCore

import zkl.AIGames.germs.logic.GermFeel
import zkl.AIGames.germs.logic.GermLog
import zkl.tools.math.Point2D
import zkl.tools.math.pointOf
import java.io.DataInputStream
import java.io.DataOutputStream
import kotlin.concurrent.thread


class TFNerveCore : NerveCore {
	lateinit var process: Process
	lateinit var dataInput: DataInputStream
	lateinit var dataOutput: DataOutputStream
	
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
		thread(name = "pythonErrorThread") {
			process.errorStream.bufferedReader().use {
				while(true){
					val line: String = it.readLine() ?: break
					System.err.println("[python] $line")
				}
			}
			System.err.println("[python] exited")
		}
		
		
		dataOutput.writeInt(COM_INITIALIZE)
		dataOutput.flush()
		
		val status = dataInput.readInt()
		if (status != STU_SUCCEED) throw OperationFailedException("initialize", status)
	}
	
	override fun finalize(saveGraph:Boolean) {
		dataOutput.writeInt(COM_FINALIZE)
		dataOutput.writeInt(if (saveGraph) 0 else 1)
		dataOutput.flush()
		
		val status = dataInput.readInt()
		if (status != STU_SUCCEED) throw OperationFailedException("finalize", status)
		
		process.waitFor()
	}
	
	override fun runActor(feels:List<GermFeel>):List<Point2D>{
		dataOutput.writeInt(COM_RUN_ACTOR)
		dataOutput.writeList(feels,DataOutputStream::writeGermFeel)
		dataOutput.flush()
		
		val status = dataInput.readInt()
		if (status != STU_SUCCEED) throw OperationFailedException("runActor", status)
		
		return dataInput.readList(DataInputStream::readPoint2D)
	}
	
	override fun trainCritic(germLog:List<GermLog>) {
		dataOutput.writeInt(COM_TRAIN_CRITIC)
		dataOutput.writeList(germLog, DataOutputStream::writeGermLog)
		dataOutput.flush()
		
		val status = dataInput.readInt()
		if (status != STU_SUCCEED) throw OperationFailedException("trainCritic", status)
	}
	
	override fun trainActor(feels:List<GermFeel>) {
		dataOutput.writeInt(COM_TRAIN_ACTOR)
		dataOutput.writeList(feels,DataOutputStream::writeGermFeel)
		dataOutput.flush()
		
		val status = dataInput.readInt()
		if (status != STU_SUCCEED) throw OperationFailedException("trainActor", status)
	}
	
}

class OperationFailedException(operation: String, status:Int)
	: NerveCoreException("The operation  \"$operation\" returned failed status $status")


fun <T> DataOutputStream.writeList(list:List<T>,encoder:DataOutputStream.(T)->Unit){
	writeInt(list.size)
	list.forEach { this.encoder(it) }
}
fun DataOutputStream.writePoint2D(point: Point2D) {
	writeFloat(point.x.toFloat())
	writeFloat(point.y.toFloat())
}
fun DataOutputStream.writeGermFeel(germFeel: GermFeel) {
	writePoint2D(germFeel.nutrient)
	writePoint2D(germFeel.germ)
	writeFloat(germFeel.energy.toFloat())
}
fun DataOutputStream.writeGermLog(germLog: GermLog) {
	writeGermFeel(germLog.feel)
	writePoint2D(germLog.actVelocity)
	writeFloat(germLog.realLoss.toFloat())
}

fun <T> DataInputStream.readList(decoder:DataInputStream.()->T): List<T> {
	val size = readInt()
	val list = ArrayList<T>(size)
	for (i in 0 until size) {
		list.add(this.decoder())
	}
	return list
}
fun DataInputStream.readPoint2D(): Point2D {
	return pointOf(readFloat().toDouble(), readFloat().toDouble())
}
