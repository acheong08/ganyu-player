package dev.duti.ganyu.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class Screens {
    SONGS,
    ARTISTS,
    SUBSCRIPTIONS,
    SEARCH,
    DOWNLOADING
}

@Composable
fun NavDrawer(onNav: (Screens) -> Unit) {
    return ModalDrawerSheet {
        Text("Player", modifier = Modifier.padding(16.dp))
        HorizontalDivider()
        LazyColumn {
            items(count = Screens.entries.size) { index ->
                NavigationDrawerItem(
                    label = { Text(text = Screens.entries[index].toString()) },
                    selected = false,
                    onClick = { onNav(Screens.entries[index]) }
                )

            }
        }
    }
}
