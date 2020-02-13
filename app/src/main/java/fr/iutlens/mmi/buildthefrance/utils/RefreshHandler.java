package fr.iutlens.mmi.buildthefrance.utils;


import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by dubois on 27/12/2017.
 */

public class RefreshHandler extends Handler {

        WeakReference<TimerAction> weak;

        public RefreshHandler(TimerAction animator){
            weak = new WeakReference(animator);
        }

        @Override
        public void handleMessage(Message msg) {
            if (weak.get() == null) return;
            weak.get().update();
        }

        public void scheduleRefresh(long delayMillis) {
            this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }

        public boolean isRunning() {
            return this.hasMessages(0);
        }
}
