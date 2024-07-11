package works.danyella

sealed class Scale {
    data class Uniform(val value: Float) : Scale()
    data class NonUniform(val x: Float, val y: Float, val z: Float) : Scale()
}

typealias UniformScale = Scale.Uniform
typealias NonUniformScale = Scale.NonUniform
