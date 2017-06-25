package zkl.AIGames.germs.tf

import zkl.tools.math.InstantPoint2D
import zkl.tools.math.Point2D
import java.io.DataInputStream
import java.io.DataOutputStream
import kotlin.reflect.jvm.internal.impl.utils.StringsKt

fun main(args: Array<String>) {
	println("running python")
	val process = Runtime.getRuntime().exec("python ./py/connectTest.py")
	val dataInput = DataInputStream(process.inputStream)
	val dataOutput = DataOutputStream(process.outputStream)
	println()
	
	
	val theFloats = floatArrayOf(1.23f, 4.56f, 7.89f, 10.0f)
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
}


fun DataInputStream.readPoint2D(): Point2D {
	return InstantPoint2D(readFloat().toDouble(), readFloat().toDouble())
}

fun DataInputStream.readFloats(): FloatArray {
	val size = readInt()
	return FloatArray(size) { readFloat() }
}
fun DataInputStream.readPoint2Ds(): Array<Point2D> {
	val size = readInt()
	return Array<Point2D>(size) { readPoint2D() }
}

fun DataOutputStream.writeFloats(floatArray: FloatArray) {
	writeInt(floatArray.size)
	floatArray.forEach { writeFloat(it) }
}
fun DataOutputStream.writePoint2Ds(points:Collection<Point2D>) {
	writeInt(points.size)
	points.forEach {
		writeFloat(it.x.toFloat())
		writeFloat(it.y.toFloat())
	}
}


