package zkl.aiGames.germs.nerveCore

import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket


class ClientNerveCore : NerveCore {
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
		val socket = Socket("localhost", 8081)
		dataInput = DataInputStream(socket.getInputStream())
		dataOutput = DataOutputStream(socket.getOutputStream())
		
		dataOutput.writeInt(COM_INITIALIZE)
		dataOutput.flush()
		
		val status = dataInput.readInt()
		if (status != STU_SUCCEED) throw OperationFailedException("initialize", status)
	}
	
	override fun finalize(saveGraph: Boolean) {
		dataOutput.writeInt(COM_FINALIZE)
		dataOutput.writeInt(if (saveGraph) 0 else 1)
		dataOutput.flush()
		
		val status = dataInput.readInt()
		if (status != STU_SUCCEED) throw OperationFailedException("finalize", status)
	}
	
	override fun runActor(feels: List<GermFeel>): List<GermAct> {
		dataOutput.writeInt(COM_RUN_ACTOR)
		dataOutput.writeList(feels, DataOutputStream::writeGermFeel)
		dataOutput.flush()
		
		val status = dataInput.readInt()
		if (status != STU_SUCCEED) throw OperationFailedException("runActor", status)
		
		return dataInput.readList(DataInputStream::readGermAct)
	}
	
	override fun trainCritic(germLog: List<GermLog>) {
		dataOutput.writeInt(COM_TRAIN_CRITIC)
		dataOutput.writeList(germLog, DataOutputStream::writeGermLog)
		dataOutput.flush()
		
		val status = dataInput.readInt()
		if (status != STU_SUCCEED) throw OperationFailedException("trainCritic", status)
	}
	
	override fun trainActor(feels: List<GermFeel>) {
		dataOutput.writeInt(COM_TRAIN_ACTOR)
		dataOutput.writeList(feels, DataOutputStream::writeGermFeel)
		dataOutput.flush()
		
		val status = dataInput.readInt()
		if (status != STU_SUCCEED) throw OperationFailedException("trainActor", status)
	}
	
}