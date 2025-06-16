# Hotel Management Quick Start App using Couchbase Lite Android SDK - Kotlin + Jetpack Compose + Capella App Services

## Introduction
[Couchbase Mobile](https://docs.couchbase.com/home/mobile.html) brings the power of NoSQL to the edge with offline-first data sync. It is comprised of three components:

- [Couchbase Lite](https://docs.couchbase.com/home/mobile.html), local-first, embedded, NoSQL JSON Document database for your mobile, desktop and embedded apps
- Sync Gateway, an internet-facing component that securely syncs data between Couchbase Lite clients and Couchbase server
- Couchbase Server, a highly scalable, distributed NoSQL database platform backing data store

Couchbase Server and Sync Gateway can be deployed in a self-managed configuration or developers can leverage **Capella**, Couchbase's fully managed Couchbase Server offering. This tutorial uses Capella App Services as backend data store and sync for Couchbase Lite App. Capella offers a [**free tier**](https://docs.couchbase.com/cloud/get-started/intro.html) for developers to explore core features at no cost.

## App Overview
This Android sample app demonstrates how to build a simple offline-first Jetpack Compose application using Couchbase Lite and Capella App Services. The app presents a basic hotel listing interface powered by data from the `travel-sample` dataset, which is hosted in Couchbase Capella and synced locally to the device.

The project serves as a quickstart for developers building mobile apps that require embedded database functionality with support for local reads/writes, flexible querying, and real-time synchronization with the cloud.

It covers the essentials of:
- Basics of App Services and configuring it for remote sync
- Couchbase Lite database setup and integration on Android using the Kotlin SDK
- Performing basic create, read, update, and delete (CRUD) operations
- Basic Query and Full Text Search (FTS)
- Syncing documents from a remote Capella cluster using App Services
- Connecting a Jetpack Compose interface to local database content

This sample is an ideal example of how developers can build apps that must remain usable without a network connection and synchronize data reliably once connectivity is restored.

## Setup and Technology Stack
- Capella / Capella App Services
- Couchbase Lite Android KTX SDK (3.2.2)
- Jetpack Compose
- Quick Start Android Kotlin App

## Features
- Offline-first data access with Couchbase Lite
- Jetpack Compose interface listing hotels from Capella
- Secure data sync using App Services
- Syncs with hotel collection from `travel-sample` dataset
- Basic app user authentication
- Unit and UI tests with JUnit and AndroidX

## Project Requirements
- Android Studio: Hedgehog (or newer)
- Android SDK: 34+
- Minimum SDK: 34
- Language: Kotlin
- UI: Jetpack Compose
- Dependency Management: Gradle (Kotlin DSL)
- Database: Couchbase Lite Android KTX SDK (3.2.2)
- Testing: JUnit, AndroidX Test

## Installation Instructions

### Capella Cluster Setup
1. Sign up for Capella Free Tier and follow the steps to deploy a free tier cluster.
2. When you create a free tier cluster, it will automatically import a **travel-sample** bucket with datasets that we will use in this app. If you deleted the travel-sample bucket, you can reimport it from Data Tools → Import tab.

### Create a new App Service
1. Follow [instructions](https://docs.couchbase.com/cloud/get-started/create-account.html#app-services) to create a free tier App Service that links to the free tier cluster created in the previous step.
2. On the Create App Services page:
   - Enter a name for your app service (e.g. `demo-app-service`)
   - Under the **Linked Cluster** section, select your demo-cluster from the drop-down list
   - Click the **Create App Service** button
   - It may take several minutes for the new App Service to be created.

### Create a new App Endpoint
1. Once the App Service is created and its status is listed as `Healthy`, click on the App Service name you just created.
2. On the App Endpoints screen, click the **Create App Endpoint** button.
3. On the Create App Endpoint page:
   - Enter a name (e.g. `hotels`) in the **App Endpoint Name** field
   - For **Bucket**, select `travel-sample`
   - For **Scope**, select `inventory`
   - Under **Choose collections to link**, enable the `hotel` collection by switching the Link toggle to On
   - Click the **Create App Endpoint** button

### Set up App Endpoint security
1. Click on the newly created endpoint (e.g. `hotels`) to view the App Endpoint details.
2. On the **Security** tab:
   - If a message appears stating that the App Endpoint is paused, click the **Resume app endpoint** link
3. We will use the default access control and data validation function, so no changes are required there.
4. Click on the **App Users** tab in the left-hand navigation menu.
5. Click the **Create App User** button.
6. On the Create App User page:
   - Enter a username and password for the new user *(Be sure to save these credentials — you'll need them for the app configuration)*
   - Click the **Configure Access Grants** link to expand the section.
   - Under **Assign Channels**:
     - Locate the `hotel` collection under **Linked Collections**
     - Enter the Admin Channel name `hotel` and hit the Enter key (it should appear as a chip or tag)
   - Click the **Create App User** button.

### Get the App Endpoint URL
1. Click on the **Connect** tab in the navigation header.
2. The **Connect** page will appear, showing the Public Connection string.
3. Click the **copy icon** next to the URL to copy the App Endpoint URL to your clipboard. You'll need this in the app configuration file.

### Clone this repository
Use the terminal to clone the GitHub repository:
```bash
git clone https://github.com/couchbase-examples/android-kotlin-quickstart.git
```

### Open the project in Android Studio
- Open Android Studio and select **Open an existing project**
- Navigate to the `android-kotlin-quickstart` folder where the repo was cloned

### Configure the app
- Locate the `app/src/main/assets/config.json` file
- Open the file and provide the following configuration values:
  - **remoteCapellaEndpointURL** – Paste the "Public URL" from the Connect page of App Services
  - **userName** – Enter the App User username
  - **password** – Enter the App User password

Example `config.json`:
```json
{
  "remoteCapellaEndpointURL": "wss://<your-app-endpoint>.apps.cloud.couchbase.com:4984/hotels-endpoint",
  "userName": "<your-app-username>",
  "password": "<your-app-password>"
}
```

> ⚠️ **Caution:** If you do not fill out the configuration file, the app **will not sync** with your Capella cluster or endpoint.

### Run the app
- Connect an Android device or start an emulator
- In Android Studio, click the **Run** button or press `Shift + F10`
- You should see the Hotels Management App show up with the travel-sample hotels list populated

## Try it Out
After running the app successfully, explore the following key features. Each feature below is supported by code inside the `DBManager.kt` class and demonstrates how Couchbase Lite powers this offline-first experience.

### Database and Replication Operations
**Description:**  
On launch, the app initializes a local Couchbase Lite database and sets up continuous replication with Capella App Services. The `hotel` collection from the `inventory` scope is synced down from the remote cluster.

```kotlin
// Initialization
CouchbaseLite.init(context)
database = Database("travel-sample")
collection = database.createCollection("hotel", "inventory")

// Replication
val config = ReplicatorConfigurationFactory.newConfig(
    target = URLEndpoint(URI(config.remoteCapellaEndpointURL)),
    collections = mapOf(setOf(collection) to CollectionConfiguration()),
    type = ReplicatorType.PUSH_AND_PULL,
    authenticator = BasicAuthenticator(config.userName, config.password.toCharArray())
)
val replicator = Replicator(config)
replicator.start()
```
Learn more:  
- [Database Initialization](https://docs.couchbase.com/couchbase-lite/current/android/database.html#open-db)  
- [Replication](https://docs.couchbase.com/couchbase-lite/current/android/replication.html)

### Document CRUD Operations
**Description:**  
You can add, update, and delete hotel documents from the local database using simple methods.

**Add a hotel document:**
```kotlin
val hotelJson = hotel.toJson()
val mutableDocument = MutableDocument().setJSON(hotelJson.toString())
hotelCollection.save(mutableDocument)
```

**Update a hotel document:**
```kotlin
val query = database.createQuery("SELECT META().id FROM inventory.hotel WHERE type = 'hotel' AND id = ${hotel.id}")
query.execute().use { rs ->
    for (result in rs) {
        val docId = result.getString("id")
        val doc = docId?.let { hotelCollection.getDocument(it) }
        doc?.let {
            val mutableDoc = it.toMutable().setJSON(hotel.toJson().toString())
            hotelCollection.save(mutableDoc)
        }
    }
}
```

**Delete a hotel document:**
```kotlin
val query = database.createQuery("SELECT META().id FROM inventory.hotel WHERE type = 'hotel' AND id = ${hotel.id}")
query.execute().use { rs ->
    for (result in rs) {
        val docId = result.getString("id")
        val doc = docId?.let { hotelCollection.getDocument(it) }
        doc?.let { hotelCollection.delete(it) }
    }
}
```
Learn more: [Working with Documents](https://docs.couchbase.com/couchbase-lite/current/android/document.html)

### Query & Live Query Updates
**Description:**  
The app performs flexible queries using SQL-like syntax (N1QL). A live query listener updates the UI whenever matching data changes in real time.

```kotlin
val query = database.createQuery("SELECT * FROM inventory.hotel WHERE type = 'hotel' ORDER BY name ASC")
query.queryChangeFlow().collect { change ->
    // Update UI with new hotel list
}
```
Learn more:  
- [SQL++ Queries](https://docs.couchbase.com/couchbase-lite/current/android/query-n1ql-mobile.html)  
- [Live Queries](https://docs.couchbase.com/couchbase-lite/current/android/query-live.html)

### Full-Text Search
**Description:**  
You can search hotel names using Couchbase Lite's built-in Full Text Search (FTS). This is enabled via an index created on the `name` field.

**Index creation:**
```kotlin
collection.createIndex(
    "hotelNameFTSIndex",
    IndexBuilder.fullTextIndex(FullTextIndexItem.property("name")).ignoreAccents(false)
)
```

**Search query:**
```kotlin
val searchTerm = "$search*"
val query = database.createQuery(
    "SELECT * FROM inventory.hotel WHERE MATCH(hotelNameFTSIndex, '$searchTerm') AND type = 'hotel' ORDER BY name ASC"
)
query.queryChangeFlow().collect { change ->
    // Update UI with search results
}
```
Learn more: [Full Text Search](https://docs.couchbase.com/couchbase-lite/current/android/fts.html)

### Offline-First Sync
**Description:**  
Even if you disconnect from the network or Capella App Services go offline, the app continues working with the local database. Any local changes will sync automatically when the connection is restored.

**Try this:**
1. Disable your network or pause the Capella App Endpoint.
2. Add, edit, or delete a hotel in the app.
3. Reconnect to the network and watch the changes sync automatically.
4. Or — try modifying a document in the Capella UI and see it show up in the app.

Learn more: [Offline-First Architecture](https://www.couchbase.com/blog/couchbase-offline-first-app-use-cases/)

## Learn more
To learn more about Couchbase Lite and App Services, check out the following resources:
- [Couchbase Lite on Android](https://docs.couchbase.com/couchbase-lite/current/android/quickstart.html)
- [App Services](https://docs.couchbase.com/cloud/app-services/index.html)
- [App Services Access Control Concepts - Channels](https://docs.couchbase.com/cloud/app-services/channels/channels.html)
- [App Services Access Control & Data Validation](https://docs.couchbase.com/cloud/app-services/deployment/access-control-data-validation.html)

## Getting Help
You can reach out to us on the Github repo or join our community of developers:
- [Discord community](https://bit.ly/3NbK5vg): Chat with Couchbase developers and ask questions.
- [Stack Overflow community](https://stackoverflow.com/tags/couchbase/info/): Ask questions.
- [Developer Portal](https://www.couchbase.com/developer): More information including tutorials and learning paths.
