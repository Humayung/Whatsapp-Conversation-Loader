package com.example.waconversationloader.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.os.VibrationEffect
import android.os.Vibrator
import android.renderscript.Allocation
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.Velocity
import androidx.core.view.WindowCompat
import io.skipday.takan.extensions.log
import kotlinx.coroutines.delay
import java.net.URL
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.roundToInt

fun hideSystemUI(activity: Activity) {
    //Hides the ugly action bar at the top
    activity.run {
        actionBar?.hide()
        //Hide the status bars
        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (SDK_INT < Build.VERSION_CODES.R) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            window.insetsController?.apply {
                hide(WindowInsets.Type.statusBars())
                systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }
}

fun translucentNavigationBar(window: Window) {
    //don't apply if SDK 28 or lower
    if (SDK_INT <= Build.VERSION_CODES.P) {
        return
    }
    window.setFlags(
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
    )
}

inline fun measureTimeMillis(block: () -> Unit): Long {
    val start = System.currentTimeMillis()
    block()
    return System.currentTimeMillis() - start
}


fun Activity.makeStatusBarTransparent() {
    window.apply {
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }
}

fun loadImage(url: String?): Bitmap? {
    val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
    StrictMode.setThreadPolicy(policy)
    return try {
        val inputStream = URL(url).openStream()
        BitmapFactory.decodeStream(inputStream)
    } catch (_: Exception) {
        null
    }
}

fun isAndroidVersionLessThan(version: Int): Boolean {
    return SDK_INT < version
}

@Composable
fun RequiresAndroid12(block: @Composable () -> Unit) {
    if (!isAndroidVersionLessThan(Build.VERSION_CODES.S)) {
        block()
    }
}

class ShiftedAlignment(
    val x: Float = 0f,
    val y: Float = 0f,
) : Alignment {
    override fun align(size: IntSize, space: IntSize, layoutDirection: LayoutDirection): IntOffset {
        val centerX = (space.width - size.width).toFloat() / 2f
        val centerY = (space.height - size.height).toFloat() / 2f
        val shiftedX = centerX + x
        val shiftedY = centerY + y
        return IntOffset(shiftedX.roundToInt(), shiftedY.roundToInt())
    }
}

@Composable
fun RequiresAndroidN(block: @Composable () -> Unit) {
    if (!isAndroidVersionLessThan(Build.VERSION_CODES.N)) {
        block()
    }
}

fun blurImage(context: Context, imageBitmap: ImageBitmap, radius: Float): Bitmap {
    val bitmap = imageBitmap.asAndroidBitmap().copy(Bitmap.Config.ARGB_8888, true)
    val renderScript = RenderScript.create(context)
    val bitmapAlloc = Allocation.createFromBitmap(renderScript, bitmap)
    ScriptIntrinsicBlur.create(renderScript, bitmapAlloc.element).apply {
        setRadius(radius)
        setInput(bitmapAlloc)
        forEach(bitmapAlloc)
    }
    val blurredBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
    bitmapAlloc.copyTo(blurredBitmap)
    renderScript.destroy()
    return blurredBitmap
}

fun calculateSHA(input: String): String {
    val bytes = input.toByteArray(StandardCharsets.UTF_8)
    val digest = MessageDigest.getInstance("SHA-256")
    val hashBytes = digest.digest(bytes)
    val hexString = StringBuilder()
    for (byte in hashBytes) {
        hexString.append(String.format("%02x", byte))
    }
    return hexString.toString()
}

fun <T> requiresAndroid12(block: () -> T): T? {
    if (!isAndroidVersionLessThan(Build.VERSION_CODES.S)) {
        return block()
    }
    return null
}

fun requiresAndroidNougat(block: () -> Unit) {
    if (!isAndroidVersionLessThan(Build.VERSION_CODES.N)) {
        block()
    }
}

fun <T> requiresAndroidVersion(versionCode: Int, block: () -> T): T? {
    if (!isAndroidVersionLessThan(versionCode)) {
        return block()
    }
    return null
}

@Composable
fun dpToSp(dp: Dp) = with(LocalDensity.current) { dp.toSp() }

fun uniformTypography(textStyle: TextStyle): Typography {
    return Typography(
        bodyLarge = textStyle,
        bodyMedium = textStyle,
        bodySmall = textStyle,
        displayLarge = textStyle,
        displayMedium = textStyle,
        displaySmall = textStyle,
        headlineLarge = textStyle,
        headlineMedium = textStyle,
        headlineSmall = textStyle,
        labelLarge = textStyle,
        labelMedium = textStyle,
        labelSmall = textStyle,
        titleLarge = textStyle,
        titleMedium = textStyle,
        titleSmall = textStyle,
    )
}

@OptIn(ExperimentalFoundationApi::class)
fun ConsumeSwipeUp(
    anchoredDraggableState: AnchoredDraggableState<*>,
    orientation: Orientation,
    onFling: (velocity: Float) -> Unit,
): NestedScrollConnection = object : NestedScrollConnection {
    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        val delta = available.toFloat()
        return if (delta > 0 && source == NestedScrollSource.Drag) {
            anchoredDraggableState.dispatchRawDelta(delta).toOffset()
        } else {
            Offset.Zero
        }
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource,
    ): Offset {
        return if (source == NestedScrollSource.Drag) {
            anchoredDraggableState.dispatchRawDelta(available.toFloat()).toOffset()
        } else {
            Offset.Zero
        }
    }

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        onFling(available.toFloat())
        return available
    }

    private fun Float.toOffset(): Offset = Offset(
        x = if (orientation == Orientation.Horizontal) this else 0f,
        y = if (orientation == Orientation.Vertical) this else 0f
    )

    @JvmName("velocityToFloat")
    private fun Velocity.toFloat() = if (orientation == Orientation.Horizontal) x else y

    @JvmName("offsetToFloat")
    private fun Offset.toFloat(): Float = if (orientation == Orientation.Horizontal) x else y
}

