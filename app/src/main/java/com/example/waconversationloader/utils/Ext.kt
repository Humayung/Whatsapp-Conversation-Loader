package io.skipday.takan.extensions

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Typeface
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.core.graphics.ColorUtils
import com.example.waconversationloader.utils.generateColorFromHashCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.BuildConfig
import java.io.File
import java.io.FileOutputStream
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

inline val Float.int: Int
    get() = this.toInt()

fun Char.isAlphabet(): Boolean {
    return this.lowercaseChar() in 'a'..'z'
}

fun Color.toHexString(): String {
    val argb = this.toArgb()
    return String.format("#%06X", 0xFFFFFF and argb)
}

fun loadTypeFace(assetManager: AssetManager, path: String): Typeface {
    return Typeface.createFromAsset(assetManager, path)
}

@SuppressLint("LogNotTimber")
@Deprecated(
    message = "Debugging purposes. This method logs the value of the object and return itself",
    level = DeprecationLevel.WARNING
)
fun <T> T?.logNullable(title: String = "Log"): T? {
    Log.d("LOGGGGG", "$title: $this")
    return this
}

fun <T> T.log(title: String = "Log"): T {
    Log.d("LOGGGGG", "$title: $this")
    return this
}

@SuppressLint("LogNotTimber")
@Deprecated(
    message = "Debugging purposes. This method logs the value of the object and return itself",
    level = DeprecationLevel.WARNING
)
fun <T> T.log(title: String = "Log", block: (T) -> Any?): T {
    Log.d("LOGGGGG", "$title: ${block(this)}")
    return this
}

fun log(title: String, vararg value: Any?) {
    Log.d("LOGGGGG", "$title: ${value.joinToString { it.toString() }}")
}

fun Float.mapToRange(min: Float, max: Float, tMin: Float, tMax: Float): Float {
    val fraction = (this - min) / (max - min)
    return tMin + (tMax - tMin) * fraction
}

@Deprecated(
    message = "Debugging purposes. This method logs the value of the object and return itself",
    level = DeprecationLevel.WARNING
)
infix fun Boolean.doIfTrue(block: () -> Unit) {
    if (this) {
        block()
    }
}

@Composable
fun Int.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }

fun Int.pxToDp(density: Density) = with(density) { this@pxToDp.toDp() }

val Boolean.int: Int
    get() = if (this) 1 else 0

val Boolean.float: Float
    get() = if (this) 1f else 0f

@Composable
fun Float.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }

fun Float.pxToDp(density: Density) = with(density) { this@pxToDp.toDp() }

fun Any.boolAble(): Boolean {
    return true
}

fun Bitmap.resize(newWidth: Float, newHeight: Float): Bitmap {
    val scaleWidth = newWidth / width
    val scaleHeight = newHeight / height
    // CREATE A MATRIX FOR THE MANIPULATION
    val matrix = Matrix()
    // RESIZE THE BIT MAP
    matrix.postScale(scaleWidth, scaleHeight)
    // "RECREATE" THE NEW BITMAP
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, false)
}

@Composable
fun LazyGridState.OnBottomReached(loadMore: () -> Unit) {
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
                ?: return@derivedStateOf true

            lastVisibleItem.index == layoutInfo.totalItemsCount - 1
        }
    }
    // Convert the state into a cold flow and collect
    LaunchedEffect(shouldLoadMore) {
        snapshotFlow { shouldLoadMore.value }.collect {
            if (it) loadMore()
        }
    }
}

fun Color.contrasted(background: Color): Color {
    val contrast = ColorUtils.calculateContrast(background.toArgb(), this.toArgb())
    return if (contrast > 1.5) Color.White else Color.Black
}

fun Color.invert(): Color {
    val invertedRed = 1 - red
    val invertedGreen = 1 - green
    val invertedBlue = 1 - blue
    return Color(invertedRed, invertedGreen, invertedBlue)
}

@Deprecated(
    message = "Debugging purposes. This modifier adds colored layer for layout debugging purposes",
    level = DeprecationLevel.WARNING
)
fun Modifier.debugLayer(obj: Any? = null) = then(
    background(
        generateColorFromHashCode(
            obj
                ?: this
        ).copy(alpha = 0.5f)
    )
)

fun Context.setClipboard(text: String, label: String): Boolean {
    return try {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        true
    } catch (e: Exception) {
        false
    }
}

@Deprecated(
    message = "Debugging purposes. This modifier adds colored layer for layout debugging purposes",
    level = DeprecationLevel.WARNING
)
fun Modifier.debugLayer(color: Color) = background(color.copy(alpha = 0.5f))

// ACTUAL OFFSET
@OptIn(ExperimentalFoundationApi::class)
fun PagerState.offsetForPage(page: Int) = (currentPage - page) + currentPageOffsetFraction

// OFFSET ONLY FROM THE LEFT
@OptIn(ExperimentalFoundationApi::class)
fun PagerState.startOffsetForPage(page: Int): Float {
    return offsetForPage(page).coerceAtLeast(0f)
}

// OFFSET ONLY FROM THE RIGHT
@OptIn(ExperimentalFoundationApi::class)
fun PagerState.endOffsetForPage(page: Int): Float {
    return offsetForPage(page).coerceAtMost(0f)
}

