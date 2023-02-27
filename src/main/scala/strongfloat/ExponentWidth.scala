package strongfloat

import strongfloat.Float.fromIntToExpWidth

sealed class ExponentWidth(val width: Int) {

    def max(that: ExponentWidth): ExponentWidth = this.get.max(that.get).E

    def +(that: ExponentWidth): ExponentWidth = (this.get + that.get).E

    def +(that: Int): ExponentWidth = (this.get + that).E

    def get: Int = width
}
