package com.jozze.nuvo.feature.auth

import com.jozze.nuvo.domain.entity.User
import com.jozze.nuvo.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeAuthRepository
    private lateinit var viewModel: AuthViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeAuthRepository()
        viewModel = AuthViewModel(repository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Idle or Authenticated based on repository`() = runTest {
        assertEquals(AuthState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `login intent updates state to Authenticated on success`() = runTest {
        val email = "test@example.com"
        val password = "password"

        viewModel.onIntent(AuthIntent.Login(email, password))
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value is AuthState.Authenticated)
        val authenticatedState = viewModel.uiState.value as AuthState.Authenticated
        assertEquals(email, authenticatedState.user.email)
    }

    @Test
    fun `logout intent updates state to Idle`() = runTest {
        // First login
        repository.loginWithEmail("test@example.com", "password")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then logout
        viewModel.onIntent(AuthIntent.Logout)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(AuthState.Idle, viewModel.uiState.value)
    }
}

class FakeAuthRepository : AuthRepository {
    private val _user = MutableStateFlow<User?>(null)
    override fun getCurrentUser(): Flow<User?> = _user
    override suspend fun getToken(): String? = if (_user.value != null) "token" else null

    override suspend fun loginWithEmail(email: String, password: String): Result<User> {
        val user = User("1", email, "Test", null, null)
        _user.value = user
        return Result.success(user)
    }

    override suspend fun logout() {
        _user.value = null
    }
}
