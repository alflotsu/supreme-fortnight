package io.peng.sparrowdelivery.data.auth

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import io.peng.sparrowdelivery.R

@Composable
fun rememberGoogleSignInClient(context: Context): GoogleSignInClient {
    return remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.google_web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }
}

@Composable
fun GoogleSignInLauncher(
    googleSignInClient: GoogleSignInClient,
    onResult: (Result<GoogleSignInAccount>) -> Unit
): () -> Unit {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.getResult(ApiException::class.java)
            onResult(Result.success(account))
        } catch (e: ApiException) {
            onResult(Result.failure(e))
        }
    }
    
    return {
        launcher.launch(googleSignInClient.signInIntent)
    }
}
