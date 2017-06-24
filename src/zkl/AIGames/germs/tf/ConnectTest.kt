package zkl.AIGames.germs.tf

import java.io.DataInputStream
import java.io.DataOutputStream
import kotlin.reflect.jvm.internal.impl.utils.StringsKt

fun main(args: Array<String>) {
	println("running python")
	val process = Runtime.getRuntime().exec("python ./py/connectTest.py")
	val dataInput = DataInputStream(process.inputStream)
	val dataOutput = DataOutputStream(process.outputStream)
	println()
	
	
	val theFloats = floatArrayOf(1.23f, 4.56f, 7.89f)
	val theFloatsStr = "[" + StringsKt.join(theFloats.asIterable(), ",") + "]"
	println("writing 3 floats: $theFloatsStr")
	dataOutput.writeFloat(theFloats[0])
	dataOutput.writeFloat(theFloats[1])
	dataOutput.writeFloat(theFloats[2])
	dataOutput.flush()
	println()
	
	println("waiting return")
	val returnFloats = floatArrayOf(dataInput.readFloat(), dataInput.readFloat(), dataInput.readFloat())
	val returnFloatsStr = "[" + StringsKt.join(returnFloats.asIterable(), ",") + "]"
	println("return floats:" + returnFloatsStr)
	println()
	
	println("waiting for exit")
	process.waitFor()
}



