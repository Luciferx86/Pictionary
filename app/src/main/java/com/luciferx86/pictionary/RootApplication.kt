package com.luciferx86.pictionary

import android.app.Application
import io.socket.client.Socket

internal object RootApplication : Application() {
    var mSocket: Socket? = null

    var userObject: Socket?
        get() = mSocket
        set(socket) {
            this.mSocket = socket
        }
}