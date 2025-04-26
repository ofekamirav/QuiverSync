package com.example.quiversync.android.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.generated.NavGraphs

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    DestinationsNavHost(
        navGraph = NavGraphs.root,
        navController = navController
    )

}
