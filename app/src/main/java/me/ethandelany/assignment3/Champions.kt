package me.ethandelany.assignment3

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

data class Champion(val id: Int, val name: String, val price: Int, val icon: Int)

val champions = listOf(
    Champion(0, "Caitlyn", 550, 5),
    Champion(1, "Ahri", 995, 10),
    Champion(2, "Hecarim", 1550, 15),
    Champion(3, "Lee Sin", 455, 20),
    Champion(4, "Yuumi", 1280, 25),
    Champion(5, "Annie", 220, 30),
    Champion(6, "Jax", 970, 35),
)



class ChampionsViewModel(application: Application) : AndroidViewModel(application) {
    private val _championList = mutableSetOf<Champion>()
    var errorMessage: String by mutableStateOf("")
    val championList: Set<Champion>
        get() = _championList

    private val repo = ChampionRepository(application.applicationContext)

    fun getChampionList() {
        viewModelScope.launch {
            try {
                _championList.clear()

                repo.getPurchasedChamps()?.map { champions[it.toInt()] }?.let { _championList.addAll(it) }
            } catch (e: Exception) {
                errorMessage = e.message.toString()
            }
        }
    }

    fun addChamp(champId: Int) {
        viewModelScope.launch {
            try {
                val newChamp = champions[champId]
                _championList.add(newChamp)

                repo.addChamp(champId)
            } catch (e: Exception) {
                errorMessage = e.message.toString()
            }
        }
    }
}

class ChampionRepository(context: Context) {
    private val pref: SharedPreferences = context.getSharedPreferences("me.ethandelany.assignment3.champprefs", Context.MODE_PRIVATE)
    private val editor = pref.edit()

    fun getPurchasedChamps(): Set<String>? {
        return pref.getStringSet("purchasedChamps", HashSet<String>())?.toHashSet()
    }

    fun addChamp(id: Int): Boolean {
        val currents = pref.getStringSet("purchasedChamps", HashSet<String>())
        val new = currents?.toHashSet()
        new?.add(id.toString())
        return editor.putStringSet("purchasedChamps", new).commit()
    }
}