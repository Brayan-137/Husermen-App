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

        // Opción 1: Usar runBlocking para manejar corrutinas en código síncrono
        return runBlocking {
            try {
                // Intento 1: Verificar token existente
                val (currentToken, isExpired) = getCurrentToken()

                if (!isExpired && currentToken != null) {
                    return@runBlocking chain.proceed(
                        originalRequest.newBuilder()
                            .header("Authorization", "Bearer $currentToken")
                            .build()
                    )
                }

                // Intento 2: Renovar el token (llamada suspend dentro de runBlocking)
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