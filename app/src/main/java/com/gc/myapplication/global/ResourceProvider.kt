package com.gc.myapplication.global

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.StringWriter

interface ResourceProvider {
    /**
     * Get String
     * @param resId resource id of string
     * @return String
     */
    fun getString(@StringRes resId: Int): String

    /**
     * Get String
     * @param formatId resource id of string
     * @param formatArgs format of string
     * @return String
     */
    fun getString(@StringRes formatId: Int, vararg formatArgs: Any): String

    /**
     * Get drawable
     * @param drawableId resource id of drawable
     * @return [Drawable]
     */
    @SuppressLint("ObsoleteSdkInt")
    fun getDrawable(@DrawableRes drawableId: Int): Drawable?

    /**
     * Get Dimension
     * @param dimensionId resource id of dimention
     * @return Float
     */
    fun getDimension(@DimenRes dimensionId: Int): Float

    /**
     * Read Json file
     * @param rawId resource id of raw json file
     * @return String
     */
    fun readJsonFile(@RawRes rawId: Int): String
}

/**
 * Resource provider
 * @author n.quoi
 * @date 10.18.201
 *
 * @param context Context
 */

class AppResourceProvider(context: Context) : ResourceProvider {

    val context: Context = context.applicationContext

    val resources: Resources
        get() = context.resources

    /**
     * Get String
     * @param resId resource id of string
     * @return String
     */
    override fun getString(@StringRes resId: Int): String {
        return context.getString(resId)
    }

    /**
     * Get String
     * @param formatId resource id of string
     * @param formatArgs format of string
     * @return String
     */
    override fun getString(@StringRes formatId: Int, vararg formatArgs: Any): String {
        return context.getString(formatId, formatArgs)
    }

    /**
     * Get drawable
     * @param drawableId resource id of drawable
     * @return [Drawable]
     */
    @SuppressLint("ObsoleteSdkInt")
    override fun getDrawable(@DrawableRes drawableId: Int): Drawable? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.getDrawable(drawableId)
        } else {
            resources.getDrawable(drawableId)
        }
    }

    /**
     * Get Dimension
     * @param dimensionId resource id of dimention
     * @return Float
     */
    override fun getDimension(@DimenRes dimensionId: Int): Float {
        return context.resources.getDimensionPixelSize(dimensionId).toFloat()
    }

    /**
     * Read Json file
     * @param rawId resource id of raw json file
     * @return String
     */
    override fun readJsonFile(@RawRes rawId: Int): String {
        val ins = context.resources.openRawResource(rawId)
        val writer = StringWriter()
        val buffer = CharArray(1024)
        try {
            val reader = BufferedReader(InputStreamReader(ins, "UTF-8"))
            var n = 0
            while ((n.apply { n = reader.read(buffer) }) != -1) {
                writer.write(buffer, 0, n)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                ins.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        return writer.toString()
    }
}
