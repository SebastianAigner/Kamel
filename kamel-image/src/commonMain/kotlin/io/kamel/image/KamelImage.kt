package io.kamel.image

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import io.kamel.core.ExperimentalKamelApi
import io.kamel.core.Resource

/**
 * A composable that is used to display a [Painter] resource.
 * To load an image resource asynchronously, use [lazyPainterResource].
 * @param resource The [Resource] that needs to be displayed.
 * @param modifier The modifier that is applied to the [Box].
 * @param onLoading Composable which is used while the image is in [Resource.Loading] state.
 * @param onFailure Composable which is used while the image is in [Resource.Failure] state.
 * @param contentAlignment The default alignment inside the Box.
 * @param animationSpec a [FiniteAnimationSpec] to be used in [Crossfade] animation, or null to be disabled.
 * The rest is the default [Image] parameters.
 */
@OptIn(ExperimentalKamelApi::class)
@Composable
public fun KamelImage(
    resource: Resource<Painter>,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    onLoading: @Composable (BoxScope.(Float) -> Unit)? = null,
    onFailure: @Composable (BoxScope.(Throwable) -> Unit)? = null,
    contentAlignment: Alignment = Alignment.Center,
    animationSpec: FiniteAnimationSpec<Float>? = null,
) {
    val onSuccess: @Composable (BoxScope.(Painter) -> Unit) = { painter ->
        Image(
            painter,
            contentDescription,
            Modifier.fillMaxSize(),
            alignment,
            contentScale,
            alpha,
            colorFilter
        )
    }
    KamelImageBox(
        resource,
        modifier,
        contentAlignment,
        animationSpec,
        onLoading,
        onFailure,
        onSuccess,
    )
}

/**
 * A composable that is used to display a [Painter] resource.
 * To load an image [Resource] asynchronously, use [lazyPainterResource].
 * @param resource The [Resource] that needs to be displayed.
 * @param modifier The modifier that is applied to the [Box].
 * @param contentAlignment The default alignment inside the Box.
 * @param animationSpec a [FiniteAnimationSpec] to be used in [Crossfade] animation, or null to be disabled.
 * @param onLoading Composable which is used while the image is in [Resource.Loading] state.
 * @param onFailure Composable which is used while the image is in [Resource.Failure] state.
 * @param onSuccess Composable which is used while the image is in [Resource.Success] state.
 */
@ExperimentalKamelApi
@Composable
public fun KamelImageBox(
    resource: Resource<Painter>,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
    animationSpec: FiniteAnimationSpec<Float>? = null,
    onLoading: @Composable (BoxScope.(Float) -> Unit)? = null,
    onFailure: @Composable (BoxScope.(Throwable) -> Unit)? = null,
    onSuccess: @Composable BoxScope.(Painter) -> Unit,
) {
    Box(modifier, contentAlignment) {
        if (animationSpec != null) {
            Crossfade(resource, animationSpec = animationSpec) { animatedResource ->
                when (animatedResource) {
                    is Resource.Loading -> if (onLoading != null) onLoading(animatedResource.progress)
                    is Resource.Success -> onSuccess(animatedResource.value)
                    is Resource.Failure -> if (onFailure != null) onFailure(animatedResource.exception)
                }
            }
        } else {
            when (resource) {
                is Resource.Loading -> if (onLoading != null) onLoading(resource.progress)
                is Resource.Success -> onSuccess(resource.value)
                is Resource.Failure -> if (onFailure != null) onFailure(resource.exception)
            }
        }
    }
}