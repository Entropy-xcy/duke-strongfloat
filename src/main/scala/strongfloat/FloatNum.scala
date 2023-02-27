package strongfloat

import Chisel.Data
import chisel3.util.DecoupledIO
import chisel3.{Bundle, UInt}

trait FloatNum[T <: Data] {
    // Async Square Root
    def ~/-(that: T): DecoupledIO[T]

    // Async Division
    def ~/(that: T): DecoupledIO[T]

    // Async Multiplication
    def ~*(that: T): DecoupledIO[T]

    // Async Addition
    def ~+(that: T): DecoupledIO[T]

    // Async Subtraction
    def ~-(that: T): DecoupledIO[T]

    // Async FMA
    def ~*+(that: T, acc: T): DecoupledIO[T]

    def fromUInt(x: UInt): T

    def roundToUInt: UInt

    def *+(coeff: T, acc: T): T

    def :==(that: T): Unit
}
