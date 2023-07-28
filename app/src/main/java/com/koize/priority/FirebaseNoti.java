package com.koize.priority;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class FirebaseNoti extends Service {
    public FirebaseNoti() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}