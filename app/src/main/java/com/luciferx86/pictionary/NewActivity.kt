package com.luciferx86.pictionary

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class NewActivity : AppCompatActivity() {
    var dv: NewDoodle? = null
    override fun onCreate(savedInstanceState: Bundle?) {
//        dv = NewDoodle(this);
        super.onCreate(savedInstanceState)
        setContentView(dv);
    }
}