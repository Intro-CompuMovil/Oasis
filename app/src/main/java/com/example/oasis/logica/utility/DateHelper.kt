package com.example.oasis.logica.utility

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DateHelper {
    fun getDateWithHour(date: LocalDateTime): String{
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy h:mma")
        return date.format(formatter)
    }
}