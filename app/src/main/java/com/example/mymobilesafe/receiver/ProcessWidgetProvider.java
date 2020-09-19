package com.example.mymobilesafe.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.example.mymobilesafe.service.ProcessService;

/**
 * Created by JW.S on 2020/9/3 10:55 PM.
 */
public class ProcessWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        //15分钟更定一次
        context.startService(new Intent(context, ProcessService.class));
    }
}
