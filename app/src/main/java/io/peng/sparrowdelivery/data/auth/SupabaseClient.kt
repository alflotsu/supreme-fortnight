package io.peng.sparrowdelivery.data.auth

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = "https://ewnwaxaakuhagfyjqann.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImV3bndheGFha3VoYWdmeWpxYW5uIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTI5NjE0NjIsImV4cCI6MjA2ODUzNzQ2Mn0.EX8IwPJCkdMiZWGdhGjFIi_TO1Qz6i59lS4aEJC4wGQ"
    ) {
        install(Auth) {
            // Auth configuration - OAuth handled by deep links in AndroidManifest
        }
        install(Postgrest)
    }
}
