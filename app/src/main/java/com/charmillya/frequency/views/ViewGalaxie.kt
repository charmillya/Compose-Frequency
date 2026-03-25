package com.charmillya.frequency.views

import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.EaseInBack
import androidx.compose.animation.core.EaseInCirc
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.Spring.StiffnessLow
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.charmillya.frequency.LocalHazeState
import com.charmillya.frequency.R
import com.charmillya.frequency.composables.HazedScaffoldLazyColumn
import com.charmillya.frequency.models.Lien
import com.charmillya.frequency.ui.theme.gradientBrush
import com.charmillya.frequency.viewmodels.ViewModelGalaxie
import compose.icons.TablerIcons
import compose.icons.tablericons.Planet
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.HazeMaterials
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sin
import kotlin.math.sqrt


private data class StarData(
    val xRatio: Float,    
    val yRatio: Float,    
    val sizeBase: Float,  
    val blinkOffset: Float 
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ViewGalaxie(
    viewModel: ViewModelGalaxie = viewModel(),
    isVisible: Boolean = true,
    onAjouterLienClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val listeLiens by viewModel.listeLiens.collectAsState()
    val orbitStyle by viewModel.orbitStyle.collectAsState()
    val rotationSpeed by viewModel.rotationSpeed.collectAsState()
    val layoutStyle by viewModel.layoutStyle.collectAsState()

    val density = LocalDensity.current
    val hazeState = LocalHazeState.current
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val scope = rememberCoroutineScope()
    val sessionSeed = remember { kotlin.random.Random.nextInt() }
    val textMeasurer = rememberTextMeasurer()
    val isSystemDark = isSystemInDarkTheme()

    val labelToday = stringResource(R.string.time_today)
    val labelDaySuffix = stringResource(R.string.time_day_suffix)

    val bottomOffsetPx = with(density) { 230.dp.toPx() }

    val backgroundStars = remember {
        List(400) {
            StarData(
                xRatio = kotlin.random.Random.nextFloat(),
                yRatio = kotlin.random.Random.nextFloat(),
                sizeBase = 0.5f + kotlin.random.Random.nextFloat() * 2.5f,
                blinkOffset = kotlin.random.Random.nextFloat() * 2 * PI.toFloat()
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "stars")
    val starTimeAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "starTwinkle"
    )

    val sunEntranceScale = remember { Animatable(0f) }
    val orbitEntranceScale = remember { Animatable(0f) }
    val globalPlanetsAlpha = remember { Animatable(0f) }
    val nebulaExpansion = remember { Animatable(0f) }

    LaunchedEffect(isVisible) {
        if (isVisible) {
            launch {
                nebulaExpansion.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 3000, easing = EaseOutCubic)
                )
            }
            launch {
                sunEntranceScale.animateTo(
                    1f,
                    spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow)
                )
            }
            launch {
                delay(100)
                orbitEntranceScale.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 1500, easing = EaseOutQuart)
                )
            }
            launch {
                delay(200)
                globalPlanetsAlpha.animateTo(1f, tween(1200))
            }
        } else {
            launch {
                nebulaExpansion.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 800, easing = EaseInCirc)
                )
            }
            launch {
                sunEntranceScale.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 600, easing = EaseInBack)
                )
            }
            launch {
                globalPlanetsAlpha.animateTo(0f, tween(400))
            }
            launch {
                orbitEntranceScale.animateTo(0f, tween(400))
            }
        }
    }

    var totalRotation by remember { mutableFloatStateOf(0f) }
    val currentRotationSpeed by rememberUpdatedState(rotationSpeed)

    LaunchedEffect(rotationSpeed) {
        var lastTimeMillis = withFrameMillis { it }
        while (true) {
            withFrameMillis { time ->
                val deltaTime = (time - lastTimeMillis) / 1000f
                totalRotation += (360f / 60f) * deltaTime * currentRotationSpeed
                lastTimeMillis = time
            }
        }
    }

    var scale by remember { mutableFloatStateOf(2.5f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var selectedLienId by remember { mutableStateOf<String?>(null) }
    var selectedLienPosition by remember { mutableStateOf(Offset.Zero) }

    val sunPressScale = remember { Animatable(1f) }
    var isSunPressed by remember { mutableStateOf(false) }

    val dim = remember(density) {
        object {
            val soleil = with(density) { 60.dp.toPx() }
            val planete = with(density) { 30.dp.toPx() }
            val espacement = with(density) { 100.dp.toPx() }
            val orbitStart = with(density) { 135.dp.toPx() }
        }
    }

    val animatedDistances = remember { mutableStateMapOf<String, Animatable<Float, AnimationVector1D>>() }
    val animatedAngles = remember { mutableStateMapOf<String, Animatable<Float, AnimationVector1D>>() }
    val animatedScales = remember { mutableStateMapOf<String, Animatable<Float, AnimationVector1D>>() }
    val noteScales = remember { mutableStateMapOf<String, Animatable<Float, AnimationVector1D>>() }
    val textDirAnimatables = remember { mutableStateMapOf<String, Animatable<Float, AnimationVector1D>>() }

    val currentAngleMap = remember { mutableStateMapOf<String, Float>() }
    val previousSpeeds = remember { mutableStateMapOf<String, Float>() }

    val planetesData = remember(listeLiens, layoutStyle) {
        
        
        val thresholds = listOf(3, 7, 21, 30, 60, 180, 365)

        
        val buckets = Array(thresholds.size + 1) { mutableListOf<Lien>() }
        val now = LocalDate.now(ZoneId.systemDefault())

        listeLiens.forEach { lien ->
            val interactionTime = lien.lastInteractionDay ?: 0L
            val daysDiff = if (interactionTime > 0) {
                val dateInteraction = Instant.ofEpochMilli(interactionTime)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                ChronoUnit.DAYS.between(dateInteraction, now).toInt()
            } else { Int.MAX_VALUE }

            
            var bucketIndex = buckets.lastIndex
            for (i in thresholds.indices) {
                if (daysDiff <= thresholds[i]) {
                    bucketIndex = i
                    break
                }
            }
            buckets[bucketIndex].add(lien)
        }

        val result = mutableListOf<Triple<Lien, Pair<Float, Float>, Float>>()
        var currentOrbitIndex = 0

        
        buckets.forEach { bucket ->
            if (bucket.isEmpty()) {
                
                currentOrbitIndex++
            } else {
                var liensRestants = bucket.toList() 

                
                while (liensRestants.isNotEmpty()) {
                    val maxCapacity = 7 + (currentOrbitIndex * 2)

                    
                    val chunk = liensRestants.take(maxCapacity).shuffled(kotlin.random.Random(sessionSeed + currentOrbitIndex))
                    liensRestants = liensRestants.drop(maxCapacity)

                    val dist = dim.orbitStart + (currentOrbitIndex * dim.espacement)
                    val count = chunk.size

                    val baseOrbitSpeed = 0.6f + kotlin.random.Random(currentOrbitIndex + sessionSeed).nextFloat() * 0.7f
                    val orbitRotationSeed = kotlin.random.Random(currentOrbitIndex + sessionSeed).nextFloat() * 360f

                    
                    
                    val minAngleSpacing = 360f / (count * 1.2f)
                    val espaceRestant = 360f - (minAngleSpacing * count)

                    val randomParts = List(count) { kotlin.random.Random(sessionSeed + it + currentOrbitIndex).nextFloat() }
                    val totalParts = randomParts.sum()

                    var currentAngle = orbitRotationSeed
                    val randomAngles = randomParts.map { part ->
                        val angleAssigne = currentAngle
                        val ajoutAleatoire = if (totalParts > 0) (part / totalParts) * espaceRestant else 0f
                        currentAngle += minAngleSpacing + ajoutAleatoire
                        angleAssigne
                    }

                    chunk.forEachIndexed { index, lien ->
                        val speedModifier = if (count == 1) 0.6f + (kotlin.random.Random(sessionSeed + index).nextFloat() * 0.8f) else 1f

                        result.add(Triple(
                            lien,
                            Pair(dist, randomAngles[index]),
                            baseOrbitSpeed * speedModifier
                        ))
                    }

                    currentOrbitIndex++
                }
            }
        }
        result
    }

    val maxOrbitRadius = remember(planetesData) { planetesData.maxOfOrNull { it.second.first } ?: dim.orbitStart }

    LaunchedEffect(planetesData) {
        planetesData.forEachIndexed { index, (lien, targets, personalitySpeed) ->
            val id = lien.idLien
            val isNew = !animatedDistances.containsKey(id)
            val animDist = animatedDistances.getOrPut(id) { Animatable(0f) }
            val animAngle = animatedAngles.getOrPut(id) { Animatable(targets.second) }
            val animNote = noteScales.getOrPut(id) { Animatable(0f) }
            animatedScales.getOrPut(id) { Animatable(1f) }

            val oldSpeed = previousSpeeds.getOrElse(id) { personalitySpeed }
            if (oldSpeed != personalitySpeed) {
                val speedDiff = oldSpeed - personalitySpeed
                val rotationCompensation = totalRotation * speedDiff
                animAngle.snapTo(animAngle.value + rotationCompensation)
            }
            previousSpeeds[id] = personalitySpeed
            launch {
                if (isNew) delay(150L + (index * 50L))
                launch {
                    animDist.animateTo(
                        targets.first,
                        spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow)
                    )
                }
                launch {
                    val currentAngle = animAngle.value
                    val targetAngle = targets.second
                    val closestTarget =
                        targetAngle + 360f * round((currentAngle - targetAngle) / 360f)
                    animAngle.animateTo(
                        closestTarget,
                        spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow)
                    )
                }
                launch {
                    animNote.animateTo(
                        1f,
                        spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow)
                    )
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            planetesData.forEach { (lien, _, _) ->
                animatedAngles[lien.idLien]?.value?.let {
                    currentAngleMap[lien.idLien] = it
                }
            }
        }
    }

    val rotationState = rememberUpdatedState(totalRotation)

    LaunchedEffect(Unit) {
        while (true) {
            delay(100)
            planetesData.forEach { (lien, _, personality) ->
                val currentAngleBase = animatedAngles[lien.idLien]?.value ?: 0f
                val distance = animatedDistances[lien.idLien]?.value ?: 0f
                val currentTotalAngle = currentAngleBase + (rotationState.value * personality)
                val rad = currentTotalAngle * (PI.toFloat() / 180f)
                val visualX = offset.x + (distance * cos(rad) * scale)
                val targetDir = if (visualX > 0) -1f else 1f
                val anim = textDirAnimatables.getOrPut(lien.idLien) { Animatable(targetDir) }
                if (anim.targetValue != targetDir) {
                    launch {
                        anim.animateTo(
                            targetDir,
                            tween(durationMillis = 400, easing = LinearOutSlowInEasing)
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(selectedLienId) {
        animatedScales.forEach { (id, animatable) ->
            animatable.animateTo(
                if (id == selectedLienId) 1.25f else 1.0f,
                spring(stiffness = Spring.StiffnessLow)
            )
        }
    }

    val state = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(0.1f, 4.0f)
        val newOffset = offset + panChange
        val limitRadius = (maxOrbitRadius + 300f) * scale
        if (newOffset.getDistance() < limitRadius) {
            offset = newOffset
        } else {
            val angle = atan2(newOffset.y, newOffset.x)
            offset = Offset(cos(angle) * limitRadius, sin(angle) * limitRadius)
        }
    }

    val zoomInExp = remember(scale) { ((scale - 2.8f) / 1.2f).coerceIn(0f, 1f) }
    val zoomOutExp = remember(scale) { ((0.6f - scale) / 0.2f).coerceIn(0f, 1f) }

    val bitmaps = remember { mutableStateMapOf<String, ImageBitmap>() }

    LaunchedEffect(listeLiens) {
        withContext(Dispatchers.IO) {
            listeLiens.forEach { lien ->
                lien.imagePath?.let { path ->
                    if (File(path).exists() && !bitmaps.containsKey(lien.idLien)) {
                        try {
                            BitmapFactory.decodeFile(path)?.asImageBitmap()
                                ?.let { bitmaps[lien.idLien] = it }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    val selectedLien =
        remember(selectedLienId, listeLiens) { listeLiens.find { it.idLien == selectedLienId } }
    var rootSize by remember { mutableStateOf(IntSize.Zero) }

    HazedScaffoldLazyColumn(title = stringResource(R.string.galaxy_title), hazeState = hazeState) {
        item {
            BoxWithConstraints(
                modifier = Modifier
                    .fillParentMaxSize()
                    .onGloballyPositioned { rootSize = it.size }
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            
                            scale = (scale * zoom).coerceIn(0.4f, 4.0f)

                            
                            
                            val newOffset = offset + pan
                            val limitRadius = (maxOrbitRadius + 300f) * scale

                            if (newOffset.getDistance() < limitRadius) {
                                offset = newOffset
                            } else {
                                
                                val angle = atan2(newOffset.y, newOffset.x)
                                offset = Offset(cos(angle) * limitRadius, sin(angle) * limitRadius)
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                val screenMinDimension = minOf(constraints.maxWidth, constraints.maxHeight).toFloat()
                val targetScale = remember(maxOrbitRadius, screenMinDimension) {
                    
                    (screenMinDimension / (maxOrbitRadius * 2.2f)).coerceIn(0.1f, 1.5f)
                }

                LaunchedEffect(targetScale) {
                    
                    
                    androidx.compose.animation.core.animate(
                        initialValue = scale,
                        targetValue = targetScale,
                        animationSpec = tween(durationMillis = 2000, easing = EaseOutQuart)
                    ) { value, _ ->
                        scale = value
                    }
                }

                val visualCenter = Offset(constraints.maxWidth / 2f, (constraints.maxHeight - bottomOffsetPx) / 2f)

                if (listeLiens.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.TopCenter
                    ) { EmptyGalaxieContent(onAjouterLienClick) }
                } else {
                    val currentScaleState = rememberUpdatedState(scale)
                    val currentOffsetState = rememberUpdatedState(offset)
                    val currentPlanetesDataState = rememberUpdatedState(planetesData)
                    val currentRotationState = rememberUpdatedState(rotationState.value)

                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) { 
                                detectTapGestures(
                                    onPress = { tapOffset ->
                                        val curScale = currentScaleState.value
                                        val curOffset = currentOffsetState.value
                                        val sunPos = visualCenter + curOffset

                                        if (sqrt((tapOffset.x - sunPos.x).pow(2) + (tapOffset.y - sunPos.y).pow(2)) < dim.soleil * curScale) {
                                            isSunPressed = true
                                            scope.launch {
                                                sunPressScale.animateTo(0.85f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow))
                                            }
                                            try {
                                                awaitRelease()
                                            } finally {
                                                isSunPressed = false
                                                scope.launch {
                                                    sunPressScale.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, StiffnessLow))
                                                }
                                            }
                                        }
                                    },
                                    onTap = { tapOffset ->
                                        val curScale = currentScaleState.value
                                        val curOffset = currentOffsetState.value
                                        val curPlanetes = currentPlanetesDataState.value
                                        val curRot = currentRotationState.value

                                        var foundId: String? = null
                                        var foundPos = Offset.Zero

                                        curPlanetes.forEach { (lien, _, personality) ->
                                            val d = animatedDistances[lien.idLien]?.value ?: 0f
                                            val a = (animatedAngles[lien.idLien]?.value ?: 0f) + (curRot * personality)
                                            val rad = a * (PI.toFloat() / 180f)

                                            
                                            val rawPos = visualCenter + curOffset + Offset(
                                                d * cos(rad) * curScale,
                                                d * sin(rad) * curScale
                                            )

                                            if (sqrt((tapOffset.x - rawPos.x).pow(2) + (tapOffset.y - rawPos.y).pow(2)) < dim.planete * curScale * 1.6f) {
                                                foundId = lien.idLien
                                                foundPos = rawPos
                                            }
                                        }
                                        selectedLienId = foundId
                                        selectedLienPosition = foundPos
                                    }
                                )
                            }
                    ) {
                        val sunBlob = sunEntranceScale.value
                        val sunRadius = dim.soleil * sunBlob * sunPressScale.value
                        val orbitScaleFactor = orbitEntranceScale.value
                        val nebulaProgress = nebulaExpansion.value

                        if (nebulaProgress > 0.1f) {
                            dessinerEtoiles(
                                stars = backgroundStars,
                                globalAlpha = nebulaProgress,
                                time = starTimeAnim,
                                isDark = isSystemDark,
                                center = visualCenter
                            )
                        }

                        if (nebulaProgress > 0f) {
                            dessinerNebuleuseBackground(visualCenter, nebulaProgress, isSystemDark)
                        }

                        withTransform({
                            translate(offset.x, offset.y); scale(
                            scale,
                            scale,
                            visualCenter
                        )
                        }) {
                            dessinerSoleilKawaii(
                                visualCenter,
                                sunRadius,
                                zoomInExp,
                                zoomOutExp,
                                isSunPressed
                            )
                            val uniqueDistances = planetesData.map { it.second.first }.distinct()
                            uniqueDistances.forEach { d ->
                                dessinerOrbite(
                                    visualCenter,
                                    d * orbitScaleFactor,
                                    orbitStyle,
                                    onSurfaceColor,
                                    globalPlanetsAlpha.value
                                )
                            }
                            planetesData.forEach { (lien, _, personality) ->
                                val d = animatedDistances[lien.idLien]?.value ?: 0f
                                val a = (animatedAngles[lien.idLien]?.value
                                    ?: 0f) + (rotationState.value * personality)
                                val s = animatedScales[lien.idLien]?.value ?: 1f
                                val nS = noteScales[lien.idLien]?.value ?: 0f
                                val textDir = textDirAnimatables[lien.idLien]?.value ?: 1f

                                
                                val interactionTime = lien.lastInteractionDay ?: 0L
                                val daysDiff = if (interactionTime > 0) {
                                    val dateInteraction = Instant.ofEpochMilli(interactionTime)
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate()
                                    ChronoUnit.DAYS.between(dateInteraction, LocalDate.now(ZoneId.systemDefault())).toInt()
                                } else { Int.MAX_VALUE }

                                val isEroded = daysDiff > 30
                                val isPopular = (lien.interactionCount) > 30
                                val isRecent = daysDiff < 3

                                dessinerPlanetePhoto(
                                    visualCenter,
                                    d,
                                    a,
                                    dim.planete * s,
                                    bitmaps[lien.idLien],
                                    onSurfaceColor,
                                    globalPlanetsAlpha.value,
                                    lien.lastInteractionDay,
                                    textMeasurer,
                                    isSystemDark,
                                    nS,
                                    textDir,
                                    labelToday,
                                    labelDaySuffix,
                                    isEroded,
                                    isPopular,
                                    isRecent
                                )
                            }
                        }
                    }
                }

                if (selectedLien != null) {
                    LienDetailModal(
                        lien = selectedLien,
                        originPosition = selectedLienPosition,
                        rootSize = rootSize,
                        onInteractionClick = { id ->
                            viewModel.enregistrerInteraction(id); selectedLienId = null
                        },
                        onDismiss = { selectedLienId = null }
                    )
                }
            }
        }
    }
}

private fun DrawScope.dessinerEtoiles(
    stars: List<StarData>,
    globalAlpha: Float,
    time: Float,
    isDark: Boolean,
    center: Offset
) {
    val starColor = if (isDark) Color.White else Color(0xFF90A4AE)
    val exclusionRadius = size.minDimension * 0.12f

    stars.forEach { star ->
        val x = star.xRatio * size.width
        val y = star.yRatio * size.height
        val pos = Offset(x, y)
        val dist = (pos - center).getDistance()

        if (dist > exclusionRadius) {
            val alphaDistance =
                ((dist - exclusionRadius) / (exclusionRadius * 0.5f)).coerceIn(0f, 1f)

            val twinkle = sin(time + star.blinkOffset)
            val alphaTwinkle = (0.5f + (twinkle * 0.5f)).coerceIn(0.2f, 1f)
            val finalAlpha =
                alphaTwinkle * globalAlpha * (if (isDark) 0.8f else 0.5f) * alphaDistance

            drawCircle(
                color = starColor.copy(alpha = finalAlpha),
                radius = star.sizeBase.dp.toPx(),
                center = pos
            )
        }
    }
}

fun DrawScope.dessinerNebuleuseBackground(
    centre: Offset,
    expansionProgress: Float,
    isDark: Boolean
) {
    val maxDimension = size.width.coerceAtLeast(size.height) * 1.6f
    val currentRadius = maxDimension * expansionProgress
    if (currentRadius <= 0f) return

    val (baseCol, cloud1, cloud2, accent) = if (isDark) {
        listOf(
            Color(0xFF02040A),
            Color(0xFF0A1128),
            Color(0xFF1C2541),
            Color(0xFF4A148C).copy(alpha = 0.3f)
        )
    } else {
        listOf(
            Color(0xFFF9FAFF),
            Color(0xFFE3F2FD),
            Color(0xFFF3E5F5),
            Color(0xFFF8BBD0).copy(alpha = 0.3f)
        )
    }

    drawCircle(
        brush = Brush.radialGradient(
            0.0f to baseCol.copy(alpha = (if (isDark) 0.95f else 0.6f) * expansionProgress),
            0.6f to cloud1.copy(alpha = 0.8f * expansionProgress),
            1.0f to baseCol.copy(alpha = 1f * expansionProgress),
            center = centre, radius = currentRadius
        ),
        radius = currentRadius, center = centre
    )

    drawCircle(
        brush = Brush.radialGradient(
            0.0f to baseCol.copy(alpha = 0.8f * expansionProgress),
            0.4f to cloud2.copy(alpha = 0.3f * expansionProgress),
            1.0f to Color.Transparent,
            center = centre, radius = currentRadius * 0.9f
        ),
        radius = currentRadius * 0.9f, center = centre
    )

    drawCircle(
        brush = Brush.radialGradient(
            0.4f to accent.copy(alpha = 0.15f * expansionProgress),
            0.8f to Color.Transparent,
            center = centre, radius = currentRadius * 0.7f
        ),
        radius = currentRadius * 0.7f, center = centre
    )
}

fun DrawScope.dessinerSoleilKawaii(
    centre: Offset,
    rayon: Float,
    zoomInExp: Float,
    zoomOutExp: Float,
    isPressed: Boolean
) {
    if (rayon <= 0f) return
    val colorBodyBase = Color(0xFFFFE082);
    val colorBodyShadow = Color(0xFFFFB74D);
    val colorFaceDetails = Color(0xFF5D4037);
    val colorCheeks = Color(0xFFFF8A65);
    val colorHighlight = Color.White.copy(alpha = 0.6f)
    val strokeScale = (rayon / 60.dp.toPx()).coerceAtLeast(0.5f);
    val strokeWidthStandard = 5f * strokeScale

    drawCircle(
        brush = Brush.radialGradient(
            0.0f to Color(0xFFFFE082).copy(alpha = 0.35f + (zoomInExp * 0.1f)),
            0.4f to Color(0xFFFFE082).copy(alpha = 0.15f),
            1.0f to Color.Transparent,
            center = centre,
            radius = rayon * 6.0f
        ), radius = rayon * 6.0f, center = centre
    )
    drawCircle(
        brush = Brush.radialGradient(
            0.0f to Color(0xFFFFD54F).copy(alpha = 0.7f),
            0.5f to Color(0xFFFFAB00).copy(alpha = 0.2f),
            1.0f to Color.Transparent,
            center = centre,
            radius = rayon * 2.0f
        ), radius = rayon * 2.0f, center = centre
    )
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(colorBodyBase, colorBodyShadow),
            center = centre + Offset(-rayon * 0.3f, -rayon * 0.3f),
            radius = rayon * 1.8f
        ), radius = rayon, center = centre
    )

    val highlightSize = Size(rayon * 0.5f, rayon * 0.35f);
    val highlightTopLeft = centre + Offset(-rayon * 0.6f, -rayon * 0.65f)
    withTransform({
        rotate(
            degrees = -45f,
            pivot = highlightTopLeft + Offset(highlightSize.width / 2, highlightSize.height / 2)
        )
    }) {
        drawOval(
            brush = Brush.linearGradient(
                colors = listOf(
                    colorHighlight,
                    Color.White.copy(alpha = 0.1f)
                ),
                start = highlightTopLeft,
                end = highlightTopLeft + Offset(0f, highlightSize.height)
            ), topLeft = highlightTopLeft, size = highlightSize
        )
    }

    val eyeOffsetX = rayon * 0.35f;
    val eyeOffsetY = -rayon * 0.05f;
    val eyeWidth = rayon * 0.22f;
    val eyeHeight = rayon * 0.18f;
    val cheekSize = Size(rayon * 0.25f, rayon * 0.15f);
    val cheekOffsetY = rayon * 0.15f;
    val cheekOffsetX = rayon * 0.45f
    if (!isPressed && zoomInExp < 0.8f) {
        drawOval(
            colorCheeks,
            topLeft = centre + Offset(-cheekOffsetX - cheekSize.width / 2, cheekOffsetY),
            size = cheekSize
        ); drawOval(
            colorCheeks,
            topLeft = centre + Offset(cheekOffsetX - cheekSize.width / 2, cheekOffsetY),
            size = cheekSize
        )
    }
    if (isPressed) {
        val vS = rayon * 0.15f;
        val pL = Path().apply {
            moveTo(
                centre.x - eyeOffsetX - vS,
                centre.y + eyeOffsetY - vS
            ); lineTo(
            centre.x - eyeOffsetX,
            centre.y + eyeOffsetY
        ); lineTo(centre.x - eyeOffsetX - vS, centre.y + eyeOffsetY + vS)
        };
        val pR = Path().apply {
            moveTo(
                centre.x + eyeOffsetX + vS,
                centre.y + eyeOffsetY - vS
            ); lineTo(
            centre.x + eyeOffsetX,
            centre.y + eyeOffsetY
        ); lineTo(centre.x + eyeOffsetX + vS, centre.y + eyeOffsetY + vS)
        }
        val pressedStroke =
            Stroke(strokeWidthStandard, cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawPath(pL, colorFaceDetails, style = pressedStroke); drawPath(
            pR,
            colorFaceDetails,
            style = pressedStroke
        ); drawCircle(
            color = colorFaceDetails,
            radius = rayon * 0.08f,
            center = centre + Offset(0f, rayon * 0.25f),
            style = Stroke(strokeWidthStandard)
        )
    } else if (zoomInExp > 0.1f) {
        val colorK = colorFaceDetails.copy(alpha = zoomInExp);
        val openEyeRadius = rayon * 0.12f * (0.8f + zoomInExp * 0.4f)
        drawCircle(
            colorK,
            radius = openEyeRadius,
            center = centre + Offset(-eyeOffsetX, eyeOffsetY),
            style = Stroke(strokeWidthStandard)
        ); drawCircle(
            colorK,
            radius = openEyeRadius,
            center = centre + Offset(eyeOffsetX, eyeOffsetY),
            style = Stroke(strokeWidthStandard)
        );
        val mouthWidth = rayon * 0.1f; drawArc(
            color = colorK,
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = centre + Offset(-mouthWidth / 2, rayon * 0.2f),
            size = Size(mouthWidth, mouthWidth / 2),
            style = Stroke(strokeWidthStandard, cap = StrokeCap.Round)
        )
    } else {
        val alphaN = (1f - zoomInExp).coerceIn(0f, 1f);
        val styleEyes = Stroke(strokeWidthStandard, cap = StrokeCap.Round)
        drawArc(
            color = colorFaceDetails.copy(alpha = alphaN),
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = centre + Offset(-eyeOffsetX - eyeWidth / 2, eyeOffsetY - eyeHeight / 2),
            size = Size(eyeWidth, eyeHeight),
            style = styleEyes
        ); drawArc(
            color = colorFaceDetails.copy(alpha = alphaN),
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = centre + Offset(eyeOffsetX - eyeWidth / 2, eyeOffsetY - eyeHeight / 2),
            size = Size(eyeWidth, eyeHeight),
            style = styleEyes
        )
        if (zoomOutExp > 0f) {
            val mWidth = rayon * 0.18f * zoomOutExp;
            val mHeight = rayon * 0.22f * zoomOutExp; drawArc(
                color = colorFaceDetails.copy(alpha = zoomOutExp),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = centre + Offset(-mWidth / 2, rayon * 0.25f),
                size = Size(mWidth, mHeight),
                style = Stroke(width = strokeWidthStandard * 0.6f)
            )
        } else {
            drawArc(
                color = colorFaceDetails.copy(alpha = alphaN),
                startAngle = 15f,
                sweepAngle = 150f,
                useCenter = false,
                topLeft = centre + Offset(-rayon * 0.15f, rayon * 0.1f),
                size = Size(rayon * 0.3f, rayon * 0.25f),
                style = Stroke(strokeWidthStandard, cap = StrokeCap.Round)
            )
        }
    }
}

fun DrawScope.dessinerOrbite(
    centre: Offset,
    distance: Float,
    style: String,
    color: Color,
    alpha: Float
) {
    if (style == "hidden" || distance <= 0f) return
    val strokeStyle = if (style == "dotted") Stroke(
        width = 1.2.dp.toPx(),
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 20f), 0f)
    ) else Stroke(width = 1.dp.toPx())
    drawCircle(color.copy(alpha = 0.12f * alpha), distance, centre, style = strokeStyle)
}

@RequiresApi(Build.VERSION_CODES.O)
fun DrawScope.dessinerPlanetePhoto(
    centre: Offset,
    distance: Float,
    angle: Float,
    rayon: Float,
    image: ImageBitmap?,
    color: Color,
    alpha: Float,
    lastInt: Long?,
    measurer: TextMeasurer,
    isDark: Boolean,
    nScale: Float,
    textOffsetDir: Float,
    labelToday: String,
    labelSuffix: String,
    isEroded: Boolean,
    isPopular: Boolean,
    isRecent: Boolean
) {
    val rad = angle * (PI.toFloat() / 180f);
    val pos = centre + Offset(distance * cos(rad), distance * sin(rad))

    if (isPopular && isRecent && alpha > 0.1f) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF64FFDA).copy(alpha = 0.6f * alpha), 
                    Color.Transparent
                ),
                center = pos,
                radius = rayon * 1.6f
            ),
            radius = rayon * 1.6f,
            center = pos
        )
    }

    if (image != null) {
        val path = Path().apply { addOval(Rect(pos, rayon)) }; clipPath(path) {
            val sF = max(rayon * 2 / image.width, rayon * 2 / image.height)

            val colorFilter = if (isEroded) ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) }) else null
            val erosionAlpha = if (isEroded) alpha * 0.7f else alpha 

            drawImage(
                image,
                dstOffset = IntOffset(
                    (pos.x - image.width * sF / 2).toInt(),
                    (pos.y - image.height * sF / 2).toInt()
                ),
                dstSize = IntSize((image.width * sF).toInt(), (image.height * sF).toInt()),
                alpha = erosionAlpha,
                colorFilter = colorFilter
            )
        }
        drawCircle(color.copy(0.2f * alpha), rayon, pos, style = Stroke(1.dp.toPx()))
    } else {
        drawCircle(color.copy(0.1f * alpha), rayon, pos)
    }

    if (lastInt != null && lastInt > 0 && nScale > 0.01f) {
        
        val dateInteraction = Instant.ofEpochMilli(lastInt)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        
        val dateAujourdhui = LocalDate.now(ZoneId.systemDefault())

        
        val jours = ChronoUnit.DAYS.between(dateInteraction, dateAujourdhui).toInt()

        val text = if (jours <= 0) labelToday else "$jours$labelSuffix"
        val notePos = pos + Offset(rayon * 0.75f * textOffsetDir, -rayon * 0.9f)
        val layout = measurer.measure(
            text = text,
            style = TextStyle(
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color.White else Color.Black
            ),
            softWrap = false,
            maxLines = 1
        )
        val bSize = Size(layout.size.width + 14.dp.toPx(), layout.size.height + 6.dp.toPx())
        withTransform({ scale(nScale * alpha, nScale * alpha, notePos) }) {
            drawCircle(
                if (isDark) Color(0xFF262626) else Color.White,
                rayon * 0.15f,
                notePos + Offset(-5.dp.toPx() * textOffsetDir, 5.dp.toPx())
            )
            drawRoundRect(
                if (isDark) Color(0xFF262626) else Color.White,
                notePos - Offset(bSize.width / 2, bSize.height / 2),
                bSize,
                CornerRadius(10.dp.toPx())
            )
            drawText(
                textLayoutResult = layout,
                topLeft = notePos - Offset(layout.size.width / 2f, layout.size.height / 2f)
            )
        }
    }
}

