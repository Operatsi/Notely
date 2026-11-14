package com.operatsi.notely.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.operatsi.notely.R
import com.operatsi.notely.ui.theme.Dimens
import com.operatsi.notely.ui.theme.ThemeSetting

@Composable
fun SettingsScreen(
    currentTheme: ThemeSetting,
    onThemeChanged: (ThemeSetting) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var noteWatermark by remember { mutableStateOf(true) }
    var showPrivacyPolicyDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }

    BackHandler {
        onClose()
    }

    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .height(56.dp)
                        .padding(horizontal = Dimens.SpaceSmall),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onClose) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = "Navigate up",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(Dimens.SpaceSmall))
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + Dimens.SpaceLarge)
        ) {
            // Group 1: General & Appearance
            SettingsCategoryHeader(title = "GENERAL")

            SettingsSwitchItem(
                title = "Note Watermark",
                subtitle = "Display subtle OperatSI watermark on exported notes",
                checked = noteWatermark,
                onCheckedChange = { noteWatermark = it }
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                modifier = Modifier.padding(horizontal = Dimens.SpaceLarge, vertical = Dimens.SpaceSmall)
            )

            SettingsCategoryHeader(title = "APPEARANCE")

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.SpaceLarge, vertical = Dimens.SpaceSmall)
            ) {
                Text(
                    text = "Theme Mode",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(Dimens.SpaceSmall))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSmall)
                ) {
                    ThemeSetting.entries.forEach { setting ->
                        val isSelected = currentTheme == setting
                        val label = when (setting) {
                            ThemeSetting.SYSTEM -> "System"
                            ThemeSetting.LIGHT -> "Light"
                            ThemeSetting.DARK -> "Dark"
                        }
                        FilterChip(
                            selected = isSelected,
                            onClick = { onThemeChanged(setting) },
                            label = { Text(label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            shape = RoundedCornerShape(Dimens.RadiusFull)
                        )
                    }
                }
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                modifier = Modifier.padding(horizontal = Dimens.SpaceLarge, vertical = Dimens.SpaceSmall)
            )

            // Group 2: Default Reminder Alert Times
            SettingsCategoryHeader(title = "REMINDER ALERT TIMES")

            SettingsClickableItem(
                title = "Morning Reminder",
                statusText = "09:00 AM",
                onClick = {}
            )

            SettingsClickableItem(
                title = "Afternoon Reminder",
                statusText = "01:00 PM",
                onClick = {}
            )

            SettingsClickableItem(
                title = "Evening Reminder",
                statusText = "06:00 PM",
                onClick = {}
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                modifier = Modifier.padding(horizontal = Dimens.SpaceLarge, vertical = Dimens.SpaceSmall)
            )

            // Group 3: Privacy & Security
            SettingsCategoryHeader(title = "PRIVACY & SECURITY")

            SettingsClickableItem(
                title = "Privacy Policy",
                statusText = "Read Full Policy",
                onClick = { showPrivacyPolicyDialog = true }
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                modifier = Modifier.padding(horizontal = Dimens.SpaceLarge, vertical = Dimens.SpaceSmall)
            )

            // Group 4: About Notely
            SettingsCategoryHeader(title = "ABOUT")

            SettingsClickableItem(
                title = "About Notely",
                statusText = "v1.0.0",
                onClick = { showAboutDialog = true }
            )

            SettingsClickableItem(
                title = "Check for Updates",
                statusText = "Latest",
                onClick = { showUpdateDialog = true }
            )
        }
    }

    // Full Corporate Privacy Policy Dialog
    if (showPrivacyPolicyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyPolicyDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_settings),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(Dimens.SpaceSmall))
                    Text(
                        text = "Privacy Policy",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(420.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Notely by OperatSI — Official Privacy Policy",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text = "Effective Date: November 14, 2026",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    Spacer(modifier = Modifier.height(Dimens.SpaceMedium))

                    PrivacySection(
                        number = "1",
                        title = "Commitment to Confidentiality",
                        content = "OperatSI is dedicated to protecting your privacy. Notely is engineered from the ground up as a private, local-first productivity tool. Your notes, ideas, personal tasks, and schedules belong strictly to you and remain entirely under your control."
                    )

                    PrivacySection(
                        number = "2",
                        title = "100% On-Device Local Storage",
                        content = "All notes, to-dos, reminders, and preference settings are stored locally on your device storage. Notely does not transmit your personal note content, task descriptions, or metadata to external servers or cloud repositories. Your data never leaves your hardware without your explicit action."
                    )

                    PrivacySection(
                        number = "3",
                        title = "Device Permissions & System Alarms",
                        content = "Notely requests only the bare essential Android system permissions:\n• Exact Alarms: Used solely to trigger reminder alerts at the precise time specified by you.\n• Post Notifications: Used exclusively to display reminder heads-up banners on your device.\n• Boot Completed: Used to re-register your active alarms after device reboot.\nNo permission is ever leveraged for telemetry, profiling, or background tracking."
                    )

                    PrivacySection(
                        number = "4",
                        title = "Zero Data Sharing & Advertising",
                        content = "OperatSI does not sell, lease, monetize, or disclose your personal information. Notely contains zero third-party advertising SDKs, zero behavioral analytics trackers, and zero cross-app profiling frameworks."
                    )

                    PrivacySection(
                        number = "5",
                        title = "Data Ownership & Permanent Deletion",
                        content = "You retain 100% ownership of everything you write or create in Notely. When you delete a note or to-do task, it is immediately and permanently purged from your device storage."
                    )

                    PrivacySection(
                        number = "6",
                        title = "Security Architecture",
                        content = "Notely operates within Android's sandboxed environment, isolated from all other apps. Stored content benefits from modern Android file-based hardware encryption."
                    )

                    PrivacySection(
                        number = "7",
                        title = "Discussion & Contact",
                        content = "For questions, feedback, or compliance verifications regarding this policy, join the discussion on GitHub:\nhttps://github.com/Operatsi/Notely/discussions\nOperatSI"
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showPrivacyPolicyDialog = false },
                    shape = RoundedCornerShape(Dimens.RadiusFull),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Close", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // About Notely Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_notely_logo),
                        contentDescription = null,
                        tint = androidx.compose.ui.graphics.Color.Unspecified,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(Dimens.SpaceSmall))
                    Text("About Notely", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text(
                        "Notely — Corporate Note & To-do Application",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Version 1.0.0 (Build 2026.11)",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(Dimens.SpaceMedium))
                    Text(
                        "Developed by OperatSI with a commitment to minimalism, performance, and complete data privacy. Engineered with Jetpack Compose and Material 3 design principles.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Check for Updates Dialog
    if (showUpdateDialog) {
        AlertDialog(
            onDismissRequest = { showUpdateDialog = false },
            title = { Text("Software Update", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "You are using the latest version of Notely (v1.0.0). No updates are available at this time.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(onClick = { showUpdateDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
private fun PrivacySection(
    number: String,
    title: String,
    content: String
) {
    Column(modifier = Modifier.padding(bottom = Dimens.SpaceMedium)) {
        Text(
            text = "$number. $title",
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 12.sp,
                lineHeight = 18.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SettingsCategoryHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            letterSpacing = 1.sp
        ),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = Dimens.SpaceLarge, vertical = Dimens.SpaceSmall)
    )
}

@Composable
private fun SettingsClickableItem(
    title: String,
    statusText: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = Dimens.SpaceLarge, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 16.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (statusText != null) {
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_right),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun SettingsSwitchItem(
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = Dimens.SpaceLarge, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.width(Dimens.SpaceMedium))

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