fun hueToRgb(p: Float, q: Float, t: Float): Float {
    var tVar = t
    if (tVar < 0) tVar += 1
    if (tVar > 1) tVar -= 1
    if (tVar < 1f / 6) return p + (q - p) * 6 * tVar
    if (tVar < 1f / 2) return q
    if (tVar < 2f / 3) return p + (q - p) * (2f / 3 - tVar) * 6
    return p
}

fun hslToRgb(h: Float, s: Float, l: Float): IntArray {
    val r: Float
    val g: Float
    val b: Float

    if (s == 0f) {
        r = l
        g = l
        b = l
    } else {
        val q = if (l < 0.5) l * (1 + s) else l + s - l * s
        val p = 2 * l - q
        r = hueToRgb(p, q, h + 1f / 3)
        g = hueToRgb(p, q, h)
        b = hueToRgb(p, q, h - 1f / 3)
    }

    return intArrayOf((r * 255).roundToInt(), (g * 255).roundToInt(), (b * 255).roundToInt())
}

fun generateColorFromHashCode(obj: Any): Color {
    val goldenRatio = 0.618033988749895
    val hue = (obj.hashCode() * goldenRatio) % 1
    val saturation = 0.7
    val lightness = 0.6
    val rgb = hslToRgb(hue.toFloat(), saturation.toFloat(), lightness.toFloat())

    return Color(rgb[0], rgb[1], rgb[2])
}

suspend fun waitUntil(
    condition: () -> Boolean,
    functionToWait: suspend () -> Unit,
    intervalInMillis: Long,
    timeoutInMillis: Long,
): Boolean {
    val startTime = System.currentTimeMillis()
    while (!condition()) {
        functionToWait()
        delay(intervalInMillis)
        if (System.currentTimeMillis() - startTime >= timeoutInMillis) {
            return false
        }
    }
    return true
}

fun toast(context: Context, text: String) {
    Handler(Looper.getMainLooper()).post {
        val t = Toast.makeText(context, text, Toast.LENGTH_LONG)
        t.setGravity(Gravity.FILL, 0, 0)
        t.show()
    }
}

fun formatArithmeticExpression(expression: String): String {
    // Define a regular expression pattern to match numbers in the expression
    val numberPattern = Regex("-?\\d+(\\.\\d+)?")

    // Use the find function to find all numbers in the expression
    val formattedExpression = numberPattern.replace(expression) { matchResult ->
        val number = matchResult.value.toDouble()
        formatNumberWithThousandsSeparator(number)
    }

    return formattedExpression
}

fun formatNumberWithThousandsSeparator(input: Any?): String {
    val numberFormat =
        DecimalFormat("#,###.###########", DecimalFormatSymbols.getInstance(Locale.US))

    return when (input) {
        is Double -> numberFormat.format(input)
        is String -> {
            try {
                val number = input.toDouble()
                numberFormat.format(number)
            } catch (e: Exception) {
                //recognize scientific notation in string like 1e+5 cm
                val regex = """([-+]?[0-9]*\.?[0-9]+([eE][-+]?[0-9]+)?)\s*(\w+)""".toRegex()
                val matchResult = regex.find(input)
                matchResult?.let { match ->
                    val decimalValue = match.groupValues[1].toDouble()
                    val unitPart = match.groupValues[3]
                    val isAlphabet = input.matches(Regex("[a-zA-Z]+"))
                    if (!isAlphabet) {
                        return input
                    }
                    "${numberFormat.format(decimalValue)} $unitPart"
                } ?: input
            }
        }
        else -> ""
    }
}

fun getDeviceName(): String {
    val manufacturer = Build.MANUFACTURER
    val model = Build.MODEL
    return if (model.startsWith(manufacturer)) {
        model.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
    } else {
        "$manufacturer $model".replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
    }
}
