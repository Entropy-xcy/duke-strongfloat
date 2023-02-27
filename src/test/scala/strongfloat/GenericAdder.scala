package strongfloat

import chisel3._
import chisel3.util.Decoupled
import strongfloat.Float.fromIntToExpWidth

class GenericAdder[T <: Data with Num[T]](gen: => T) extends Module {
    val io = IO(new Bundle {
        val a = Input(gen)
        val b = Input(gen)
        val c = Output(gen)
    })

    io.c := io.a + io.b
}

object GenericAdderBuild extends App {
    val floatOptions = FloatOptions(
        FPULibrary.BerkeleyHardFloat,
        RoundingMode.RoundNearestEven
    )
    (new chisel3.stage.ChiselStage).emitVerilog(
        // Float takes the floatOptions as an implicit parameter
        new GenericAdder(strongfloat.Float(8.E, 24.W))
    )
}

class IntAdder extends Module {
    val io = IO(new Bundle {
        val a = Input(UInt(8.W))
        val b = Input(UInt(8.W))
        val y = Output(UInt(8.W))
    })

    io.y := io.a + io.b
}


class FloatAdder extends Module {
    val io = IO(new Bundle {
        val a = Input(strongfloat.Float(8.E, 24.W))
        val b = Input(strongfloat.Float(8.E, 24.W))
        val y = Output(strongfloat.Float(8.E, 24.W))
    })

    io.y := io.a + io.b
}

