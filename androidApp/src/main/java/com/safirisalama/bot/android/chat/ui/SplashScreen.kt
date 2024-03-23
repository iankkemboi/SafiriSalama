package com.safirisalama.bot.android.chat.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.safirisalama.bot.android.R
import com.safirisalama.bot.android.chat.ui.navigation.AppRouter

@Composable
fun SplashScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Safari Salama",
            modifier = Modifier.padding(top = 32.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = "Karibu Africa, as we say in Swahili, translates to 'Welcome to Africa.' With us, you have your personalized AI travel companion, dedicated to making your next destination a truly marvelous experience.",
            modifier = Modifier.padding(16.dp),
        )
        Image(
            painter = painterResource(id = R.drawable.business_travel),
            contentDescription = "Traveler with luggage",
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentScale = ContentScale.Fit,
        )
        Button(
            onClick = { navController.navigate(AppRouter.Screen.Home.route) },
            modifier = Modifier.padding(16.dp),
        ) {
            Text(text = "Continue")
        }
    }
}