fun Modifier.fadingEdge(brush: Brush) = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawBehind { }
    .drawWithContent {
        drawContent()
        drawRect(brush = brush, blendMode = BlendMode.DstIn)
    }

fun Modifier.fadingEdgeBottom(percent: Float): Modifier {
    val brush = Brush.verticalGradient(
        0f to Color.Black,
        (100 - percent) / 100 to Color.Black,
        1f to Color.Transparent
    )
    return fadingEdge(brush)
}

fun Modifier.fadingEdgeTop(percent: Float): Modifier {
    val brush = Brush.verticalGradient(
        0f to Color.Transparent,
        (100 - percent) / 100 to Color.Black,
        1f to Color.Black
    )
    return fadingEdge(brush)
}

fun Modifier.fadingEdgeEnd(percent: Float): Modifier {
    val brush = Brush.horizontalGradient(
        0f to Color.Black,
        (100 - percent) / 100 to Color.Black,
        1f to Color.Transparent
    )
    return fadingEdge(brush)
}

fun Modifier.fadingEdgeHorizontal(percent: Float): Modifier {
    val brush =
        Brush.horizontalGradient(
            0f to Color.Transparent,
            (percent) / 100 to Color.Black,
            (100 - percent) / 100 to Color.Black,
            1f to Color.Transparent
        )
    return fadingEdge(brush)
}

fun Modifier.fadingEdgeStart(percent: Float): Modifier {
    val brush = Brush.horizontalGradient(
        0f to Color.Transparent,
        (100 - percent) / 100 to Color.Black,
        1f to Color.Black
    )
    return fadingEdge(brush)
}

fun putBitmapToStorage(bitmap: Bitmap, target: File): Boolean {
    return try {
        val outputStream = FileOutputStream(target)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.close()
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

fun ComponentActivity.createActivityLauncher(onResult: (ActivityResult) -> Unit = {}): ActivityResultLauncher<Intent> {
    return registerForActivityResult(ActivityResultContracts.StartActivityForResult(), onResult)
}

infix fun <T> Boolean.select(option: Pair<T, T>): T {
    return if (this) option.second else option.first
}

infix fun <T> Pair<T, T>.select(selector: Boolean?): T {
    return if (selector == true) first else second
}

enum class EllipsisPosition {
    START,
    MIDDLE,
    END
}

fun String.ellipsis(
    maxLength: Int,
    ellipsisPosition: EllipsisPosition = EllipsisPosition.END
): String {
    return when (ellipsisPosition) {
        EllipsisPosition.START -> {
            if (this.length > maxLength) {
                "...${this.substring(this.length - maxLength + 3)}"
            } else {
                this
            }
        }

        EllipsisPosition.MIDDLE -> {
            if (this.length > maxLength) {
                val halfLength = maxLength / 2
                val firstPart = this.substring(0, halfLength)
                val secondPart = this.substring(this.length - halfLength, this.length)
                "$firstPart...$secondPart"
            } else {
                this
            }
        }

        else -> { // EllipsisPosition.END
            if (this.length > maxLength) {
                "${this.substring(0, maxLength - 3)}..."
            } else {
                this
            }
        }
    }
}

fun Modifier.compatibleContentPadding() =
    then(if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) Modifier.safeContentPadding() else Modifier.systemBarsPadding())
// if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
//     safeContentPadding()
// }else{
//     safeContentPadding()
// }

fun calculateTotalFileSize(directory: File): Long {
    var totalSize: Long = 0
    if (directory.exists() && directory.isDirectory) {
        val files = directory.listFiles()
        files.joinToString().log("laksdjlksadja")
        if (files != null) {
            for (file in files) {
                if (file.isFile) {
                    totalSize += file.length()
                }
            }
        }
    }

    return totalSize
}

fun calculateTotalFileSize(files: List<File>): Long {
    var totalSize: Long = 0
    files.joinToString().log("laksdjlksadja")
    for (file in files) {
        if (file.isFile && file.exists()) {
            totalSize += file.length()
        }
    }

    return totalSize
}

fun Float.roundToDecimalPlaces(decimalPlaces: Int): Float {
    val factor = 10.0.pow(decimalPlaces.toDouble())
    return (this * factor).roundToInt() / factor.toFloat()
}

fun <T> T.on(scope: CoroutineScope, block: suspend T.() -> Unit) {
    scope.launch {
        block(this@on)
    }
}

fun Modifier.addBottomBorder(borderColor: Color, borderWidth: Dp): Modifier = then(
    Modifier.drawBehind {
        drawRect(
            color = borderColor,
            topLeft = Offset(0f, size.height - borderWidth.toPx()),
            size = Size(size.width, borderWidth.toPx())
        )
    }
)

@Composable
fun Modifier.doubleClickDebounced(
    debounceTime: Duration = 220.milliseconds,
    onSingleClick: () -> Unit = {},
    onDoubleClick: () -> Unit = {},
): Modifier = composed {
    var clickCount by remember { mutableIntStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    clickable {
        coroutineScope.launch {
            clickCount++
            delay(debounceTime)
            if (clickCount == 1) {
                onSingleClick()
            } else if (clickCount >= 2) {
                onDoubleClick()
            }
            clickCount = 0
        }
    }
}
