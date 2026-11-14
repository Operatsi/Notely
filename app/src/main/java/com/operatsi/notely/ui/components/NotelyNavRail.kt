package com.operatsi.notely.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.operatsi.notely.R
import com.operatsi.notely.ui.NavDestination
import com.operatsi.notely.ui.theme.Dimens

@Composable
fun NotelyNavRail(
    currentDestination: NavDestination,
    onDestinationSelected: (NavDestination) -> Unit,
    onOpenSettings: () -> Unit,
    onFabClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .width(Dimens.NavRailWidth)
            .fillMaxHeight(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(vertical = Dimens.SpaceNormal),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Section: Logo & FAB
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_notely_logo),
                    contentDescription = "Notely Logo",
                    modifier = Modifier.size(36.dp)
                )

                Spacer(modifier = Modifier.height(Dimens.SpaceLarge))

                NotelyFab(
                    currentDestination = currentDestination,
                    onClick = onFabClicked
                )

                Spacer(modifier = Modifier.height(Dimens.SpaceExtraLarge))

                // Destinations
                NotelyNavRailItem(
                    label = "Notes",
                    iconRes = R.drawable.ic_nav_notes,
                    isSelected = currentDestination == NavDestination.NOTES,
                    onClick = { onDestinationSelected(NavDestination.NOTES) }
                )

                Spacer(modifier = Modifier.height(Dimens.SpaceNormal))

                NotelyNavRailItem(
                    label = "To-do",
                    iconRes = R.drawable.ic_nav_todo,
                    isSelected = currentDestination == NavDestination.TODO,
                    onClick = { onDestinationSelected(NavDestination.TODO) }
                )
            }

            // Bottom Section: Settings Icon
            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier.size(Dimens.MinTouchTarget)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_settings),
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun NotelyNavRailItem(
    label: String,
    iconRes: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val activeColor = MaterialTheme.colorScheme.primary
    val inactiveColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
    val pillBackgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) activeColor else inactiveColor,
        animationSpec = tween(durationMillis = 200),
        label = "nav_rail_color"
    )

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(Dimens.RadiusMedium))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = Dimens.SpaceSmall, vertical = Dimens.SpaceSmall),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(Dimens.RadiusFull))
                .background(if (isSelected) pillBackgroundColor else Color.Transparent)
                .padding(horizontal = 14.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            color = contentColor,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}
