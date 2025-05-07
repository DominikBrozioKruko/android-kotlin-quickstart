package com.example.android_kotlin_quickstart.viewmodel

import android.content.Context
import android.util.Log
import android.util.LogPrinter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_kotlin_quickstart.DBManager
import com.example.android_kotlin_quickstart.data.model.Hotel
import com.example.android_kotlin_quickstart.data.model.HotelDocumentModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.ref.WeakReference

class HomeViewModel(private val context: WeakReference<Context>) : ViewModel() {
    var uiState = "Loading hotels..."
    val liveQueryState: MutableLiveData<MutableList<Hotel>?> by lazy { MutableLiveData<MutableList<Hotel>?>(null) }
    init {
        loadData()
    }

    private fun loadData() {
        runIt()
    }

    fun onDeleteHotel(hotel: Hotel) {
        context.get()?.let {
            val dbManager = DBManager.getInstance(it)
            dbManager.delete(hotel)
        }
    }

    private fun runIt(): MutableLiveData<MutableList<Hotel>?> {
        context.get()?.let {
            viewModelScope.launch(Dispatchers.IO) {
                val mgr = DBManager.getInstance(it)
                mgr.createDb("travel-sample")
                mgr.createCollection("hotel")
                mgr.replicate(it)
                mgr.queryDocs()
                    ?.onEach { change ->
                        val hotelList: MutableList<Hotel> = mutableListOf()
                        change.results?.let { rs ->
                            rs.forEach {
                                val jsonString = it.toJSON()
                                val jsonObject = JSONObject(jsonString)
                                var hotelDoc = HotelDocumentModel.fromJson(jsonObject)
                                hotelList.add(hotelDoc.hotel)
                                Log.i("","results: ${hotelDoc.hotel.name}")
                            }
                        }
                        liveQueryState.postValue(hotelList)
                    }
                    ?.collect()
            }
        }
        return liveQueryState
    }
}