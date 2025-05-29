package com.example.android_kotlin_quickstart.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_kotlin_quickstart.DBManager
import com.example.android_kotlin_quickstart.data.model.Hotel
import com.example.android_kotlin_quickstart.data.model.HotelDocumentModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.json.JSONObject
import java.lang.ref.WeakReference

class HomeViewModel(private val context: WeakReference<Context>) : ViewModel() {
    
    // StateFlow for UI state management (more Kotlin idiomatic than LiveData)
    private val _uiState = MutableStateFlow("Loading hotels...")
    val uiState: StateFlow<String> = _uiState.asStateFlow()
    
    private val _hotelList = MutableStateFlow<List<Hotel>>(emptyList())
    val hotelList: StateFlow<List<Hotel>> = _hotelList.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState.asStateFlow()
    
    var descendingList: Boolean = false
        private set
    
    val searchText = MutableStateFlow("")
    
    // Coroutine job for managing ongoing operations
    private var dataLoadingJob: Job? = null
    
    init {
        initializeData()
    }
    
    private fun initializeData() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _uiState.value = "Initializing database..."
                
                context.get()?.let { ctx ->
                    val dbManager = DBManager.getInstance(ctx)
                    dbManager.createDb("travel-sample")
                    dbManager.createCollection("hotel")
                    
                    // Start replication
                    launch {
                        dbManager.replicate(ctx)?.catch { e ->
                            Log.e(TAG, "Replication error", e)
                        }?.collect { /* Handle replication changes if needed */ }
                    }
                    
                    // Load initial data
                    loadData()
                }
            } catch (e: Exception) {
                handleError("Failed to initialize database", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private fun loadData() {
        // Cancel previous loading job
        dataLoadingJob?.cancel()
        
        dataLoadingJob = viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorState.value = null
                
                context.get()?.let { ctx ->
                    val dbManager = DBManager.getInstance(ctx)
                    val searchQuery = searchText.value
                    
                    // Choose query based on search input
                    val queryFlow = if (searchQuery.isNotBlank()) {
                        dbManager.queryDocsByNameFTS(searchQuery)
                    } else {
                        dbManager.queryDocs()
                    }
                    
                    // Collect query results and update UI
                    queryFlow
                        .catch { e -> handleError("Query failed", e) }
                        .map { change -> processQueryResults(change) }
                        .flowOn(Dispatchers.Default) // Process on background thread
                        .collect { hotels ->
                            _hotelList.value = hotels
                            _uiState.value = if (hotels.isEmpty()) {
                                "No hotels found"
                            } else {
                                "Loaded ${hotels.size} hotels"
                            }
                        }
                }
            } catch (e: Exception) {
                handleError("Failed to load data", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // Process query results with improved Kotlin syntax
    private suspend fun processQueryResults(change: com.couchbase.lite.QueryChange): List<Hotel> =
        withContext(Dispatchers.Default) {
            try {
                change.results?.asSequence()
                    ?.mapNotNull { result ->
                        try {
                            val jsonString = result.toJSON()
                            val jsonObject = JSONObject(jsonString)
                            HotelDocumentModel.fromJson(jsonObject).hotel
                        } catch (e: Exception) {
                            Log.w(TAG, "Failed to parse hotel document", e)
                            null
                        }
                    }
                    ?.toList()
                    ?.let { hotels ->
                        // Sort hotels with improved Kotlin syntax
                        val sorted = hotels.sortedWith(
                            compareBy(String.CASE_INSENSITIVE_ORDER) { it.name ?: "" }
                        )
                        if (descendingList) sorted.reversed() else sorted
                    } ?: emptyList()
            } catch (e: Exception) {
                Log.e(TAG, "Error processing query results", e)
                emptyList()
            }
        }
    
    // Suspend function for deleting hotels
    fun onDeleteHotel(hotel: Hotel) {
        viewModelScope.launch {
            try {
                context.get()?.let { ctx ->
                    val dbManager = DBManager.getInstance(ctx)
                    dbManager.delete(hotel)
                    Log.i(TAG, "Hotel deleted successfully: ${hotel.name}")
                }
            } catch (e: Exception) {
                handleError("Failed to delete hotel: ${hotel.name}", e)
            }
        }
    }
    
    fun onSortButtonTapped() {
        descendingList = !descendingList
        
        // Update current list without re-querying
        viewModelScope.launch(Dispatchers.Default) {
            val currentList = _hotelList.value
            val sortedList = if (descendingList) {
                currentList.reversed()
            } else {
                currentList.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name ?: "" })
            }
            _hotelList.value = sortedList
        }
    }
    
    fun onSearchTextChanged(newText: String) {
        searchText.value = newText
        
        // Debounce search to avoid excessive queries
        dataLoadingJob?.cancel()
        dataLoadingJob = viewModelScope.launch {
            delay(300) // Debounce delay
            loadData()
        }
    }
    
    // Clear error state
    fun clearError() {
        _errorState.value = null
    }
    
    // Centralized error handling
    private fun handleError(message: String, throwable: Throwable) {
        Log.e(TAG, message, throwable)
        _errorState.value = "$message: ${throwable.localizedMessage}"
        _isLoading.value = false
    }
    
    // Refresh data manually
    fun refreshData() {
        loadData()
    }
    
    override fun onCleared() {
        super.onCleared()
        dataLoadingJob?.cancel()
        
        // Cleanup database manager if needed
        viewModelScope.launch {
            try {
                context.get()?.let { ctx ->
                    val dbManager = DBManager.getInstance(ctx)
                    dbManager.cleanup()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error during cleanup", e)
            }
        }
    }
    
    companion object {
        private const val TAG = "HomeViewModel"
    }
}