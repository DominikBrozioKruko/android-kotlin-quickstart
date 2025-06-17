package com.example.android_kotlin_quickstart

import android.content.Context
import android.util.Log
import com.couchbase.lite.*
import com.example.android_kotlin_quickstart.data.model.AppError
import com.example.android_kotlin_quickstart.data.model.Hotel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.json.JSONObject
import java.net.URI
import java.util.concurrent.atomic.AtomicReference

class DBManager private constructor() {
    private var database: Database? = null
    private var collection: com.couchbase.lite.Collection? = null
    private var replicator: Replicator? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // Kotlin extension property for cleaner access
    private val hotelCollection: com.couchbase.lite.Collection?
        get() = collection

    // <.>
    // One-off initialization using suspend function
    private suspend fun init(context: Context): Unit = withContext(Dispatchers.IO) {
        try {
            CouchbaseLite.init(context)
            Log.i(TAG, "CBL Initialized")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize CBL", e)
            throw e
        }
    }

    // <.>
    // Create a database with suspend function and exception handling
    suspend fun createDb(dbName: String): Unit = withContext(Dispatchers.IO) {
        try {
            database = Database(dbName)
            Log.i(TAG, "Database created: $dbName")
        } catch (e: CouchbaseLiteException) {
            Log.e(TAG, "Failed to create database: $dbName", e)
            throw e
        }
    }

    // <.>
    // Create collection with FTS index using Kotlin extensions
    suspend fun createCollection(collName: String): Unit = withContext(Dispatchers.IO) {
        try {
            collection = database?.createCollection(collName, "inventory")
            Log.i(TAG, "Collection created: $collection")
            
            // Create FTS index using Kotlin extensions
            hotelCollection?.let { coll ->
                coll.createIndex(
                    "hotelNameFTSIndex",
                    IndexBuilder.fullTextIndex(
                        FullTextIndexItem.property("name")
                    ).ignoreAccents(false)
                )
                Log.i(TAG, "FTS index created successfully")
            }
        } catch (e: CouchbaseLiteException) {
            Log.e(TAG, "Failed to create collection: $collName", e)
            throw e
        }
    }

    // Suspend function for creating hotel documents
    suspend fun create(hotel: Hotel): Unit = withContext(Dispatchers.IO) {
        try {
            val hotelJson = hotel.toJson()
            val mutableDocument = MutableDocument().setJSON(hotelJson.toString())
            hotelCollection?.save(mutableDocument)
            Log.i(TAG, "Hotel created: ${hotel.name}")
        } catch (e: CouchbaseLiteException) {
            Log.e(TAG, "Failed to create hotel: ${hotel.name}", e)
            throw e
        }
    }

    // Suspend function for deleting hotels with improved Kotlin syntax
    suspend fun delete(hotel: Hotel): Unit = withContext(Dispatchers.IO) {
        try {
            database?.let { db ->
                val query = db.createQuery(
                    "SELECT META().id FROM inventory.hotel WHERE type = 'hotel' AND id = ${hotel.id}"
                )
                query.execute().use { rs ->
                    for (result in rs) {
                        val docId = result.getString("id")
                        val doc = docId?.let { hotelCollection?.getDocument(it) }
                        doc?.let { hotelCollection?.delete(it) }
                    }
                }
                Log.i(TAG, "Hotel deleted: ${hotel.name}")
            }
        } catch (e: CouchbaseLiteException) {
            Log.e(TAG, "Failed to delete hotel: ${hotel.name}", e)
            throw e
        }
    }

    // Suspend function for updating hotels
    suspend fun update(hotel: Hotel): Unit = withContext(Dispatchers.IO) {
        try {
            database?.let { db ->
                val query = db.createQuery(
                    "SELECT META().id FROM inventory.hotel WHERE type = 'hotel' AND id = ${hotel.id}"
                )
                query.execute().use { rs ->
                    for (result in rs) {
                        val docId = result.getString("id")
                        val doc = docId?.let { hotelCollection?.getDocument(it) }
                        doc?.let {
                            val mutableDoc = it.toMutable().setJSON(hotel.toJson().toString())
                            hotelCollection?.save(mutableDoc)
                        }
                    }
                }
                Log.i(TAG, "Hotel updated: ${hotel.name}")
            }
        } catch (e: CouchbaseLiteException) {
            Log.e(TAG, "Failed to update hotel: ${hotel.name}", e)
            throw e
        }
    }

