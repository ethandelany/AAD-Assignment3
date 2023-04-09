package me.ethandelany.assignment3

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

sealed class BottomNavItem(var title: String, var icon: Int, var screenRoute: String) {
    object Inventory : BottomNavItem("Inventory", R.drawable.ic_shoppingbag, "inventory")
    object Champions : BottomNavItem("Champions", R.drawable.ic_usergroup, "champions")
}


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun Inventory(championsVM: ChampionsViewModel) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row {
                        Text("Inventory")
                    }
                })
        },
        content = {
            if (championsVM.errorMessage.isEmpty()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.SpaceBetween) {

                    Box(
                        Modifier.fillMaxWidth().padding(0.dp, 16.dp)
                    ) {
                        Button(onClick = {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "Check out my purchased champions! ${championsVM.championList.joinToString { it.name }}")
                            }
                            startActivity(context, intent, null)
                        }) {
                            Text("Share your champions!")
                        }
                    }

                    if (championsVM.championList.isEmpty()) {
                        Text("You haven't purchased any champions yet!")
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxHeight()) {
                            items(championsVM.championList.toList(), key = { champ -> champ.id }) { champ ->
                                Column {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(0.dp, 0.dp, 16.dp, 0.dp)
                                        ) {
                                            Text(
                                                champ.name,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(16.dp))
                                    }
                                    Divider()
                                }
                            }
                        }
                    }
                    }
            } else {
                Text(championsVM.errorMessage)
            }
        }
    )
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun Champions(championsVM: ChampionsViewModel) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row {
                        Text("Champion Store")
                    }
                })
        },
        content = {
            Column(modifier = Modifier.padding(16.dp)) {
                LazyColumn(modifier = Modifier.fillMaxHeight()) {
                    items(champions, key = { champ -> champ.id }) { champ ->
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(0.dp, 0.dp, 16.dp, 0.dp)
                                ) {
                                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                                        Text(
                                            champ.name,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Button(onClick = {
                                            championsVM.addChamp(champ.id)
                                        }) {
                                            Text("Add")
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                            }
                            Divider()
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun NavigationGraph(navController: NavHostController, championsVM: ChampionsViewModel) {
    NavHost(navController = navController, startDestination = BottomNavItem.Inventory.screenRoute) {
        composable(BottomNavItem.Inventory.screenRoute) {
            Inventory(championsVM)
        }

        composable(BottomNavItem.Champions.screenRoute) {
            Champions(championsVM)
        }
    }
}

@Composable
fun MyBottomNavigation(navController: NavController) {
    val items = listOf(
        BottomNavItem.Inventory,
        BottomNavItem.Champions,
    )

    BottomNavigation(
        backgroundColor = Color.DarkGray,
        contentColor = Color.Black
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(imageVector = ImageVector.vectorResource(id = item.icon), contentDescription = item.title) },
                label = { Text(text = item.title, fontSize = 9.sp)},
                selectedContentColor = Color.Black,
                unselectedContentColor = Color.Black.copy(0.4f),
                alwaysShowLabel = true,
                selected = currentRoute == item.screenRoute,
                onClick = {
                    navController.navigate(item.screenRoute) {
                        navController.graph.startDestinationRoute?.let { screenRoute ->
                            popUpTo(screenRoute) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreenView(championsVM: ChampionsViewModel) {
    val navController = rememberNavController()

    Scaffold(bottomBar = { MyBottomNavigation(navController = navController) }) {
        NavigationGraph(navController = navController, championsVM)
    }
}