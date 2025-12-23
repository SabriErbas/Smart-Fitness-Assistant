package com.example.gymapp002

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.gymapp002.ui.theme.MainColorScheme

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    Scaffold(modifier = Modifier.fillMaxSize(),
        bottomBar = { NavigationBar(
            modifier = Modifier.fillMaxSize(),
            containerColor = MainColorScheme.background

        ) { Text("bottombar") } }
    ) { innerPadding ->
        ContetScreen(modifier = Modifier.padding(innerPadding))
    }

}


@Composable
fun ContetScreen(modifier: Modifier = Modifier){

}