package com.example.waconversationloader.persentation.components

import android.graphics.RenderEffect
import android.graphics.Shader
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import io.skipday.takan.extensions.debugLayer
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FlipBottomSheet(
    modifier: Modifier = Modifier,
    sheetState: FLipBottomSheetState = rememberFlipBottomSheetState(),
    insets: WindowInsets = WindowInsets.systemBars,
    sheetContent: @Composable ColumnScope.() -> Unit,
    content: @Composable BoxScope.() -> Unit,
) {
    val scope = rememberCoroutineScope()
    val shouldDrawSheet by remember {
        derivedStateOf {
            (sheetState.anchoredDraggableState.currentValue == DragAnchor.Expanded || sheetState.anchoredDraggableState.targetValue == DragAnchor.Expanded || sheetState.anchoredDraggableState.progress != 1f)
        }
    }

    if (shouldDrawSheet) {
        BackHandler {
            scope.launch {
                sheetState.anchoredDraggableState.animateTo(DragAnchor.Collapse)
            }
        }
    }
    Box(modifier = Modifier) {
        content()
    }
    if (shouldDrawSheet) Box(modifier = Modifier
        .fillMaxSize()
        .graphicsLayer {
            if (sheetState.anchoredDraggableState.progress >= 0f) {
                val startOffset =
                    sheetState.anchoredDraggableState.offset / sheetState.anchoredDraggableState.anchors.maxAnchor()
                alpha = (1f - startOffset) * 0.3f
            }
        }
        .background(Color.Black)

    )
    if (shouldDrawSheet) Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }) {
                scope.launch {
                    sheetState.anchoredDraggableState.animateTo(
                        DragAnchor.Collapse,
                        sheetState.anchoredDraggableState.lastVelocity
                    )
                }
            }
            .windowInsetsPadding(insets)
            .fillMaxSize()
            .offset {
                IntOffset(
                    x = 0,
                    y = sheetState.anchoredDraggableState
                        .requireOffset()
                        .roundToInt(),
                )
            }
            .then(
                if (sheetState.swipeToDismiss) {
                    Modifier
                        .nestedScroll(
                            negativeScroll(
                                onDelta = sheetState.anchoredDraggableState::dispatchRawDelta,
                                orientation = Orientation.Vertical,
                                onFling = {
                                    scope.launch {
                                        sheetState.anchoredDraggableState.settle(sheetState.anchoredDraggableState.lastVelocity)
                                    }
                                })
                        )
                        .anchoredDraggable(
                            state = sheetState.anchoredDraggableState,
                            orientation = Orientation.Vertical
                        )
                } else Modifier
            )
            .fillMaxWidth()
            .onSizeChanged {
                sheetState.anchoredDraggableState.updateAnchors(DraggableAnchors {
                    DragAnchor.Collapse at it.height.toFloat()
                    DragAnchor.Expanded at 0f
                })
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(Color.White)
                .padding(top = 8.dp, bottom = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(32.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.Gray.copy(0.3f))
            )
            Spacer(modifier = Modifier.height(16.dp))
            sheetContent()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
class FLipBottomSheetState(
    initialValue: DragAnchor = DragAnchor.Collapse,
    animationSpec: AnimationSpec<Float> = tween(durationMillis = 100),
    var swipeToDismiss: Boolean,
) {
    val currentValue: DragAnchor
        get() = anchoredDraggableState.currentValue
    internal val anchoredDraggableState: AnchoredDraggableState<DragAnchor>

    suspend fun expand() {
        anchoredDraggableState.animateTo(DragAnchor.Expanded)
    }

    suspend fun settle() {
        anchoredDraggableState.settle(anchoredDraggableState.lastVelocity)
    }

    suspend fun collapse() {
        anchoredDraggableState.animateTo(DragAnchor.Collapse)
    }

    init {
        anchoredDraggableState = AnchoredDraggableState(
            initialValue = initialValue,
            positionalThreshold = { distance: Float ->
                distance * 0.5f
            },
            velocityThreshold = { 1000f },
            animationSpec = animationSpec,
        )
    }

    companion object {
        val Saver: Saver<FLipBottomSheetState, *> = listSaver(
            save = { listOf<Any>(it.anchoredDraggableState.currentValue, it.swipeToDismiss) },
            restore = {
                FLipBottomSheetState(
                    initialValue = it[0] as DragAnchor,
                    swipeToDismiss = it[1] as Boolean
                )
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun rememberFlipBottomSheetState(
    animationSpec: AnimationSpec<Float> = tween(),
    initialValue: DragAnchor = DragAnchor.Collapse,
    swipeToDismiss: Boolean = true,
): FLipBottomSheetState {
    val context = LocalContext.current
    return rememberSaveable(saver = FLipBottomSheetState.Saver) {
        FLipBottomSheetState(
            initialValue = initialValue,
            animationSpec = animationSpec,
            swipeToDismiss = swipeToDismiss
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
fun negativeScroll(
    onDelta: (Float) -> Float,
    orientation: Orientation,
    onFling: (velocity: Float) -> Unit,
): NestedScrollConnection = object : NestedScrollConnection {
    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        val delta = available.toFloat()
        return if (delta < 0 && source == NestedScrollSource.Drag) {
            onDelta(delta).toOffset()
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
            onDelta(available.toFloat()).toOffset()
        } else {
            Offset.Zero
        }
    }

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        onFling(available.toFloat())
        return available
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        return super.onPreFling(available)
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

enum class DragAnchor {
    Collapse, Expanded, Half
}