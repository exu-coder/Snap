package com.exu.mediadownloader

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Glass = Color.White.copy(alpha = 0.72f)
private val GlassDark = Color(0xFF15171C)

@Composable
fun ExuApp(vm: MainViewModel) {
    var tab by remember { mutableIntStateOf(0) }
    val shared by vm.sharedUrl.collectAsState()
    val downloads by vm.downloads.collectAsState()

    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    ) {
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surfaceVariant,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
        ) {
            when (tab) {
                0 -> HomeScreen(vm, downloads)
                1 -> DownloadsScreen(downloads)
                2 -> BrowserPlaceholder()
                3 -> LibraryScreen()
                else -> SettingsScreen()
            }
            Dock(tab) { tab = it }
            AnimatedVisibility(
                visible = shared != null,
                enter = fadeIn() + expandVertically(animationSpec = spring(stiffness = Spring.StiffnessMedium)),
                exit = fadeOut() + shrinkVertically(),
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 105.dp, start = 16.dp, end = 16.dp)
            ) {
                shared?.let { SharePanel(it, vm) }
            }
        }
    }
}

@Composable
private fun GlassCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = Glass),
        elevation = CardDefaults.cardElevation(8.dp),
        content = content
    )
}

@Composable
fun HomeScreen(vm: MainViewModel, downloads: List<DownloadUi>) {
    var url by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(22.dp).padding(bottom = 105.dp)) {
        Spacer(Modifier.height(24.dp))
        Text("EXU", fontSize = 42.sp, fontWeight = FontWeight.ExtraBold)
        Text("MEDIA DOWNLOADER", fontSize = 15.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
        Spacer(Modifier.height(24.dp))
        GlassCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(22.dp)) {
                Text("Paste a media URL", fontWeight = FontWeight.ExtraBold, fontSize = 21.sp)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("https://…") },
                    leadingIcon = { Icon(Icons.Default.Link, null) },
                    shape = RoundedCornerShape(20.dp)
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { if (url.startsWith("http")) vm.startDirectDownload(url) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(18.dp)
                ) { Text("ANALYZE & DOWNLOAD", fontWeight = FontWeight.ExtraBold) }
            }
        }
        Spacer(Modifier.height(20.dp))
        Text("RECENT", fontWeight = FontWeight.ExtraBold, letterSpacing = 1.5.sp)
        Spacer(Modifier.height(8.dp))
        if (downloads.isEmpty()) {
            GlassCard(Modifier.fillMaxWidth()) {
                Text("No downloads yet.", Modifier.padding(22.dp), fontWeight = FontWeight.SemiBold)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(downloads.take(5)) { DownloadCard(it) }
            }
        }
    }
}

@Composable
fun SharePanel(url: String, vm: MainViewModel) {
    GlassCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(46.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Download, null, tint = MaterialTheme.colorScheme.onPrimary)
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("Shared link", fontWeight = FontWeight.ExtraBold, fontSize = 19.sp)
                    Text(url, maxLines = 1, style = MaterialTheme.typography.bodySmall)
                }
                IconButton(onClick = vm::dismissShare) { Icon(Icons.Default.Close, null) }
            }
            Spacer(Modifier.height(14.dp))
            Text("Direct media links can be downloaded in the MVP. Provider-specific resolution should be implemented only through authorized/allowed access.", fontSize = 13.sp)
            Spacer(Modifier.height(14.dp))
            Button(
                onClick = { vm.startDirectDownload(url); vm.dismissShare() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) { Text("DOWNLOAD", fontWeight = FontWeight.ExtraBold) }
        }
    }
}

@Composable
fun DownloadsScreen(downloads: List<DownloadUi>) {
    Column(Modifier.fillMaxSize().padding(22.dp).padding(bottom = 105.dp)) {
        Spacer(Modifier.height(24.dp))
        Text("DOWNLOADS", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(16.dp))
        if (downloads.isEmpty()) Text("Your download queue is empty.")
        else LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(downloads) { DownloadCard(it) }
        }
    }
}

@Composable
fun DownloadCard(item: DownloadUi) {
    GlassCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp)) {
            Text(item.fileName, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(5.dp))
            Text(item.status, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { item.progress / 100f },
                modifier = Modifier.fillMaxWidth().height(7.dp).clip(RoundedCornerShape(10.dp))
            )
            Spacer(Modifier.height(7.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${item.progress}%")
                Text(item.speed)
            }
        }
    }
}

@Composable fun BrowserPlaceholder() = CenterScreen("BROWSER", "WebView browser module is scaffolded for the next build phase.")
@Composable fun LibraryScreen() = CenterScreen("LIBRARY", "MediaStore + Media3 library/player module is ready for implementation.")
@Composable fun SettingsScreen() = CenterScreen("SETTINGS", "Theme, download directory, concurrency, network and browser preferences.")

@Composable
fun CenterScreen(title: String, subtitle: String) {
    Column(Modifier.fillMaxSize().padding(22.dp), verticalArrangement = Arrangement.Center) {
        Text(title, fontSize = 34.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(10.dp))
        Text(subtitle)
    }
}

@Composable
fun Dock(selected: Int, onSelected: (Int) -> Unit) {
    val icons = listOf(Icons.Default.Home, Icons.Default.Download, Icons.Default.Language, Icons.Default.VideoLibrary, Icons.Default.Settings)
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 18.dp)
            .clip(RoundedCornerShape(32.dp)).background(GlassDark.copy(alpha = 0.92f))
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        icons.forEachIndexed { index, icon ->
            val active = selected == index
            Box(
                Modifier.size(if (active) 58.dp else 50.dp)
                    .clip(CircleShape)
                    .background(if (active) Color.White.copy(alpha = .18f) else Color.Transparent)
                    .clickable { onSelected(index) },
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = Color.White)
            }
        }
    }
}
