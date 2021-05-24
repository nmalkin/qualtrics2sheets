package logging

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/** The currently default log level. Anything below this level will not be printed. */
var DEFAULT_LOG_LEVEL = LogLevel.DEBUG

/**
 * Given a closure, infer the file/class name from it.
 *
 * This code is adapted from the kotlin-logging library,
 * which is licensed under the Apache 2.0 license and is Copyright (c) 2016-2018 Ohad Shai.
 */
private fun classNameFromClosure(func: () -> Unit): String {
    val name = func.javaClass.name
    return when {
        name.contains("Kt$") -> name.substringBefore("Kt$")
        name.contains("$") -> name.substringBefore("$")
        else -> name
    }
}

/**
 * Returns the current time, formatted, as a string
 */
private fun getCurrentTime(): String {
    val current = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
    return current.format(formatter)
}

/**
 * ANSI color codes
 * via https://stackoverflow.com/q/5762491
 */
@Suppress("MagicNumber")
private enum class Color(val ansiCode: Int) {
    RESET(0),
    BLACK(30),    // BLACK
    RED(31),      // RED
    GREEN(32),    // GREEN
    YELLOW(33),   // YELLOW
    BLUE(34),     // BLUE
    MAGENTA(35),  // MAGENTA
    CYAN(36),     // CYAN
    WHITE(37);    // WHITE

    override fun toString(): String {
        return "\u001B[${this.ansiCode}m"
    }
}

/**
 * This module attempts to implement a standalone logger
 * that is *somewhat* API-compatible with KotlinLogging API of the kotlin-logging library:
 * https://github.com/MicroUtils/kotlin-logging/blob/master/LICENSE
 */
object KotlinLogging {
    fun logger(name: String): Logger {
        return Logger(name)
    }

    fun logger(func: () -> Unit): Logger {
        return logger(classNameFromClosure(func))
    }
}

enum class LogLevel { TRACE, DEBUG, INFO, WARNING, ERROR }

/**
 * Return the appropriate color for the given log level
 */
private fun colorForLevel(level: LogLevel): Color {
    return when (level) {
        LogLevel.TRACE -> Color.WHITE
        LogLevel.DEBUG -> Color.CYAN
        LogLevel.INFO -> Color.GREEN
        LogLevel.WARNING -> Color.YELLOW
        LogLevel.ERROR -> Color.RED
    }
}

@Suppress("TooManyFunctions")
class Logger(private val name: String) {
    /**
     * The log level at which this logger will print output.
     * If null, uses the level set by DEFAULT_LOG_LEVEL
     * @see DEFAULT_LOG_LEVEL
     */
    var logLevel: LogLevel? = null

    fun trace(msg: String?) {
        trace { msg }
    }

    fun trace(msg: () -> Any?) {
        log(LogLevel.TRACE, msg)
    }

    fun debug(msg: String?) {
        debug { msg }
    }

    fun debug(msg: () -> Any?) {
        log(LogLevel.DEBUG, msg)
    }

    fun info(msg: String?) {
        info { msg }
    }

    fun info(msg: () -> Any?) {
        log(LogLevel.INFO, msg)
    }

    fun warning(msg: String?) {
        warning { msg }
    }

    fun warning(msg: () -> Any?) {
        log(LogLevel.WARNING, msg)
    }

    fun error(msg: String?) {
        error { msg }
    }

    fun error(msg: () -> Any?) {
        log(LogLevel.ERROR, msg)
    }

    private fun log(level: LogLevel, msg: () -> Any?) {
        // Don't output anything below the current log level
        val currentLogLevel = this.logLevel ?: DEFAULT_LOG_LEVEL
        if (level < currentLogLevel) {
            return
        }

        try {
            val messageString = msg.invoke().toString()
            val color = colorForLevel(level)
            val statement = "$color${getCurrentTime()} - $name - $level - $messageString${Color.RESET}"
            System.err.println(statement)
        } catch (e: Exception) {
            System.err.println("encountered error while trying to log message")
            e.printStackTrace()
        }
    }
}
