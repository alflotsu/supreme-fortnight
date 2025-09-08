package io.peng.sparrowdelivery.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.peng.sparrowdelivery.R
import io.peng.sparrowdelivery.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.*
import kotlin.random.Random

data class Particle(
    var x: Float,
    var y: Float,
    var velocityX: Float,
    var velocityY: Float,
    var size: Float,
    var alpha: Float,
    var color: Color,
    var life: Float = 1f,
    var maxLife: Float = 1f
)

@Composable
fun EnhancedSplashScreen(
    onSplashComplete: () -> Unit,
    modifier: Modifier = Modifier,
    splashDuration: Long = 3000L,
    particleCount: Int = 50,
    showParticles: Boolean = true,
    showGradientBackground: Boolean = true
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    
    val screenWidth = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeight = with(density) { configuration.screenHeightDp.dp.toPx() }
    
    // Animation states
    var logoVisible by remember { mutableStateOf(false) }
    var textVisible by remember { mutableStateOf(false) }
    var particlesVisible by remember { mutableStateOf(false) }
    
    // Particle system state
    val particles = remember {
        mutableListOf<Particle>().apply {
            repeat(particleCount) {
                add(createRandomParticle(screenWidth, screenHeight))
            }
        }
    }
    
    // Animation values
    val logoScale by animateFloatAsState(
        targetValue = if (logoVisible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "logo_scale"
    )
    
    val logoRotation by animateFloatAsState(
        targetValue = if (logoVisible) 0f else -180f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logo_rotation"
    )
    
    val textAlpha by animateFloatAsState(
        targetValue = if (textVisible) 1f else 0f,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "text_alpha"
    )
    
    val backgroundAlpha by animateFloatAsState(
        targetValue = if (particlesVisible) 1f else 0f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "background_alpha"
    )
    
    // Particle animation
    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    val particleTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particle_time"
    )
    
    // Splash sequence
    LaunchedEffect(Unit) {
        delay(200)
        logoVisible = true
        
        delay(800)
        textVisible = true
        
        if (showParticles) {
            delay(300)
            particlesVisible = true
        }
        
        delay(splashDuration - 1300)
        onSplashComplete()
    }
    
    // Update particles
    LaunchedEffect(particleTime) {
        updateParticles(particles, screenWidth, screenHeight)
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                if (showGradientBackground) {
                    Brush.radialGradient(
                        colors = listOf(
                            ShadcnTheme.colors.primary.copy(alpha = 0.1f),
                            ShadcnTheme.colors.background,
                            ShadcnTheme.colors.background
                        ),
                        radius = screenWidth * 0.8f,
                        center = Offset(screenWidth * 0.5f, screenHeight * 0.4f)
                    )
                } else {
                    SolidColor(ShadcnTheme.colors.background)
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        // Particle system background
        if (showParticles) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(backgroundAlpha)
            ) {
                drawParticles(particles)
            }
        }
        
        // Main content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo with animations
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(logoScale)
                    .rotate(logoRotation)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                ShadcnTheme.colors.primary,
                                ShadcnTheme.colors.primary.copy(alpha = 0.8f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // You can replace this with your actual logo
                Icon(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "App Logo",
                    tint = ShadcnTheme.colors.primaryForeground,
                    modifier = Modifier.size(80.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // App name with fade animation
            Text(
                text = "Sparrow Delivery",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = ShadcnTheme.colors.foreground,
                modifier = Modifier.alpha(textAlpha)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Fast • Reliable • Secure",
                fontSize = 16.sp,
                color = ShadcnTheme.colors.mutedForeground,
                modifier = Modifier.alpha(textAlpha)
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Loading indicator
            AnimatedVisibility(
                visible = textVisible,
                enter = fadeIn(
                    animationSpec = tween(500, delayMillis = 300)
                )
            ) {
                PulsingLoadingIndicator()
            }
        }
        
        // Additional decorative elements
        if (showParticles) {
            FloatingShapes(
                visible = particlesVisible,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun PulsingLoadingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "loading_scale"
    )
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "loading_alpha"
    )
    
    Box(
        modifier = Modifier
            .size(40.dp)
            .scale(scale)
            .alpha(alpha)
            .background(
                ShadcnTheme.colors.primary.copy(alpha = 0.3f),
                CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(
                    ShadcnTheme.colors.primary,
                    CircleShape
                )
        )
    }
}

@Composable
private fun FloatingShapes(
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    val shapes = remember { createFloatingShapes(5) }
    
    shapes.forEachIndexed { index, shape ->
        val infiniteTransition = rememberInfiniteTransition(label = "shape_$index")
        
        val offsetY by infiniteTransition.animateFloat(
            initialValue = shape.initialY,
            targetValue = shape.initialY - 200f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = (3000..5000).random(),
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "shape_offset_y_$index"
        )
        
        val offsetX by infiniteTransition.animateFloat(
            initialValue = shape.initialX - 20f,
            targetValue = shape.initialX + 20f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = (2000..4000).random(),
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "shape_offset_x_$index"
        )
        
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = (8000..12000).random(),
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "shape_rotation_$index"
        )
        
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(
                animationSpec = tween(1000, delayMillis = index * 200)
            ),
            modifier = modifier
        ) {
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                rotate(rotation) {
                    drawCircle(
                        color = shape.color,
                        radius = shape.size,
                        center = Offset(offsetX, offsetY),
                        alpha = 0.6f
                    )
                }
            }
        }
    }
}

