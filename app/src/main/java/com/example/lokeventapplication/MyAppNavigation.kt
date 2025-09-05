package com.example.lokeventapplication

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.example.lokeventapplication.viewmodel.AuthViewModel
import com.example.lokeventapplication.viewmodel.EventsViewModel
import com.example.lokeventapplication.view.SignupScreen
import com.example.lokeventapplication.view.LoginScreen
import com.example.lokeventapplication.view.EventsScreen
import com.example.lokeventapplication.view.EventDetailScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.navDeepLink
import com.example.lokeventapplication.view.AddEventScreen


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyAppNavigation(modifier: Modifier = Modifier, authViewModel: AuthViewModel, startEventId: String? = null){
    val navController = rememberNavController()
    val eventsViewModel: EventsViewModel = viewModel() // stvara se jednom


    if (startEventId != null) {
        LaunchedEffect(startEventId) {
            navController.navigate("event_detail/$startEventId")
        }
    }

    NavHost(navController = navController, startDestination = "login", builder = {
        composable("login"){
            LoginScreen(modifier, navController, authViewModel)
        }
        composable("signup"){
            SignupScreen(modifier, navController, authViewModel)
        }
        composable("events"){
            EventsScreen(modifier, navController, authViewModel, eventsViewModel)
        }
        composable(
            route = "event_detail/{eventId}",
            deepLinks = listOf(navDeepLink { uriPattern = "myapp://event_detail/{eventId}" })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            EventDetailScreen(
                eventId = eventId,
                eventsViewModel = eventsViewModel,
                navController = navController,
                context = LocalContext.current
            )
        }
        composable("add_event") {
            AddEventScreen(navController)
        }




    })
}