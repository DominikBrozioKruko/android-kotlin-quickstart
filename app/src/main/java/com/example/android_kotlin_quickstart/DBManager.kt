package com.example.android_kotlin_quickstart

import android.content.Context
import android.util.Log
import com.couchbase.lite.BasicAuthenticator
import com.couchbase.lite.Collection
import com.couchbase.lite.CollectionConfiguration
import com.couchbase.lite.CouchbaseLite
import com.couchbase.lite.DataSource
import com.couchbase.lite.Database
import com.couchbase.lite.Expression
import com.couchbase.lite.ListenerToken
import com.couchbase.lite.MutableDocument
import com.couchbase.lite.Query
import com.couchbase.lite.QueryBuilder
import com.couchbase.lite.QueryChange
import com.couchbase.lite.QueryChangeListener
import com.couchbase.lite.Replicator
import com.couchbase.lite.ReplicatorChange
import com.couchbase.lite.ReplicatorConfigurationFactory
import com.couchbase.lite.ReplicatorType
import com.couchbase.lite.SelectResult
import com.couchbase.lite.URLEndpoint
import com.couchbase.lite.newConfig
import com.couchbase.lite.queryChangeFlow
import com.couchbase.lite.replicatorChangesFlow
import com.example.android_kotlin_quickstart.data.model.AppError
import com.example.android_kotlin_quickstart.data.model.Hotel
import kotlinx.coroutines.flow.Flow
import org.json.JSONObject
import java.net.URI
import java.util.concurrent.atomic.AtomicReference

class DBManager {
    private var database: Database? = null
    private var collection: Collection? = null
    private var replicator: Replicator? = null
    private var token: ListenerToken? = null

    // tag::getting-started[]

    // <.>
    // One-off initialization
    private fun init(context: Context) {
        CouchbaseLite.init(context)
        Log.i(TAG, "CBL Initialized")
    }

    // <.>
    // Create a database
    fun createDb(dbName: String) {
        database = Database(dbName)
        Log.i(TAG, "Database created: $dbName")
    }

    // <.>
    // Create a new named collection (like a SQL table)
    // in the database's default scope.
    fun createCollection(collName: String) {
        collection = database!!.createCollection(collName,"inventory")
        Log.i(TAG, "Collection created: $collection")
    }

    fun create(hotel: Hotel) {
        val hotelJson: JSONObject = hotel.toJson()
        val mutableDocument = MutableDocument()
            .setJSON(hotelJson.toString())
        collection?.save(mutableDocument)
    }

    fun delete(hotel: Hotel) {
        val database = database ?: return
        val query: Query = database.createQuery("SELECT META().id FROM inventory.hotel WHERE type = 'hotel' AND id = ${hotel.id}")
        query.execute().use { rs ->
            rs.forEach {
                val docId = it.getString("id")
                val doc = docId?.let { it1 -> collection?.getDocument(it1) }
                doc?.let { it1 -> collection?.delete(it1) }
            }
        }
    }
    fun update(hotel: Hotel) {
        val database = database ?: return
        val query: Query = database.createQuery("SELECT META().id FROM inventory.hotel WHERE type = 'hotel' AND id = ${hotel.id}")
        query.execute().use { rs ->
            rs.forEach {
                val docId = it.getString("id")
                val doc = docId?.let { it1 -> collection?.getDocument(it1) }
                doc?.let { it1 -> collection?.save(it1.toMutable().setJSON(hotel.toJson().toString())) }
            }
        }
    }

    fun queryDocs(): Flow<QueryChange>? {
        token?.remove()
        val coll = collection ?: return null
        val database = database ?: return null
        val query: Query = database.createQuery("SELECT * FROM inventory.hotel WHERE type = 'hotel' ORDER BY name ASC")

        val changes = query.queryChangeFlow()
        query.execute()
        return changes
    }

    fun replicate(context: Context): Flow<ReplicatorChange>? {
        val coll = collection ?: return null

        val collConfig = CollectionConfiguration()
        val config = ConfigManager.getInstance(context).getConfig()
            ?: run {
                ErrorManager.postError(AppError("Cannot connect to Capella App Services for data sync","Please make sure the App Services connection URL, username and password are placed in the app configuration file", showDismissButton = false))
                return null
            }

        val repl = Replicator(
            ReplicatorConfigurationFactory.newConfig(
                target = URLEndpoint(URI(config.remoteCapellaEndpointURL)),
                collections = mapOf(setOf(coll) to collConfig),
                type = ReplicatorType.PUSH_AND_PULL,
                authenticator = BasicAuthenticator(config.userName, config.password.toCharArray())
            )
        )

        // Listen to replicator change events.
        val changes = repl.replicatorChangesFlow()

        // Start replication.
        repl.start()
        replicator = repl

        return changes
    }
    // end::getting-started[]

    companion object {
        private const val TAG = "START_KOTLIN"

        private val INSTANCE = AtomicReference<DBManager?>()

        @Synchronized
        fun getInstance(context: Context): DBManager {
            var mgr = INSTANCE.get()
            if (mgr == null) {
                mgr = DBManager()
                if (INSTANCE.compareAndSet(null, mgr)) {
                    mgr.init(context)
                }
            }
            return INSTANCE.get()!!
        }
    }
}