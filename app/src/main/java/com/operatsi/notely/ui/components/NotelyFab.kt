package com.operatsi.notely.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.operatsi.notely.R
import com.operatsi.notely.ui.NavDestination
import com.operatsi.notely.ui.theme.Dimens

@Composable
fun NotelyFab(
    currentDestination: NavDestination,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1.0f,
        animationSpec = tween(durationMillis = 150),
        label = "fab_scale"
    )

    val contentDescription = when (currentDestination) {
        NavDestination.NOTES -> "Create new note"
        NavDestination.TODO -> "Create new to-do task"
    }

    FloatingActionButton(
        onClick = onClick,
        modifier = modifier
            .size(Dimens.FabSize)
            .scale(scale),
        shape = CircleShape,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = Color.White,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = Dimens.FabElevation,
            pressedElevation = 6.dp,
            hoveredElevation = 4.dp
        ),
        interactionSource = interactionSource
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_fab_plus),
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}