@Composable
fun LienDetailModal(
    lien: Lien,
    originPosition: Offset,
    rootSize: IntSize,
    onInteractionClick: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) { detectTapGestures { onDismiss() } },
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = scaleIn(
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow),
                    transformOrigin = TransformOrigin(
                        pivotFractionX = (originPosition.x / rootSize.width).coerceIn(0f, 1f),
                        pivotFractionY = (originPosition.y / rootSize.height).coerceIn(0f, 1f)
                    )
                ) + fadeIn(tween(150)),
                exit = scaleOut(tween(150)) + fadeOut(tween(150))
            ) {
                Card(
                    modifier = Modifier
                        .padding(32.dp)
                        .widthIn(max = 320.dp)
                        .wrapContentHeight()
                        .clip(RoundedCornerShape(32.dp))
                        .hazeEffect(LocalHazeState.current, HazeMaterials.ultraThin())
                        .border(2.dp, gradientBrush, RoundedCornerShape(32.dp))
                        .clickable(enabled = false) {},
                    colors = CardDefaults.cardColors(
                        if (isDark) Color.Black.copy(0.7f) else Color.White.copy(0.8f)
                    )
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {

                        DessinerEntetePhoto(
                            nom = lien.name,
                            imagePath = lien.imagePath,
                            height = 140.dp
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 140.dp)
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val dateStr = remember(lien.lastInteractionDay) {
                                val ts = lien.lastInteractionDay ?: 0L
                                if (ts <= 0) "Aucune interaction" else SimpleDateFormat(
                                    "d MMMM yyyy", Locale.getDefault()
                                ).format(Date(ts))
                            }

                            Text(
                                stringResource(R.string.last_interaction_label),
                                color = MaterialTheme.colorScheme.onSurface.copy(0.5f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(dateStr, fontSize = 14.sp)

                            Spacer(Modifier.height(24.dp))

                            Button(
                                onClick = { onInteractionClick(lien.idLien) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(gradientBrush),
                                colors = ButtonDefaults.buttonColors(Color.Transparent)
                            ) {
                                Text(
                                    stringResource(R.string.interacted_button),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            TextButton(onClick = onDismiss, modifier = Modifier.padding(top = 8.dp)) {
                                Text(stringResource(R.string.close_button), color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LazyItemScope.EmptyGalaxieContent(onAjouterLienClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillParentMaxHeight(0.8f)
            .fillMaxWidth()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(TablerIcons.Planet, null, Modifier.size(64.dp), Color.Gray.copy(alpha = 0.5f))
        Text(
            stringResource(R.string.empty_galaxy_message),
            Modifier.padding(top = 16.dp),
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Button(
            onClick = onAjouterLienClick,
            modifier = Modifier
                .padding(top = 16.dp)
                .background(gradientBrush, CircleShape),
            colors = ButtonDefaults.buttonColors(Color.Transparent)
        ) {
            Text(
                stringResource(R.string.create_first_link),
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}