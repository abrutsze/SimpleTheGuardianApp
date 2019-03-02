package com.artur.simpleTheGuardianApp.Utils;

import android.util.Log;

/**
 * Created by artur on 02-Mar-19.
 */

public class LogsClass {

    private static LogsClass logsClass;
    private final boolean showLogs = true;
    private final boolean showDebugLogs = true;
    private final boolean showErrorLogs = true;
    private final boolean showWarningLogs = true;
    private final boolean showInfoLogs = true;
    private final boolean showWTFLogs = true;

    public static LogsClass getLogsClassInstance() {
        if (logsClass == null) {
            logsClass = new LogsClass();
        }
        return logsClass;
    }

    public void d(String message) {
        if (showLogs && showDebugLogs) Log.d(getCallerInfo(), message);
    }

    public void e(String message) {
        if (showLogs && showErrorLogs) Log.e(getCallerInfo(), message);
    }

    public void w(String message) {
        if (showLogs && showWarningLogs) Log.w(getCallerInfo(), message);
    }

    public void i(String message) {
        if (showLogs && showInfoLogs) Log.i(getCallerInfo(), message);
    }

    public void wtf(String message) {
        if (showLogs && showWTFLogs) Log.wtf(getCallerInfo(), message);
    }

    private String getCallerInfo() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement element = stackTrace[4];
        return (element.getClassName() + "___" + element.getMethodName() + ":::");
    }

}
