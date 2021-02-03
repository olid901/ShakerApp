package com.example.myapplication;

import android.os.Handler;
import android.os.Looper;

import androidx.recyclerview.widget.RecyclerView;

public class Helper {
    /**
     * Gibt einen RecyclerView-Adapter bescheid, dass sich seine Daten geändert haben.
     * Das muss auf dem Ui Thread ausgeführt werden.
     */
    public static void notifyAdaperFromUi(RecyclerView.Adapter adapter){
        if (adapter != null){
            runOnUiThread(() -> adapter.notifyDataSetChanged());
        }
    }

    /**
     * Gibt einen RecyclerView-Adapter bescheid, dass sich die Daten eines Elements geändert haben.
     * Das muss auf dem Ui Thread ausgeführt werden.
     */
    public static void notifyAdaperFromUi(RecyclerView.Adapter adapter, int position){
        if (adapter != null){
            runOnUiThread(() -> {
                adapter.notifyDataSetChanged();
                adapter.notifyItemChanged(position);
            });
        }
    }

    /**
     * Diese Methode führt eine Task auf dem Ui Thread aus. Ist notwendig um in
     * irgendeiner Weise mit Views zu interagieren
     * (bspw. Adapter Bescheid geben, dass die Daten sich geändert haben)
     */
    public static void runOnUiThread(Runnable task) {
        new Handler(Looper.getMainLooper()).post(task);
    }
}