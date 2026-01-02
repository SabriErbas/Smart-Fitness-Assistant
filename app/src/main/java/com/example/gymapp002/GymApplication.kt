package com.example.gymapp002

import android.app.Application
import com.example.gymapp002.data.local.GymDatabase

class GymApplication : Application() {


    val database by lazy { GymDatabase.getDatabase(this)}
}