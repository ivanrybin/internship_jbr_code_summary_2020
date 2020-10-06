package jbr.intern.codesummary.methods

import java.time.LocalDate
import java.time.LocalTime

data class MethodInfo(
    val name: String,
    val firstObserved: Pair<LocalDate, LocalTime>,
    var lastObserved: Pair<LocalDate, LocalTime>,
    val projectPath: String,
    val firstObservedHash: String,
    var lastObservedHash: String
) {
    fun toJSON() : String {
        return listOf("{",
            "\t\t\t\t\"name\" : \"$name\",",
            "\t\t\t\t\"first_time\" : \"${firstObserved.first} ${firstObserved.second}\",",
            "\t\t\t\t\"last_time\" : \"${lastObserved.first} ${lastObserved.second}\",",
            "\t\t\t\t\"path\" : \"$projectPath\",",
            "\t\t\t\t\"first_hash\" : \"$firstObservedHash\",",
            "\t\t\t\t\"last_hash\" : \"$lastObservedHash\"",
            "\t\t}").joinToString(separator = System.lineSeparator())
    }
}
