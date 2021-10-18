package com.gc.myapplication.global

import timber.log.Timber
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.system.exitProcess

/**
 * An custom handler for uncaught exception
 * @author n.quoi
 * @date 10.18.2021
 */
class ExceptionHandler : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(thread: Thread, exception: Throwable) {
        val stackTrace = StringWriter()
        exception.printStackTrace(PrintWriter(stackTrace))

        Timber.e(stackTrace.toString())
        Timber.e(exception.stackTrace[0].lineNumber.toString())
        android.os.Process.killProcess(android.os.Process.myPid())
        exitProcess(10)
    }
}
