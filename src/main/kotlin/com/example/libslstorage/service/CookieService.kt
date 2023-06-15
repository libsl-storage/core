package com.example.libslstorage.service

import com.example.libslstorage.entity.AccountEntity
import com.example.libslstorage.util.ACCESS_TOKEN_COOKIE_NAME
import com.example.libslstorage.util.AUTH_FLAG_COOKIE_NAME
import com.example.libslstorage.util.REFRESH_TOKEN_COOKIE_NAME
import jakarta.servlet.http.Cookie
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class CookieService(
    private val tokenService: TokenService
) {

    @Value("\${security.accessTokenMaxAge}")
    private val accessTokenMaxAge = 1200

    @Value("\${security.refreshTokenMaxAge}")
    private val refreshTokenMaxAge = 604800

    private fun createTokenCookie(
        account: AccountEntity,
        cookieName: String,
        maxAge: Int
    ): Cookie {
        val token = tokenService.createToken(account, maxAge.toLong())
        val tokenCookie = Cookie(cookieName, token)
        tokenCookie.maxAge = maxAge
        tokenCookie.isHttpOnly = true
        tokenCookie.path = "/"
        return tokenCookie
    }

    fun createAccessTokenCookie(account: AccountEntity): Cookie {
        return createTokenCookie(account, ACCESS_TOKEN_COOKIE_NAME, accessTokenMaxAge)
    }

    fun createRefreshTokenCookie(account: AccountEntity): Cookie {
        return createTokenCookie(account, REFRESH_TOKEN_COOKIE_NAME, refreshTokenMaxAge)
    }

    fun createAuthFlagCookie(): Cookie {
        val cookie = Cookie(AUTH_FLAG_COOKIE_NAME, "")
        cookie.path = "/"
        cookie.maxAge = refreshTokenMaxAge
        return cookie
    }

    fun createAuthCookies(account: AccountEntity): List<Cookie> {
        val accessTokenCookie = createAccessTokenCookie(account)
        val refreshTokenCookie = createRefreshTokenCookie(account)
        val authFlagCookie = createAuthFlagCookie()
        return listOf(accessTokenCookie, refreshTokenCookie, authFlagCookie)
    }
}