private fun createRandomParticle(screenWidth: Float, screenHeight: Float): Particle {
    val colors = listOf(
        Color(0xFF3B82F6), // Blue
        Color(0xFF10B981), // Green
        Color(0xFFEF4444), // Red
        Color(0xFFF59E0B), // Yellow
        Color(0xFF8B5CF6)  // Purple
    )
    
    return Particle(
        x = Random.nextFloat() * screenWidth,
        y = Random.nextFloat() * screenHeight,
        velocityX = (Random.nextFloat() - 0.5f) * 100f,
        velocityY = (Random.nextFloat() - 0.5f) * 100f,
        size = Random.nextFloat() * 8f + 2f,
        alpha = Random.nextFloat() * 0.7f + 0.3f,
        color = colors.random(),
        maxLife = Random.nextFloat() * 2f + 1f
    ).also { it.life = it.maxLife }
}

private fun updateParticles(
    particles: MutableList<Particle>,
    screenWidth: Float,
    screenHeight: Float
) {
    particles.forEach { particle ->
        // Update position
        particle.x += particle.velocityX * 0.016f // 60 FPS
        particle.y += particle.velocityY * 0.016f
        
        // Update life
        particle.life -= 0.016f
        particle.alpha = (particle.life / particle.maxLife).coerceIn(0f, 1f)
        
        // Reset particle if it's dead or out of bounds
        if (particle.life <= 0f || 
            particle.x < -50f || particle.x > screenWidth + 50f ||
            particle.y < -50f || particle.y > screenHeight + 50f) {
            
            // Reset particle
            particle.x = Random.nextFloat() * screenWidth
            particle.y = Random.nextFloat() * screenHeight
            particle.life = particle.maxLife
            particle.alpha = Random.nextFloat() * 0.7f + 0.3f
        }
    }
}

private fun DrawScope.drawParticles(particles: List<Particle>) {
    particles.forEach { particle ->
        drawCircle(
            color = particle.color,
            radius = particle.size,
            center = Offset(particle.x, particle.y),
            alpha = particle.alpha
        )
    }
}

private data class FloatingShape(
    val initialX: Float,
    val initialY: Float,
    val size: Float,
    val color: Color
)

private fun createFloatingShapes(count: Int): List<FloatingShape> {
    val colors = listOf(
        Color(0xFF3B82F6).copy(alpha = 0.3f),
        Color(0xFF10B981).copy(alpha = 0.3f),
        Color(0xFFEF4444).copy(alpha = 0.3f),
        Color(0xFFF59E0B).copy(alpha = 0.3f),
        Color(0xFF8B5CF6).copy(alpha = 0.3f)
    )
    
    return (0 until count).map {
        FloatingShape(
            initialX = Random.nextFloat() * 400f + 50f,
            initialY = Random.nextFloat() * 800f + 400f,
            size = Random.nextFloat() * 30f + 10f,
            color = colors.random()
        )
    }
}
