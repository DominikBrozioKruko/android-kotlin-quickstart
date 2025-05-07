package com.example.android_kotlin_quickstart.data.model
import com.example.android_kotlin_quickstart.utility.optNullableString
import org.json.JSONObject
import kotlin.random.Random

data class HotelDocumentModel(
    val hotel: Hotel
) {
    companion object {
        fun fromJson(json: JSONObject): HotelDocumentModel {
            return HotelDocumentModel(
                hotel = Hotel.fromJson(json.getJSONObject("hotel"))
            )
        }
    }
}

data class Hotel(
    val title: String?,
    val name: String?,
    val address: String?,
    val directions: String?,
    val phone: String?,
    val tollfree: String?,
    val email: String?,
    val fax: String?,
    val url: String?,
    val checkin: String?,
    val checkout: String?,
    val price: String?,
    val geo: Geo,
    val type: String?,
    var id: Int?,
    val country: String?,
    val city: String?,
    val state: String?,
    val reviews: List<Review>?,
    val publicLikes: List<String>?,
    val vacancy: Boolean?,
    val description: String?,
    val alias: String?,
    val petsOk: Boolean?,
    val freeBreakfast: Boolean?,
    val freeInternet: Boolean?,
    val freeParking: Boolean?
) {
    fun toJson(): JSONObject {
        return JSONObject().apply {
            putOpt("title", title)
            putOpt("name", name)
            putOpt("address", address)
            putOpt("directions", directions)
            putOpt("phone", phone)
            putOpt("tollfree", tollfree)
            putOpt("email", email)
            putOpt("fax", fax)
            putOpt("url", url)
            putOpt("checkin", checkin)
            putOpt("checkout", checkout)
            putOpt("price", price)
            put("geo", geo.toJson())
            putOpt("type", type)
            putOpt("id", id)
            putOpt("country", country)
            putOpt("city", city)
            putOpt("state", state)

            if (reviews != null) {
                val reviewsArray = org.json.JSONArray()
                reviews.forEach { reviewsArray.put(it.toJson()) }
                put("reviews", reviewsArray)
            }

            if (publicLikes != null) {
                val likesArray = org.json.JSONArray()
                publicLikes.forEach { likesArray.put(it) }
                put("public_likes", likesArray)
            }

            putOpt("vacancy", vacancy)
            putOpt("description", description)
            putOpt("alias", alias)
            putOpt("pets_ok", petsOk)
            putOpt("free_breakfast", freeBreakfast)
            putOpt("free_internet", freeInternet)
            putOpt("free_parking", freeParking)
        }
    }

    companion object {
        fun fromJson(json: JSONObject): Hotel {
            return Hotel(
                title = json.optNullableString("title"),
                name = json.optNullableString("name"),
                address = json.optNullableString("address"),
                directions = json.optNullableString("directions"),
                phone = json.optNullableString("phone"),
                tollfree = json.optNullableString("tollfree"),
                email = json.optNullableString("email"),
                fax = json.optNullableString("fax"),
                url = json.optNullableString("url"),
                checkin = json.optNullableString("checkin"),
                checkout = json.optNullableString("checkout"),
                price = json.optNullableString("price"),
                geo = Geo.fromJson(json.getJSONObject("geo")),
                type = json.optNullableString("type"),
                id = json.optInt("id", 0),
                country = json.optNullableString("country"),
                city = json.optNullableString("city"),
                state = json.optNullableString("state"),
                reviews = json.optJSONArray("reviews")?.let { array ->
                    List(array.length()) { i -> Review.fromJson(array.getJSONObject(i)) }
                },
                publicLikes = json.optJSONArray("public_likes")?.let { array ->
                    List(array.length()) { i -> array.getString(i) }
                },
                vacancy = json.optBoolean("vacancy"),
                description = json.optNullableString("description"),
                alias = json.optNullableString("alias"),
                petsOk = json.optBoolean("pets_ok"),
                freeBreakfast = json.optBoolean("free_breakfast"),
                freeInternet = json.optBoolean("free_internet"),
                freeParking = json.optBoolean("free_parking")
            )
        }

        fun empty(): Hotel {
            return Hotel(
                title = null,
                name = null,
                address = null,
                directions = null,
                phone = null,
                tollfree = null,
                email = null,
                fax = null,
                url = null,
                checkin = null,
                checkout = null,
                price = null,
                geo = Geo(0.0, 0.0, ""),
                type = "hotel",
                id = Random.nextInt(0, 9999999),
                country = null,
                city = null,
                state = null,
                reviews = emptyList(),
                publicLikes = emptyList(),
                vacancy = false,
                description = null,
                alias = null,
                petsOk = false,
                freeBreakfast = false,
                freeInternet = false,
                freeParking = false
            )
        }
    }
}

data class Geo(
    val lat: Double,
    val lon: Double,
    val accuracy: String
) {
    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("lat", lat)
            put("lon", lon)
            put("accuracy", accuracy)
        }
    }

    companion object {
        fun fromJson(json: JSONObject): Geo {
            return Geo(
                lat = json.getDouble("lat"),
                lon = json.getDouble("lon"),
                accuracy = json.getString("accuracy")
            )
        }
    }
}

data class Review(
    val content: String,
    val ratings: Map<String, Double>,
    val author: String,
    val date: String
) {
    fun toJson(): JSONObject {
        val ratingsJson = JSONObject().apply {
            for ((key, value) in ratings) {
                put(key, value)
            }
        }

        return JSONObject().apply {
            put("content", content)
            put("ratings", ratingsJson)
            put("author", author)
            put("date", date)
        }
    }

    companion object {
        fun fromJson(json: JSONObject): Review {
            val ratingsJson = json.getJSONObject("ratings")
            val ratingsMap = ratingsJson.keys().asSequence().associateWith { key ->
                ratingsJson.getDouble(key)
            }
            return Review(
                content = json.getString("content"),
                ratings = ratingsMap,
                author = json.getString("author"),
                date = json.getString("date")
            )
        }
    }
}
