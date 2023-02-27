package strongfloat

import Chisel.Cat
import chisel3._
import chisel3.internal.firrtl.Width
import chisel3.internal.sourceinfo.SourceInfo
import chisel3.util.DecoupledIO
import hardfloat.{AddRecFN, CompareRecFN, MulAddRecFN, MulRecFN, fNFromRecFN, recFNFromFN}
import strongfloat.Float.fromIntToExpWidth

class BHF_Float(val expWidth: ExponentWidth, val sigWidth: Width) extends Bundle
    with Num[BHF_Float]
    with FloatNum[BHF_Float]
    {
    /*
    * format: |sign|exponent|mantissa|
    * expWidth = |exponent|
    * mantissaWidth = sigWidth - 1
    * total width = expWidth + sigWidth
    * */
    val sign: UInt = UInt(1.W)
    val exp: UInt = UInt(expWidth.get.W)
    val mantissa: UInt = UInt((sigWidth.get - 1).W)

    def asBits = {
        val bits = Wire(UInt((expWidth.get + sigWidth.get).W))
        bits := Cat(sign, exp, mantissa)
        bits
    }


    private def inferResultWidths(this_expWidth: Int, this_sigWidth: Int, that_expWidth: Int, that_sigWidth: Int): (Int, Int) = {
        val exp_width = Math.max(this_expWidth, that_expWidth)
        val sig_width = Math.max(this_sigWidth, that_sigWidth)
        (exp_width, sig_width)
    }

    override def do_+(that: BHF_Float)(implicit sourceInfo: SourceInfo, compileOptions: CompileOptions): BHF_Float = {
        val rec_this = recFNFromFN(this.expWidth.get, this.sigWidth.get, this.asBits)
        val rec_that = recFNFromFN(that.expWidth.get, that.sigWidth.get, that.asBits)
        val (new_expWidth, new_sigWidth) = inferResultWidths(this.expWidth.get, this.sigWidth.get, that.expWidth.get, that.sigWidth.get)
        val adder = chisel3.Module(new AddRecFN(new_expWidth, new_sigWidth))
        adder.io.subOp := 0.U
        adder.io.a := rec_this
        adder.io.b := rec_that
        adder.io.roundingMode := 0.U
        adder.io.detectTininess := 0.U
        val out_rec_float = adder.io.out
        val out_fn = Wire(UInt((new_expWidth + new_sigWidth).W))
        out_fn := fNFromRecFN(new_expWidth, new_sigWidth, out_rec_float)
        BHF_Float.fromBits(new_expWidth, new_sigWidth, out_fn)
    }


    override def do_*(that: BHF_Float)(implicit sourceInfo: SourceInfo, compileOptions: CompileOptions): BHF_Float = {
        val rec_this = recFNFromFN(this.expWidth.get, this.sigWidth.get, this.asBits)
        val rec_that = recFNFromFN(that.expWidth.get, that.sigWidth.get, that.asBits)
        val (new_expWidth, new_sigWidth) = inferResultWidths(this.expWidth.get, this.sigWidth.get, that.expWidth.get, that.sigWidth.get)
        val multiplier = chisel3.Module(new MulRecFN(new_expWidth, new_sigWidth))
        multiplier.io.a := rec_this
        multiplier.io.b := rec_that
        multiplier.io.roundingMode := 0.U
        multiplier.io.detectTininess := 0.U
        val out_rec_float = multiplier.io.out
        val out_fn = Wire(UInt((new_expWidth + new_sigWidth).W))
        out_fn := fNFromRecFN(new_expWidth, new_sigWidth, out_rec_float)
        BHF_Float.fromBits(new_expWidth, new_sigWidth, out_fn)
    }

    override def do_/(that: BHF_Float)(implicit sourceInfo: SourceInfo, compileOptions: CompileOptions): BHF_Float = {
        throw new Exception("Directly dividing two BHF_Floats is prohibited, try using /-/")
    }

    override def do_%(that: BHF_Float)(implicit sourceInfo: SourceInfo, compileOptions: CompileOptions): BHF_Float = {
        throw new Exception("Floating point modulo is not supported.")
    }

    override def do_-(that: BHF_Float)(implicit sourceInfo: SourceInfo, compileOptions: CompileOptions): BHF_Float = {
        val rec_this = recFNFromFN(this.expWidth.get, this.sigWidth.get, this.asBits)
        val rec_that = recFNFromFN(that.expWidth.get, that.sigWidth.get, that.asBits)
        val (new_expWidth, new_sigWidth) = inferResultWidths(this.expWidth.get, this.sigWidth.get, that.expWidth.get, that.sigWidth.get)
        val adder = chisel3.Module(new AddRecFN(new_expWidth, new_sigWidth))
        adder.io.subOp := 1.U
        adder.io.a := rec_this
        adder.io.b := rec_that
        adder.io.roundingMode := 0.U
        adder.io.detectTininess := 0.U
        val out_rec_float = adder.io.out
        val out_fn = Wire(UInt((new_expWidth + new_sigWidth).W))
        out_fn := fNFromRecFN(new_expWidth, new_sigWidth, out_rec_float)
        BHF_Float.fromBits(new_expWidth, new_sigWidth, out_fn)
    }

    override def do_<(that: BHF_Float)(implicit sourceInfo: SourceInfo, compileOptions: CompileOptions): Bool = {
        val rec_this = recFNFromFN(this.expWidth.get, this.sigWidth.get, this.asBits)
        val rec_that = recFNFromFN(that.expWidth.get, that.sigWidth.get, that.asBits)
        val (new_expWidth, new_sigWidth) = inferResultWidths(this.expWidth.get, this.sigWidth.get, that.expWidth.get, that.sigWidth.get)
        val comparator = chisel3.Module(new CompareRecFN(new_expWidth, new_sigWidth))
        comparator.io.a := rec_this
        comparator.io.b := rec_that
        comparator.io.signaling := 0.U
        comparator.io.lt
    }

    override def do_<=(that: BHF_Float)(implicit sourceInfo: SourceInfo, compileOptions: CompileOptions): Bool = {
        val rec_this = recFNFromFN(this.expWidth.get, this.sigWidth.get, this.asBits)
        val rec_that = recFNFromFN(that.expWidth.get, that.sigWidth.get, that.asBits)
        val (new_expWidth, new_sigWidth) = inferResultWidths(this.expWidth.get, this.sigWidth.get, that.expWidth.get, that.sigWidth.get)
        val comparator = chisel3.Module(new CompareRecFN(new_expWidth, new_sigWidth))
        comparator.io.a := rec_this
        comparator.io.b := rec_that
        comparator.io.signaling := 0.U
        comparator.io.lt | comparator.io.eq
    }

    override def do_>(that: BHF_Float)(implicit sourceInfo: SourceInfo, compileOptions: CompileOptions): Bool = {
        val rec_this = recFNFromFN(this.expWidth.get, this.sigWidth.get, this.asBits)
        val rec_that = recFNFromFN(that.expWidth.get, that.sigWidth.get, that.asBits)
        val (new_expWidth, new_sigWidth) = inferResultWidths(this.expWidth.get, this.sigWidth.get, that.expWidth.get, that.sigWidth.get)
        val comparator = chisel3.Module(new CompareRecFN(new_expWidth, new_sigWidth))
        comparator.io.a := rec_this
        comparator.io.b := rec_that
        comparator.io.signaling := 0.U
        comparator.io.gt
    }

    override def do_>=(that: BHF_Float)(implicit sourceInfo: SourceInfo, compileOptions: CompileOptions): Bool = {
        val rec_this = recFNFromFN(this.expWidth.get, this.sigWidth.get, this.asBits)
        val rec_that = recFNFromFN(that.expWidth.get, that.sigWidth.get, that.asBits)
        val (new_expWidth, new_sigWidth) = inferResultWidths(this.expWidth.get, this.sigWidth.get, that.expWidth.get, that.sigWidth.get)
        val comparator = chisel3.Module(new CompareRecFN(new_expWidth, new_sigWidth))
        comparator.io.a := rec_this
        comparator.io.b := rec_that
        comparator.io.signaling := 0.U
        comparator.io.gt | comparator.io.eq
    }

    override def do_abs(implicit sourceInfo: SourceInfo, compileOptions: CompileOptions): BHF_Float = {
        val new_float = Wire(new BHF_Float(this.expWidth.get.E, this.sigWidth.get.W))
        new_float.sign := 0.U
        new_float.exp := this.exp
        new_float.mantissa := this.mantissa
        new_float
    }

    override def fromUInt(x: UInt): BHF_Float = {
        ???
    }

    override def roundToUInt: UInt = {
        ???
    }

    override def *+(coeff: BHF_Float, acc: BHF_Float): BHF_Float = {
        // FMA
        val rec_this = recFNFromFN(this.expWidth.get, this.sigWidth.get, this.asBits)
        val rec_coeff = recFNFromFN(coeff.expWidth.get, coeff.sigWidth.get, coeff.asBits)
        val rec_acc = recFNFromFN(acc.expWidth.get, acc.sigWidth.get, acc.asBits)
        val (new_expWidth, new_sigWidth) = inferResultWidths(coeff.expWidth.get, coeff.sigWidth.get, acc.expWidth.get, acc.sigWidth.get)
        val fma = chisel3.Module(new MulAddRecFN(new_expWidth, new_sigWidth))
        fma.io.a := rec_this
        fma.io.b := rec_coeff
        fma.io.c := rec_acc
        fma.io.roundingMode := 0.U
        fma.io.detectTininess := 0.U
        fma.io.op := 3.U
        val out_rec_float = fma.io.out
        val out_fn = Wire(UInt((new_expWidth + new_sigWidth).W))
        out_fn := fNFromRecFN(new_expWidth, new_sigWidth, out_rec_float)
        BHF_Float.fromBits(new_expWidth, new_sigWidth, out_fn)
    }

    override def :==(that: BHF_Float): Unit = {
        // FIXME: need to add smart conversions between different widths
        this.sign := that.sign
        val new_exp = Wire(SInt(expWidth.get.W))
        new_exp := that.exp.asSInt
        this.exp := new_exp.asUInt

        this.mantissa := that.mantissa
    }

    override def ~/(that: BHF_Float): DecoupledIO[BHF_Float] = ???

    override def ~/-(that: BHF_Float): DecoupledIO[BHF_Float] = ???

    override def ~*(that: BHF_Float): DecoupledIO[BHF_Float] = ???

    override def ~+(that: BHF_Float): DecoupledIO[BHF_Float] = ???

    override def ~-(that: BHF_Float): DecoupledIO[BHF_Float] = ???

    override def ~*+(that: BHF_Float, acc: BHF_Float): DecoupledIO[BHF_Float] = ???
}

object BHF_Float {
    def fromBits(sigWidth: Int, expWidth: Int, bits: UInt) = {
        val new_float = Wire(new BHF_Float(expWidth.E, sigWidth.W))
        new_float.sign := bits(sigWidth + expWidth - 1)
        new_float.exp := bits(expWidth + sigWidth - 2, expWidth + sigWidth - 2 - sigWidth + 1)
        new_float.mantissa := bits(sigWidth - 3, 0)
        new_float
    }
}
