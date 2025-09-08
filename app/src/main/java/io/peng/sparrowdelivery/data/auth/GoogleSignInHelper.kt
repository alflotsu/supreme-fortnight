package io.peng.sparrowdelivery.data.auth

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import io.peng.sparrowdelivery.R

class GoogleSignInHelper(
    private val context: Context
) {
    private lateinit var googleSignInClient: GoogleSignInClient
    private var signInLauncher: ActivityResultLauncher<Intent>? = null
    private var onSignInResult: ((Result<GoogleSignInAccount>) -> Unit)? = null

    fun initialize() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.google_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    fun setupActivityResultLauncher(activity: FragmentActivity) {
        signInLauncher = activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            handleSignInResult(GoogleSignIn.getSignedInAccountFromIntent(result.data))
        }
    }

    fun signIn(onResult: (Result<GoogleSignInAccount>) -> Unit) {
        onSignInResult = onResult
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher?.launch(signInIntent)
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            onSignInResult?.invoke(Result.success(account))
        } catch (e: ApiException) {
            onSignInResult?.invoke(Result.failure(e))
        }
    }

    fun signOut(): Task<Void> {
        return googleSignInClient.signOut()
    }
}
