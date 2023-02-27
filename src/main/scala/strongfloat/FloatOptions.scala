package strongfloat


sealed abstract class RoundingMode() {}

object RoundingMode {
    case object RoundNearestEven extends RoundingMode
}


sealed abstract class FPULibrary() {}

object FPULibrary {
    case object FPNew extends FPULibrary
    case object Fudian extends FPULibrary
    case object BerkeleyHardFloat extends FPULibrary
}

case class FloatOptions(fpu_lib: FPULibrary, roundingMode: RoundingMode)