    // Flow-based query with proper error handling
    fun queryDocs(): Flow<QueryChange> = flow {
        val hotelCollection = hotelCollection ?: throw IllegalStateException("Collection not initialized")
        val database = database ?: throw IllegalStateException("Database not initialized")
        
        try {
            val query = database.createQuery(
                "SELECT * FROM inventory.hotel WHERE type = 'hotel' ORDER BY name ASC"
            )
            
            query.queryChangeFlow()
                .catch { e -> 
                    Log.e(TAG, "Query error", e)
                    throw e
                }
                .collect { emit(it) }
        } catch (e: CouchbaseLiteException) {
            Log.e(TAG, "Failed to execute query", e)
            throw e
        }
    }.flowOn(Dispatchers.IO)

    // FTS query with improved error handling and Kotlin extensions
    fun queryDocsByNameFTS(search: String): Flow<QueryChange> = flow {
        val hotelCollection = hotelCollection ?: throw IllegalStateException("Collection not initialized")
        val database = database ?: throw IllegalStateException("Database not initialized")
        
        try {
            val searchTerm = if (search.isNotBlank()) "$search*" else search
            val query = database.createQuery(
                """
                SELECT * FROM inventory.hotel 
                WHERE MATCH(hotelNameFTSIndex, '$searchTerm') AND type = 'hotel' 
                ORDER BY name ASC
                """.trimIndent()
            )
            
            query.queryChangeFlow()
                .catch { e -> 
                    Log.e(TAG, "FTS Query error", e)
                    throw e
                }
                .collect { emit(it) }
        } catch (e: CouchbaseLiteException) {
            Log.e(TAG, "Failed to execute FTS query", e)
            throw e
        }
    }.flowOn(Dispatchers.IO)

    // Replication with coroutines and proper error handling
    suspend fun replicate(context: Context): Flow<ReplicatorChange>? = withContext(Dispatchers.IO) {
        val coll = hotelCollection ?: return@withContext null

        try {
            val collConfig = CollectionConfiguration()
            val config = ConfigManager.getInstance(context).getConfig()
                ?: run {
                    ErrorManager.postError(
                        AppError(
                            "Cannot connect to Capella App Services for data sync",
                            "Please make sure the App Services connection URL, username and password are placed in the app configuration file",
                            showDismissButton = false
                        )
                    )
                    return@withContext null
                }

            val repl = Replicator(
                ReplicatorConfigurationFactory.newConfig(
                    target = URLEndpoint(URI(config.remoteCapellaEndpointURL)),
                    collections = mapOf(setOf(coll) to collConfig),
                    type = ReplicatorType.PUSH_AND_PULL,
                    authenticator = BasicAuthenticator(config.userName, config.password.toCharArray())
                )
            )

            // Use Kotlin extension for replicator changes flow
            val changes = repl.replicatorChangesFlow()
                .catch { e ->
                    Log.e(TAG, "Replication error", e)
                    throw e
                }
                .flowOn(Dispatchers.IO)

            repl.start()
            replicator = repl
            
            Log.i(TAG, "Replication started successfully")
            changes
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start replication", e)
            throw e
        }
    }

    // Cleanup function with coroutines
    suspend fun cleanup(): Unit = withContext(Dispatchers.IO) {
        try {
            replicator?.stop()
            database?.close()
            scope.cancel()
            Log.i(TAG, "DBManager cleaned up successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error during cleanup", e)
        }
    }

    companion object {
        private const val TAG = "START_KOTLIN"
        private val INSTANCE = AtomicReference<DBManager?>()

        suspend fun getInstance(context: Context): DBManager {
            return INSTANCE.get() ?: run {
                val mgr = DBManager()
                if (INSTANCE.compareAndSet(null, mgr)) {
                    mgr.init(context)
                    mgr
                } else {
                    INSTANCE.get()!!
                }
            }
        }
    }
}