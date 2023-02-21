import chisel3.UInt

trait FloatNum[T] {
    // Square Root
    def /-(that: T): T

    def fromUInt(x: UInt): T

    def roundToUInt: UInt

    def *+(coeff: T, acc: T): T

    def :==(that: T): Unit
}
