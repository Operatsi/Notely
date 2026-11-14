package com.operatsi.notely.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.operatsi.notely.R
import com.operatsi.notely.ui.theme.Dimens

@Composable
fun NotelyHeader(
    title: String,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    isGridView: Boolean,
    onToggleGridView: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
    isSelectionMode: Boolean = false,
    selectedCount: Int = 0,
    isAllSelected: Boolean = false,
    onClearSelection: () -> Unit = {},
    onSelectAll: () -> Unit = {},
    showBrandLogo: Boolean = true
) {
    val focusManager = LocalFocusManager.current

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            if (isSelectionMode) {
                // Multi-Selection Mode Top Bar (Transsion Clone)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = Dimens.SpaceNormal),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onClearSelection,
                            modifier = Modifier.size(Dimens.MinTouchTarget)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_close),
                                contentDescription = "Exit selection mode",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(Dimens.SpaceSmall))
                        Text(
                            text = if (selectedCount <= 1) "$selectedCount Item Selected" else "$selectedCount Items Selected",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Select All Toggle
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(Dimens.RadiusFull))
                            .clickable(onClick = onSelectAll)
                            .padding(horizontal = Dimens.SpaceSmall, vertical = Dimens.SpaceSmall),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isAllSelected) "Deselect All" else "Select All",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(if (isAllSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                                .border(
                                    width = 1.5.dp,
                                    color = if (isAllSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isAllSelected) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_nav_todo),
                                    contentDescription = "All Selected",
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                // Standard Notepad Header Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = Dimens.SpaceNormal),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: Title with Dropdown Chevron & Brand Logo
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (showBrandLogo) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_notely_logo),
                                contentDescription = "Notely Logo",
                                tint = Color.Unspecified,
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(Dimens.RadiusSmall))
                            )
                            Spacer(modifier = Modifier.width(Dimens.SpaceSmall))
                        }

                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                letterSpacing = (-0.5).sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.ic_chevron_down),
                            contentDescription = "Categories",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Right action icons
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // View Toggle (Grid / List)
                        IconButton(
                            onClick = onToggleGridView,
                            modifier = Modifier.size(Dimens.MinTouchTarget)
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = if (isGridView) R.drawable.ic_view_list else R.drawable.ic_view_grid
                                ),
                                contentDescription = if (isGridView) "Switch to list view" else "Switch to grid view",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // Settings / Profile Icon
                        IconButton(
                            onClick = onOpenSettings,
                            modifier = Modifier.size(Dimens.MinTouchTarget)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_settings),
                                contentDescription = "Settings and Preferences",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                // Search Bar Pill (Transsion / Google Keep Style)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.SpaceNormal, vertical = 6.dp)
                        .height(44.dp)
                        .clip(RoundedCornerShape(Dimens.RadiusFull))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        .padding(horizontal = Dimens.SpaceMedium),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(Dimens.SpaceSmall))

                    Box(modifier = Modifier.weight(1f)) {
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Search",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 15.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = onSearchQueryChanged,
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 15.sp
                            ),
                            singleLine = true,
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() })
                        )
                    }

                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { onSearchQueryChanged("") },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_close),
                                contentDescription = "Clear search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
