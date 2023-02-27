package strongfloat

import chisel3._
import chisel3.internal.firrtl.Width

abstract class Float extends Bundle with Num[Float] with FloatNum[Float]

object Float {
    implicit class fromIntToExpWidth(int: Int) {
        def E: ExponentWidth = new ExponentWidth(int)
    }

    implicit class fromIntToFloat(int: Int) {
        def F(expWidth: Int, sigWidth: Int): Float = {
            ???
        }
    }

    def apply(expWidth: ExponentWidth, sigWidth: Width): BHF_Float = {
        new BHF_Float(expWidth, sigWidth)
    }
}
