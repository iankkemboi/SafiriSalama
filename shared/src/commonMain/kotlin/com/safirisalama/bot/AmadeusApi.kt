package com.safirisalama.bot

import com.safirisalama.bot.models.Data
import com.safirisalama.bot.models.FlightOffersResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class AmadeusApi {
    private var accessToken = ""
    private val client = HttpClient() {
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.ALL
        }
        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    ignoreUnknownKeys = true
                },
            )
        }
    }

    private suspend fun getAccessToken(): String {
        // Use your credentials from the Amadeus developer dashboard
        val formData = Parameters.build {
            append("grant_type", "client_credentials")
            append("client_id", APIConstants.API_KEY)
            append("client_secret", APIConstants.API_SECRET)
        }

        return try {
            val response: AmadeusOAuth2TokenResponse =
                client.post("https://test.api.amadeus.com/v1/security/oauth2/token") {
                    setBody(FormDataContent(formData))
                    contentType(ContentType.Application.FormUrlEncoded)
                    accept(ContentType.Application.Json)
                }.body()
            print(response.accessToken)
            response.accessToken
        } catch (e: ClientRequestException) {
            println(e.message)
            ""
        } catch (e: Exception) {
            println(e.message)
            ""
        }
    }

    suspend fun searchHotelByCity(
        city: String,
        amenities: String,
        rating: String,
    ): Pair<List<Hotel>, String> {
        try {
            accessToken = getAccessToken()
            var url =
                "https://test.api.amadeus.com/v1/reference-data/locations/hotels/by-city?cityCode=$city&radius=50&radiusUnit=KM"
            if (amenities.isNotEmpty()) {
                url = "$url&amenities=$amenities"
            }
            if (rating.isNotEmpty()) {
                url = "$url&ratings=$rating"
            }
            val response: HotelSearchResponse =
                client.get(url) {
                    headers {
                        bearerAuth(accessToken)
                    }
                }.body()
            if (response.errors.isNotEmpty()) {
                return Pair(emptyList(), response.errors.first().detail ?: "An error occurred")
            }
            return Pair(response.data, "")
        } catch (e: ClientRequestException) {
            return Pair(emptyList(), e.message ?: "An error occurred")
        } catch (e: Exception) {
            return Pair(emptyList(), e.message ?: "An error occurred")
        }
    }

    suspend fun searchFlightPrices(destinationCode: String): Pair<List<Data>, String> {
        try {
            accessToken = getAccessToken()
            var url =
                "https://test.api.amadeus.com/v2/shopping/flight-offers?originLocationCode=BER&destinationLocationCode=$destinationCode&departureDate=2024-05-02&adults=1&travelClass=PREMIUM_ECONOMY&nonStop=false&currencyCode=EUR&max=250"

            val response: FlightOffersPriceResponse =
                client.get(url) {
                    headers {
                        bearerAuth(accessToken)
                    }
                }.body()
            if (response.errors.isNotEmpty()) {
                return Pair(emptyList(), response.errors.first().detail ?: "An error occurred")
            }
            return Pair(response.data, "")
        } catch (e: ClientRequestException) {
            return Pair(emptyList(), e.message ?: "An error occurred")
        } catch (e: Exception) {
            return Pair(emptyList(), e.message ?: "An error occurred")
        }
    }
}
