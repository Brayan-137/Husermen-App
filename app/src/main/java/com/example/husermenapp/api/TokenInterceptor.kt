import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class TokenInterceptor(private val tokenManager: TokenManager) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response { // <- Quita 'suspend' aquí
        val originalRequest = chain.request()

        return runBlocking {
            try {
                val (currentToken, isExpired) = getCurrentToken()

                if (!isExpired && currentToken != null) {
                    return@runBlocking chain.proceed(
                        originalRequest.newBuilder()
                            .header("Authorization", "Bearer $currentToken")
                            .build()
                    )
                }

                val newTokens = tokenManager.refreshTokenSync() // <- Ahora es suspend

                if (newTokens != null) {
                    return@runBlocking chain.proceed(
                        originalRequest.newBuilder()
                            .header("Authorization", "Bearer ${newTokens.first}")
                            .build()
                    )
                }

                throw IOException("Authentication required")
            } catch (e: Exception) {
                throw IOException("Token error: ${e.message}")
            }
        }
    }

    private suspend fun getCurrentToken(): Pair<String?, Boolean> {
        return suspendCoroutine { continuation ->
            tokenManager.getToken { token, expired ->
                continuation.resume(Pair(token, expired))
            }
        }
    }
}