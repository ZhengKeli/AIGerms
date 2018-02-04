package zkl.aiGames.germs.nerveCore

import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket


class NerveCore(host: String = "localhost", port: Int = 8081) {
	
	companion object {
		private val COM_RUN_ACTOR = 1
		private val COM_TRAIN_CRITIC = 2
		private val COM_TRAIN_ACTOR = 3
		private val COM_SAVE_GRAPH = 4
		private val COM_FINISH = 5
		
		private val STU_SUCCEED = 0
		private val STU_FAILED = 1
	}
	
	private val socket = Socket(host, port)
	private val dataInput = DataInputStream(socket.getInputStream())
	private val dataOutput: DataOutputStream = DataOutputStream(socket.getOutputStream())
	
	
	fun runActor(feels: List<GermFeel>): List<GermAct> {
		dataOutput.writeInt(COM_RUN_ACTOR)
		dataOutput.writeList(feels, DataOutputStream::writeGermFeel)
		dataOutput.flush()

		val status = dataInput.readInt()
		if (status != STU_SUCCEED) throw CommandFailedException("runActor", status)

		return dataInput.readList(DataInputStream::readGermAct)
//		return feels.map { GermAct(it.nutrient / 5.0) }
		
	}
	
	fun trainCritic(germLogs: List<GermLog>) {
		dataOutput.writeInt(COM_TRAIN_CRITIC)
		dataOutput.writeList(germLogs, DataOutputStream::writeGermLog)
		dataOutput.flush()
		
		val status = dataInput.readInt()
		if (status != STU_SUCCEED) throw CommandFailedException("trainCritic", status)
	}
	
	fun trainActor(germLogs: List<GermLog>) {
		dataOutput.writeInt(COM_TRAIN_ACTOR)
		dataOutput.writeList(germLogs) { writeList(it.feels, DataOutputStream::writeGermFeel) }
		dataOutput.flush()
		
		val status = dataInput.readInt()
		if (status != STU_SUCCEED) throw CommandFailedException("trainActor", status)
	}
	
	fun saveGraph() {
		dataOutput.writeInt(COM_SAVE_GRAPH)
		dataOutput.flush()
		val status = dataInput.readInt()
		if (status != STU_SUCCEED) throw CommandFailedException("saveGraph", status)
	}
	
	fun finish() {
		dataOutput.writeInt(COM_FINISH)
		dataOutput.flush()
		dataOutput.close()
		dataInput.close()
		socket.close()
	}
	
}

abstract class NerveCoreException(message: String) : Exception(message)
class CommandFailedException(operation: String, status: Int)
	: NerveCoreException("The operation  \"$operation\" returned failed status $status")


