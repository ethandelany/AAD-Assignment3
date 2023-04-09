package me.ethandelany.assignment3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import me.ethandelany.assignment3.ui.theme.Assignment3Theme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val championsVM = ChampionsViewModel(application)
        championsVM.getChampionList()

        super.onCreate(savedInstanceState)
        setContent {
            Assignment3Theme {
                MainScreenView(championsVM)
            }
        }
    }
}
