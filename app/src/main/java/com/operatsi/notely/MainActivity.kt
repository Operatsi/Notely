package com.operatsi.notely

import android.Manifest
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.operatsi.notely.ui.NavDestination
import com.operatsi.notely.ui.NotelyApp
import com.operatsi.notely.ui.NotelyViewModel
import com.operatsi.notely.ui.theme.ThemeSetting

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Lock portrait for standard phone form factor, allowing tablets and Chromebooks to freely rotate
        val smallestWidthDp = resources.configuration.smallestScreenWidthDp
        if (smallestWidthDp < 600) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        enableEdgeToEdge()

        // Request POST_NOTIFICATIONS on Android 13+ (API 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            val context = LocalContext.current
            val vm: NotelyViewModel = viewModel { NotelyViewModel(context.applicationContext) }
            NotelyApp(viewModel = vm)
        }
    }
}

@Preview(name = "10-inch Tablet Landscape Notes", showBackground = true, device = "spec:width=2560px,height=1440px,dpi=280")
@Composable
fun Tablet10inNotesLandscapePreview() {
    val context = LocalContext.current
    val vm = remember { NotelyViewModel(context) }
    NotelyApp(viewModel = vm)
}

@Preview(name = "10-inch Tablet Landscape To-do", showBackground = true, device = "spec:width=2560px,height=1440px,dpi=280")
@Composable
fun Tablet10inTodoLandscapePreview() {
    val context = LocalContext.current
    val vm = remember {
        NotelyViewModel(context).apply {
            setDestination(NavDestination.TODO)
        }
    }
    NotelyApp(viewModel = vm)
}

@Preview(name = "10-inch Tablet Landscape Editor", showBackground = true, device = "spec:width=2560px,height=1440px,dpi=280")
@Composable
fun Tablet10inEditorLandscapePreview() {
    val context = LocalContext.current
    val vm = remember {
        NotelyViewModel(context).apply {
            val note = notes.value.firstOrNull()
            if (note != null) openEditNote(note) else onFabClicked()
        }
    }
    NotelyApp(viewModel = vm)
}

@Preview(name = "10-inch Tablet Landscape Settings", showBackground = true, device = "spec:width=2560px,height=1440px,dpi=280")
@Composable
fun Tablet10inSettingsLandscapePreview() {
    val context = LocalContext.current
    val vm = remember {
        NotelyViewModel(context).apply {
            openSettings()
        }
    }
    NotelyApp(viewModel = vm)
}

@Preview(name = "10-inch Tablet Landscape Dark Mode", showBackground = true, device = "spec:width=2560px,height=1440px,dpi=280", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun Tablet10inDarkLandscapePreview() {
    val context = LocalContext.current
    val vm = remember {
        NotelyViewModel(context).apply {
            setThemeSetting(ThemeSetting.DARK)
        }
    }
    NotelyApp(viewModel = vm)
}

@Preview(name = "10-inch Tablet Portrait Notes", showBackground = true, device = "spec:width=1440px,height=2560px,dpi=280")
@Composable
fun Tablet10inNotesPortraitPreview() {
    val context = LocalContext.current
    val vm = remember { NotelyViewModel(context) }
    NotelyApp(viewModel = vm)
}

@Preview(name = "10-inch Tablet Portrait To-do", showBackground = true, device = "spec:width=1440px,height=2560px,dpi=280")
@Composable
fun Tablet10inTodoPortraitPreview() {
    val context = LocalContext.current
    val vm = remember {
        NotelyViewModel(context).apply {
            setDestination(NavDestination.TODO)
        }
    }
    NotelyApp(viewModel = vm)
}

@Preview(name = "10-inch Tablet Portrait Editor", showBackground = true, device = "spec:width=1440px,height=2560px,dpi=280")
@Composable
fun Tablet10inEditorPortraitPreview() {
    val context = LocalContext.current
    val vm = remember {
        NotelyViewModel(context).apply {
            val note = notes.value.firstOrNull()
            if (note != null) openEditNote(note) else onFabClicked()
        }
    }
    NotelyApp(viewModel = vm)
}
