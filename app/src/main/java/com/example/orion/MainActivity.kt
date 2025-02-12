package com.example.orion

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.example.orion.ui.HomeScreen
import com.example.orion.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mimeType = "application/octet-stream"

    private val viewModel: AppViewModel by viewModels()

    private val exportDatabaseFileLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument(mimeType)) { outputUri ->
        if (outputUri != null) {
            val databasePath = Uri.fromFile(getDatabasePath(AppModule.DATABASE_FILENAME))
            contentResolver.openInputStream(databasePath)?.use { inputStream ->
                contentResolver.openFileDescriptor(outputUri, "w")?.use { fileDescriptor ->
                    FileOutputStream(fileDescriptor.fileDescriptor).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
            }
        }
    }

    private fun exportFileName(): String {
        val time: Date = Calendar.getInstance().time
        val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate: String = df.format(time)
        return "${AppModule.DATABASE_NAME}.${formattedDate}.db"
    }

    private val openFileLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { result ->
            lifecycleScope.launch {
                if (result != null) {
                    viewModel.extractImport(result)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.Transparent.toArgb()),
//            navigationBarStyle = SystemBarStyle.light(
//                Color.Transparent.toArgb(), Color.Transparent.toArgb()
//            )
        )

        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.apply {
            //hide(WindowInsetsCompat.Type.statusBars())
            //hide(WindowInsetsCompat.Type.navigationBars())
            //systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }


        setContent {
            AppTheme(darkTheme = true) {
                HomeScreen(
                    viewModel = viewModel,
                    exportData = {
                        exportDatabaseFileLauncher.launch(exportFileName())
                    },
                    importData = {
                        openFileLauncher.launch(
                            arrayOf(
                                "application/x-sqlite3",
                                "application/vnd.sqlite3",
                                "application/octet-stream"
                            )
                        )
                    }
                )
            }
        }
    }
}
