package com.jozze.nuvo.data.repository

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class AuthRepositoryImplTest {

    private lateinit var repository: AuthRepositoryImpl

    @BeforeTest
    fun setup() {
        repository = AuthRepositoryImpl()
    }

    @Test
    fun `initial user is null`() = runTest {
        assertNull(repository.getCurrentUser().first())
    }

    @Test
    fun `login updates current user`() = runTest {
        val email = "test@example.com"
        repository.loginWithEmail(email, "password")

        val user = repository.getCurrentUser().first()
        assertNotNull(user)
        assertEquals(email, user.email)
    }

    @Test
    fun `logout clears current user`() = runTest {
        repository.loginWithEmail("test@example.com", "password")
        repository.logout()

        assertNull(repository.getCurrentUser().first())
    }

    @Test
    fun `getToken returns token when logged in`() = runTest {
        repository.loginWithEmail("test@example.com", "password")
        assertNotNull(repository.getToken())
    }

    @Test
    fun `getToken returns null when logged out`() = runTest {
        assertNull(repository.getToken())
    }
}
