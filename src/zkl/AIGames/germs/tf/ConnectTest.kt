package zkl.AIGames.germs.tf

import zkl.tools.math.pointOf
import java.io.DataInputStream
import java.io.DataOutputStream
import java.util.*
import kotlin.concurrent.thread
import kotlin.reflect.jvm.internal.impl.utils.StringsKt

fun main(args: Array<String>) {
	testRunActor()
}

fun testBasic() {
	println("running python connectTest.py")
	val process = Runtime.getRuntime().exec("python ./py/connectTest.py")
	val dataInput = DataInputStream(process.inputStream)
	val dataOutput = DataOutputStream(process.outputStream)
	println()
	
	
	val theFloats = listOf(1.23f, 4.56f, 7.89f, 10.0f)
	val theFloatsStr = "[" + StringsKt.join(theFloats.asIterable(), ",") + "]"
	println("writing floats: $theFloatsStr")
	dataOutput.writeFloats(theFloats)
	dataOutput.flush()
	println()
	
	println("waiting return")
	val returnFloats = dataInput.readFloats()
	val returnFloatsStr = "[" + StringsKt.join(returnFloats.asIterable(), ",") + "]"
	println("return floats:" + returnFloatsStr)
	println()
	
	println("waiting for exit")
	process.waitFor()
	println("test exit")
}

fun testRunActor() {
	println("running python main.py")
	val process = Runtime.getRuntime().exec("python ./py/main.py")
	val dataInput = DataInputStream(process.inputStream)
	val dataOutput = DataOutputStream(process.outputStream)
	thread{
		try {
			val errScanner = Scanner(process.errorStream)
			while (true) {
				System.err.println(errScanner.nextLine())
			}
		}catch (e:Exception){}
	}
	println()
	
	
	val feel_nutrients = arrayOf(pointOf(0.1, 0.2), pointOf(-0.3, 0.1))
	val feel_germs = arrayOf(pointOf(0.2, 0.2), pointOf(0.5, 0.0))
	
	println("command initialize")
	dataOutput.writeInt(0)
	dataOutput.flush()
	val status1 = dataInput.readInt()
	println("-> status = $status1")
	
	println("command runActor")
	dataOutput.writeInt(2)
	dataOutput.writePoint2Ds(feel_nutrients.asList())
	dataOutput.writePoint2Ds(feel_germs.asList())
	dataOutput.flush()
	val status2 = dataInput.readInt()
	println("-> status = $status2")
	val act_velocity = dataInput.readPoint2Ds()
	act_velocity.forEach { println("(${it.x},${it.y})") }
	println()
	
	println("command finalize")
	dataOutput.writeInt(1)
	dataOutput.flush()
	val status3 = dataInput.readInt()
	println("-> status = $status3")
}

fun testTrainCritic(){
	println("running python main.py")
	val process = Runtime.getRuntime().exec("python ./py/main.py")
	val dataInput = DataInputStream(process.inputStream)
	val dataOutput = DataOutputStream(process.outputStream)
	thread {
		try {
			val errScanner = Scanner(process.errorStream)
			while (true) {
				System.err.println(errScanner.nextLine())
			}
		} catch (e: Exception) {
		}
	}
	println()
	
	println("command initialize")
	dataOutput.writeInt(0)
	dataOutput.flush()
	val status1 = dataInput.readInt()
	println("-> status = $status1")
	
	println("command trainCritic")
	dataOutput.writeInt(3)
	val feel_nutrients = arrayListOf(pointOf(0.1, 0.2), pointOf(0.3, 0.1))
	val feel_germs = arrayListOf(pointOf(0.2, 0.2), pointOf(0.5, 0.0))
	val act_velocity = arrayListOf(pointOf(0.4, 0.4), pointOf(-0.5, 0.0))
	val real_loss = listOf(0.1f, 1.2f)
	dataOutput.writePoint2Ds(feel_nutrients)
	dataOutput.writePoint2Ds(feel_germs)
	dataOutput.writePoint2Ds(act_velocity)
	dataOutput.writeFloats(real_loss)
	dataOutput.flush()
	val status2 = dataInput.readInt()
	println("-> status = $status2")
	println()
	
	println("command finalize")
	dataOutput.writeInt(1)
	dataOutput.flush()
	val status3 = dataInput.readInt()
	println("-> status = $status3")
}


