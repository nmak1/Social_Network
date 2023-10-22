package ru.netology.social_network.until

import java.math.RoundingMode


import java.text.DecimalFormat

object ConvertNumber {

    private const val THOUSAND = 1_000
    private const val MILLION = 1_000_000


    fun counterDecimal(count: Int): String {
        val value = count.toDouble()
        val formatter = DecimalFormat("##.#")
        formatter.roundingMode = RoundingMode.DOWN
        return if (value < 1000.0) {
            formatter.format(value)
        } else {
            if (count in 1000..10000) {
                formatter.format(value / THOUSAND)+"K"
            } else {
                if (count in 10001 until MILLION) {
                    val formate = DecimalFormat("#")
                    formate.roundingMode = RoundingMode.DOWN
                    formate.format(value / THOUSAND) + "K"
                } else formatter.format(value / MILLION) + "M"

            }
        }
    }
}
