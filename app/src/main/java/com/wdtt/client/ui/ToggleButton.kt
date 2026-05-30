package com.wdtt.client.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.outlined.VpnLock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

/**
 * Красивая анимированная кнопка включения/выключения туннеля
 * с пульсирующими эффектами, свечением и тактильной обратной связью
 */
@Composable
fun AnimatedToggleButton(
    isActive: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val colors = MaterialTheme.colorScheme
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    
    // Основной цвет кнопки
    val buttonColor = animateColorAsState(
        targetValue = if (isActive) colors.primary else colors.surfaceVariant,
        label = "Button Color"
    )
    
    // Цвет иконки
    val iconColor = animateColorAsState(
        targetValue = if (isActive) colors.onPrimary else colors.onSurfaceVariant,
        label = "Icon Color"
    )
    
    // Анимация пульсации (только когда активна)
    val pulseScale = remember { Animatable(1f) }
    val pulseAlpha = remember { Animatable(0.3f) }
    
    LaunchedEffect(isActive) {
        if (isActive) {
            pulseScale.animateTo(
                targetValue = 1.4f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1500, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        } else {
            pulseScale.snapTo(1f)
        }
    }
    
    // Анимация свечения
    val glowAlpha = remember { Animatable(0f) }
    LaunchedEffect(isActive) {
        if (isActive) {
            glowAlpha.animateTo(
                targetValue = 0.6f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        } else {
            glowAlpha.snapTo(0f)
        }
    }
    
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Пульсирующий фон
        Box(
            modifier = Modifier
                .size(200.dp)
                .drawBehind {
                    // Внешнее свечение (пульсирующее)
                    if (isActive) {
                        drawCircle(
                            color = colors.primary.copy(alpha = glowAlpha.value * 0.4f),
                            radius = 110.dp.toPx(),
                            center = Offset(size.width / 2, size.height / 2)
                        )
                    }
                    
                    // Слой пульсации
                    drawCircle(
                        color = colors.primary.copy(alpha = 0.1f * (2 - pulseScale.value)),
                        radius = 100.dp.toPx() * pulseScale.value,
                        center = Offset(size.width / 2, size.height / 2)
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            // Основная кнопка с градиентом
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(
                        brush = if (isActive) {
                            Brush.radialGradient(
                                colors = listOf(
                                    colors.primary.copy(alpha = 0.9f),
                                    colors.primary
                                ),
                                center = Offset(80.dp.toPx(), 80.dp.toPx()),
                                radius = 80.dp.toPx()
                            )
                        } else {
                            Brush.radialGradient(
                                colors = listOf(
                                    colors.surfaceVariant.copy(alpha = 0.8f),
                                    colors.surfaceVariant
                                ),
                                center = Offset(80.dp.toPx(), 80.dp.toPx()),
                                radius = 80.dp.toPx()
                            )
                        }
                    )
                    .drawBehind {
                        // Внутреннее свечение для глубины
                        drawCircle(
                            color = Color.white.copy(alpha = if (isActive) 0.15f else 0.05f),
                            radius = 78.dp.toPx(),
                            center = Offset(size.width / 2, size.height / 2),
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }
                    .clickable(
                        enabled = enabled,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        onToggle()
                    }
                    .scale(if (isActive) 1f else 0.95f),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isActive) Icons.Filled.VpnKey else Icons.Outlined.VpnLock,
                    contentDescription = if (isActive) "Туннель активен" else "Туннель отключен",
                    tint = iconColor.value,
                    modifier = Modifier.size(80.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Текст статуса
        Text(
            text = if (isActive) "Туннель активен" else "Туннель отключен",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isActive) colors.primary else colors.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Подтекст
        Text(
            text = "Нажми на кнопку для ${if (isActive) "отключения" else "включения"}",
            fontSize = 13.sp,
            color = colors.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}

/**
 * Компактная версия для других экранов (например в навигации)
 */
@Composable
fun CompactToggleButton(
    isActive: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val buttonColor = animateColorAsState(
        targetValue = if (isActive) colors.primary else colors.surfaceVariant,
        label = "Compact Button Color"
    )
    
    val pulseScale = remember { Animatable(1f) }
    
    LaunchedEffect(isActive) {
        if (isActive) {
            pulseScale.animateTo(
                targetValue = 1.15f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        } else {
            pulseScale.snapTo(1f)
        }
    }
    
    Box(
        modifier = modifier
            .size(80.dp)
            .drawBehind {
                if (isActive) {
                    drawCircle(
                        color = colors.primary.copy(alpha = 0.15f),
                        radius = 45.dp.toPx() * pulseScale.value
                    )
                }
            }
            .clip(CircleShape)
            .background(buttonColor.value)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onToggle()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isActive) Icons.Filled.VpnKey else Icons.Outlined.VpnLock,
            contentDescription = "Toggle",
            tint = if (isActive) colors.onPrimary else colors.onSurfaceVariant,
            modifier = Modifier.size(40.dp)
        )
    }
}
