import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.*
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import kotlinx.coroutines.*
import java.awt.Cursor

class FileDropTarget(
    private val onFileDrop: (File) -> Unit
) : DropTarget() {
    @Synchronized
    override fun drop(event: DropTargetDropEvent) {
        event.acceptDrop(DnDConstants.ACTION_COPY)
        val droppedFiles = event.transferable.getTransferData(DataFlavor.javaFileListFlavor) as List<*>
        droppedFiles.firstOrNull()?.let {
            onFileDrop(it as File)
        }
        event.dropComplete(true)
    }
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "音视频转换器",
        resizable = false,
        state = WindowState(width = 800.dp, height = 700.dp)
    ) {
        val window = (window as ComposeWindow)

        MaterialTheme(
            colors = lightColors(
                primary = Color(0xFF1A1C1E),
                surface = Color.White,
                background = Color(0xFFF8F9FA)
            )
        ) {
            MediaConverterApp(window)
        }
    }
}

@Composable
fun MediaConverterApp(window: ComposeWindow) {
    var selectedFile by remember { mutableStateOf<File?>(null) }
    var outputDirectory by remember { mutableStateOf<File?>(null) }
    var selectedFormat by remember { mutableStateOf("mp4") }
    var quality by remember { mutableStateOf(0.8f) }
    var progress by remember { mutableStateOf(0f) }
    var isConverting by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val formats = listOf("mp4", "avi", "mkv", "mov", "mp3", "wav")

    // 设置文件拖放目标
    DisposableEffect(Unit) {
        val dropTarget = FileDropTarget { file ->
            selectedFile = file
        }
        window.contentPane.dropTarget = dropTarget

        onDispose {
            window.contentPane.dropTarget = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 标题
        Text(
            text = "音视频转换器",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.primary
        )

        // 拖放区域
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(
                    width = 2.dp,
                    color = Color(0xFFE0E0E0),
                    shape = RoundedCornerShape(8.dp)
                )
                .background(Color.White)
                .pointerHoverIcon(PointerIcon(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)))
                .clickable {
                    val fileChooser = JFileChooser().apply {
                        fileFilter = FileNameExtensionFilter(
                            "媒体文件", *formats.toTypedArray()
                        )
                    }
                    if (fileChooser.showOpenDialog(window) == JFileChooser.APPROVE_OPTION) {
                        selectedFile = fileChooser.selectedFile
                        // 默认将输出目录设置为输入文件所在目录
                        if (outputDirectory == null) {
                            outputDirectory = fileChooser.selectedFile.parentFile
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Upload",
                    tint = Color(0xFF9E9E9E),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    selectedFile?.name ?: "拖放文件到这里或点击上传",
                    color = Color(0xFF757575),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(16.dp))
                OutlinedButton(
                    onClick = {
                        val fileChooser = JFileChooser().apply {
                            fileFilter = FileNameExtensionFilter(
                                "媒体文件", *formats.toTypedArray()
                            )
                        }
                        if (fileChooser.showOpenDialog(window) == JFileChooser.APPROVE_OPTION) {
                            selectedFile = fileChooser.selectedFile
                            if (outputDirectory == null) {
                                outputDirectory = fileChooser.selectedFile.parentFile
                            }
                        }
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colors.primary
                    )
                ) {
                    Text("选择文件")
                }
            }
        }

        // 设置区域
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 输出格式
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "输出格式",
                    color = Color(0xFF424242),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                var expanded by remember { mutableStateOf(false) }
                Box {
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(selectedFormat.uppercase())
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        formats.forEach { format ->
                            DropdownMenuItem(onClick = {
                                selectedFormat = format
                                expanded = false
                            }) {
                                Text(format.uppercase())
                            }
                        }
                    }
                }
            }

            // 质量设置
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "质量设置",
                    color = Color(0xFF424242),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Slider(
                    value = quality,
                    onValueChange = { quality = it },
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colors.primary,
                        activeTrackColor = MaterialTheme.colors.primary
                    )
                )
            }
        }

        // 输出目录显示
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                "输出目录",
                color = Color(0xFF424242),
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    outputDirectory?.absolutePath ?: "未选择输出目录",
                    color = Color(0xFF757575),
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                OutlinedButton(
                    onClick = {
                        val fileChooser = JFileChooser().apply {
                            fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                            dialogTitle = "选择输出目录"
                        }
                        if (fileChooser.showDialog(window, "选择") == JFileChooser.APPROVE_OPTION) {
                            outputDirectory = fileChooser.selectedFile
                        }
                    }
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = "Select Directory",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("选择目录")
                }
            }
        }

        // 进度条
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "转换进度",
                    color = Color(0xFF424242),
                    fontSize = 14.sp
                )
                Text(
                    "${(progress * 100).toInt()}%",
                    color = Color(0xFF424242),
                    fontSize = 14.sp
                )
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = MaterialTheme.colors.primary,
                backgroundColor = Color(0xFFE0E0E0)
            )
        }

        // 开始转换按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 开始转换按钮
            Button(
                onClick = {
                    if (selectedFile == null || outputDirectory == null) return@Button

                    isConverting = true
                    progress = 0f

                    scope.launch {
                        try {
                            val outputFileName = "${selectedFile!!.nameWithoutExtension}_converted.$selectedFormat"
                            val outputFile = File(outputDirectory!!, outputFileName)

                            convertMedia(
                                inputFile = selectedFile!!,
                                outputFile = outputFile,
                                quality = quality,
                                onProgress = { progress = it }
                            )
                        } catch (e: Exception) {
                            // 处理错误
                        } finally {
                            isConverting = false
                        }
                    }
                },
                enabled = !isConverting && selectedFile != null && outputDirectory != null,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.primary,
                    contentColor = Color.White,
                    disabledBackgroundColor = Color(0xFFE0E0E0),
                    disabledContentColor = Color(0xFF9E9E9E)
                ),
                modifier = Modifier
                    .width(160.dp)
                    .height(48.dp)
                    .padding(end = 16.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Convert",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (isConverting) "转换中..." else "开始转换",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // 取消转换按钮
            if (isConverting) {
                OutlinedButton(
                    onClick = {
                        // 这里添加取消转换的逻辑
                        scope.coroutineContext.cancelChildren()
                        isConverting = false
                        progress = 0f
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colors.primary
                    ),
                    modifier = Modifier
                        .width(160.dp)
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colors.primary)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "取消转换",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // 如果正在转换，显示预计剩余时间
        if (isConverting) {
            Text(
                "预计剩余时间：${((1 - progress) * 100).toInt() / 2} 秒",
                color = Color(0xFF757575),
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

    }

}

suspend fun convertMedia(
    inputFile: File,
    outputFile: File,
    quality: Float,
    onProgress: (Float) -> Unit
) = withContext(Dispatchers.IO) {
    // 这里实现实际的媒体转换逻辑
    // 为了演示，这里使用模拟进度
    for (i in 1..100) {
        delay(50)
        onProgress(i / 100f)
    }
}