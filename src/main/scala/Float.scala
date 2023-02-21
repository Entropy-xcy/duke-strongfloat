import Chisel.Cat
import chisel3._
import chisel3.internal.firrtl.Width
import chisel3.internal.sourceinfo.SourceInfo
import hardfloat._

sealed abstract class Float(val expWidth: Int, val sigWidth: Int) extends Bundle with Num[Float] with FloatNum[Float]


object Float {
    implicit class fromIntToExpWidth(int: Int) {
        def E: ExponentWidth = new ExponentWidth(int)
    }

    implicit class fromIntToFloat(int: Int) {
        def F(expWidth: Int, sigWidth: Int): Float = {
            ???
        }
    }

    def apply(expWidth: ExponentWidth, sigWidth: Width): Float = {
        BHF_Float(expWidth, sigWidth)
    }
}
