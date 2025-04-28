package com.example.android_kotlin_quickstart.data.model
import com.example.android_kotlin_quickstart.utility.optNullableString
import org.json.JSONObject

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
    val id: Int?,
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
    }
}

data class Geo(
    val lat: Double,
    val lon: Double,
    val accuracy: String
) {
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
