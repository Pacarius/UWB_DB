package com.pacarius.models
import kotlinx.serialization.Serializable
enum class CoordType {
    Lamppost,
    Vehicle,
    Empty
}
@Serializable
data class Coordinates(
    val type: CoordType,
    val x: Float,
    val y: Float,
    val id: String
)