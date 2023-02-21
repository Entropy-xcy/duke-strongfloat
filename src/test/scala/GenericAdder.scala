import Float.fromIntToExpWidth
import chisel3._
import chisel3.util._
import hardfloat.{AddRecFN, fNFromRecFN, recFNFromFN}

class GenericAdder[T <: Data with Num[T]](gen: => T) extends Module {
    val io = IO(new Bundle {
        val a = Input(gen)
        val b = Input(gen)
        val c = Output(gen)
    })

    io.c := io.a + io.b
}

object GenericAdderBuild extends App {
    (new chisel3.stage.ChiselStage).emitVerilog(
        new GenericAdder(Float(8.E, 24.W))
    )
    (new chisel3.stage.ChiselStage).emitVerilog(
        new GenericAdder(UInt(8.W))
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
        val a = Input(Float(8.E, 24.W))
        val b = Input(Float(8.E, 24.W))
        val y = Output(Float(8.E, 24.W))
    })

    io.y := io.a + io.b
}

