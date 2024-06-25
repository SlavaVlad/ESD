
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun Section(title: String, divider: Boolean = true, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.SpaceBetween) {
        Box(Modifier.fillMaxWidth()) {
            Text(title, fontWeight = FontWeight.Bold)
        }
        content()
        if (divider)
        Divider(Modifier.padding(vertical = 8.dp))
    }
}

@Composable
fun Entity (entity: Api.Track, onDownload: (entity: Api.Track) -> Unit, onPlay: (entity: Api.Track) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        IconButton(content = { Icon(Icons.Default.PlayArrow, contentDescription = null) }, modifier = Modifier.size(24.dp).weight(0.1f), onClick = {
            onPlay(entity)
        })
        Column(Modifier.weight(0.7f)) {
            Text(entity.title)
            Text(entity.creatives.mainArtists.firstOrNull()?.name ?: "Unknown", style = MaterialTheme.typography.caption)
        }
        Button(colors = ButtonDefaults.buttonColors(MaterialTheme.colors.primaryVariant), modifier = Modifier.weight(0.2f), onClick = { onDownload(entity) }) {
            Text("Download")
        }
    }
}