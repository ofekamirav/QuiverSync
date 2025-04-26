package com.example.quiversync.android.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.quiversync.android.navigation.RootNavGraph
import com.ramcosta.composedestinations.annotation.Destination

@RootNavGraph
@Destination<RootNavGraph>
@Composable
fun ProfileScreen() {
    Text(text = "Profile Screen")
}
