import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxSize 
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier 
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(
    resourceId: Int, 
    modifier: Modifier = Modifier, 
    isLooping: Boolean = true,
    isPlaying: Boolean = true
) {
    val context = LocalContext.current

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val videoUri = Uri.parse("android.resource://${context.packageName}/$resourceId")
            val mediaItem = MediaItem.fromUri(videoUri)
            setMediaItem(mediaItem)

            repeatMode = if (isLooping) ExoPlayer.REPEAT_MODE_ONE else ExoPlayer.REPEAT_MODE_OFF
            prepare()
        }
    }

    DisposableEffect(Unit) {
        if (isPlaying) {
            exoPlayer.playWhenReady = true
            exoPlayer.play()
        }
        onDispose {
            exoPlayer.release()
        }
    }

    AndroidView(
        
        modifier = modifier.fillMaxSize(),
        factory = {
            PlayerView(context).apply {
                player = exoPlayer
                useController = false

                
                
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            }
        }
    )
}