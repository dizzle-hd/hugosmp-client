package com.julian.client.config

/** A single manually-set or death-marker waypoint, rendered as a beacon beam with a floating label. */
data class Waypoint(
    var x: Int = 0,
    var y: Int = 0,
    var z: Int = 0,
    var label: String = "Waypoint",
    var colorRGB: Int = 0x7AA2FF,
    var dimension: String = "overworld"
)
