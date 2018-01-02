package zkl.aiGames.germs.nerveCore

import zkl.tools.math.Point2D
import zkl.tools.math.pointOf
import java.io.DataInputStream
import java.io.DataOutputStream

fun <T> DataOutputStream.writeList(list: List<T>, encoder: DataOutputStream.(T) -> Unit) {
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
	writePoint2D(germFeel.wall)
}

fun DataOutputStream.writeGermAct(germAct: GermAct) {
	writePoint2D(germAct.velocity)
}

fun DataOutputStream.writeGermLog(germLog: GermLog) {
	writeGermFeel(germLog.feel)
	writeGermAct(germLog.act)
	writeFloat(germLog.realLoss.toFloat())
}

fun <T> DataInputStream.readList(decoder: DataInputStream.() -> T): List<T> {
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

fun DataInputStream.readGermAct(): GermAct {
	return GermAct(readPoint2D())
}