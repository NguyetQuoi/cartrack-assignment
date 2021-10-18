package com.gc.myapplication.util

import java.util.*

/**
 * Utility class for String
 * @author n.quoi
 * @date 10.18.2021
 */

object StringUtils {

    /**
     * Capitalize string
     * @param s String
     * @return capital string
     */
    fun capitalize(s: String?): String {
        if (s.isNullOrEmpty()) {
            return ""
        }
        val first = s[0]
        return if (Character.isUpperCase(first)) {
            s
        } else {
            Character.toUpperCase(first) + s.substring(1)
        }
    }

    /**
     * Check string value not null
     * @param s value need to be checked
     */
    fun isNotEmpty(s: String?): Boolean {
        if (s == null) return false
        return !(s.trim { it <= ' ' }.isEmpty() || "null" == s.toLowerCase(Locale.US))
    }

    /**
     * Check string value null
     * @param s value need to be checked
     */
    fun isEmpty(s: String?): Boolean {
        if (s == null) return true
        return (s.trim { it <= ' ' }.isEmpty() || "null" == s.toLowerCase(Locale.US))
    }

    /**
     * Split String
     * @param s value need to be split
     * @param splitBy delimiter
     */
    fun splitString(s: String, splitBy: String): List<String> {
        return s.split(splitBy)
    }
}
