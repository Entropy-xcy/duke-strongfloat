import Float.{fromIntToExpWidth, fromIntToFloat}
import chisel3._
import chiseltest._
import chiseltest.simulator.WriteVcdAnnotation
import org.scalatest.flatspec.AnyFlatSpec
import hardfloat._


class TestFloatAdder[T <: Data](gen: => T with Num[T]) extends Module {
    val io = IO(new Bundle {
        val a = Input(gen)
        val b = Input(gen)
        val c = Output(gen)
    })

    io.c := io.a + io.b
}

class TestFloatAdderRaw extends Module {
    val io = IO(new Bundle {
        val a = Input(UInt(32.W))
        val b = Input(UInt(32.W))
        val c = Output(UInt(32.W))
        val c_mirrow = Output(BHF_Float(8.E, 24.W))
    })

    // Use Berkeley Hardfloat
    val rec_this = recFNFromFN(8, 24, io.a)
    val rec_that = recFNFromFN(8, 24, io.b)
    val adder = chisel3.Module(new AddRecFN(8, 24))
    adder.io.subOp := 0.U
    adder.io.a := rec_this
    adder.io.b := rec_that
    adder.io.roundingMode := 0.U
    adder.io.detectTininess := 0.U
    val out_rec_float = adder.io.out
    val out_fn = fNFromRecFN(8, 24, out_rec_float)
    io.c := out_fn
    io.c_mirrow := BHF_Float.fromBits(8, 24, out_fn)
}
class TestFloat extends AnyFlatSpec with ChiselScalatestTester {
    behavior of "FPU Adder"
    it should "perform FP32 addition" in {
        test(new TestFloatAdder(BHF_Float(8.E, 24.W))).withAnnotations(Seq(WriteVcdAnnotation)) { c =>
            c.io.a.sign.poke(0.U)
            c.io.b.sign.poke(0.U)
            c.io.a.exp.poke(129.U)
            c.io.b.exp.poke(129.U)
            c.io.a.mantissa.poke(0.U)
            c.io.b.mantissa.poke(0.U)
            println(c.io.c.peek())
        }
    }

    it should "perform FP32 addition with Berkeley Hardfloat" in {
        test(new TestFloatAdderRaw).withAnnotations(Seq(WriteVcdAnnotation)) { c =>
            c.io.a.poke(0x40000000.U)
            c.io.b.poke(0x40000000.U)
            val out = c.io.c.peek()
            println(out)
            println(c.io.c_mirrow.peek())
        }
    }
}
