package zkl.AIGames.germs.tf

import zkl.tools.math.Point2D
import zkl.tools.math.pointOf
import java.io.DataInputStream
import java.io.DataOutputStream

fun DataInputStream.readPoint2D(): Point2D {
	return pointOf(readFloat().toDouble(), readFloat().toDouble())
}
fun DataInputStream.readFloats(): FloatArray {
	val size = readInt()
	return FloatArray(size) { readFloat() }
}
fun DataInputStream.readPoint2Ds(): Array<Point2D> {
	val size = readInt()
	return Array<Point2D>(size) { readPoint2D() }
}

fun DataOutputStream.writeFloats(floatArray: List<Float>) {
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