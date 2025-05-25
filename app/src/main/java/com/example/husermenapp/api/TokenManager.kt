import com.example.husermenapp.api.await
import com.google.firebase.database.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class TokenManager() {
    private val database = FirebaseDatabase.getInstance()

    fun saveTokens(accessToken: String, refreshToken: String, expiresIn: Long) {
        val expiresAt = System.currentTimeMillis() + (expiresIn * 1000)

        val tokenData = hashMapOf(
            "access_token" to accessToken,
            "refresh_token" to refreshToken,
            "expires_at" to expiresAt,
            "last_updated" to System.currentTimeMillis()
        )

        database.reference.child("tokens").child("mercadolibre")
            .setValue(tokenData)
    }

    fun getToken(callback: (String?, Boolean) -> Unit) {
        database.reference.child("tokens").child("mercadolibre")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        callback(null, true)
                        return
                    }

                    val accessToken = snapshot.child("access_token").getValue(String::class.java)
                    val expiresAt = snapshot.child("expires_at").getValue(Long::class.java) ?: 0
                    val isExpired = System.currentTimeMillis() > expiresAt

                    callback(accessToken, isExpired)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(null, true)
                }
            })
    }

    suspend fun getTokenSuspend(): String? = suspendCoroutine { continuation ->
        getToken { token, isExpired ->
            if (!isExpired && token != null) {
                continuation.resume(token)
            } else {
                continuation.resume(null)
            }
        }
    }

    @Throws(IOException::class)
    fun refreshTokenSync(): Pair<String, String>? {
        val currentRefreshToken = getRefreshTokenSync() ?: return null

        val client = OkHttpClient()
        val requestBody = FormBody.Builder()
            .add("grant_type", "refresh_token")
            .add("client_id", "8551929605562168")
            .add("client_secret", "P1ICWkT0Lb7TsU4oO3KSvlNytoQPYEdl")
            .add("refresh_token", currentRefreshToken)
            .build()

        val request = Request.Builder()
            .url("https://api.mercadolibre.com/oauth/token")
            .post(requestBody)
            .build()

        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val jsonResponse = response.body?.string() ?: return null
                val jsonObject = JSONObject(jsonResponse)

                val newAccessToken = jsonObject.getString("access_token")
                val newRefreshToken = jsonObject.getString("refresh_token")
                val expiresIn = jsonObject.getLong("expires_in")

                // Guardamos los nuevos tokens
                saveTokens(newAccessToken, newRefreshToken, expiresIn)

                return Pair(newAccessToken, newRefreshToken)
            }
            return null
        } catch (e: Exception) {
            throw IOException("Failed to refresh token: ${e.message}")
        }
    }

    @Throws(IOException::class)
    private fun getRefreshTokenSync(): String? {
        try {
            val snapshot = database.reference.child("tokens")
                .child("mercadolibre")
                .child("refresh_token")
                .get()
                .await()

            return snapshot.getValue(String::class.java)
        } catch (e: Exception) {
            throw IOException("Failed to get refresh token: ${e.message}")
        }
    }
}