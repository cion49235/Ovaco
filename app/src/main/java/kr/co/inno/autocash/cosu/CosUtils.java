package kr.co.inno.autocash.cosu;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInstaller;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Utility class for various operations necessary during COSU set up.
 */
class CosuUtils {
    public static final String TAG = "CosuSetup";
    public static final boolean DEBUG = false;

    public static final int MSG_DOWNLOAD_COMPLETE = 1;
    public static final int MSG_DOWNLOAD_TIMEOUT = 2;
    public static final int MSG_INSTALL_COMPLETE = 3;

    private static final int DOWNLOAD_TIMEOUT_MILLIS = 120_000;

    public static final String ACTION_INSTALL_COMPLETE = "com.afwsamples.testdpc.INSTALL_COMPLETE";

    public static Long startDownload(DownloadManager dm, Handler handler, String location) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(location));
        Long id = dm.enqueue(request);
        handler.sendMessageDelayed(handler.obtainMessage(MSG_DOWNLOAD_TIMEOUT, id),
                DOWNLOAD_TIMEOUT_MILLIS);
        if (DEBUG) Log.d(TAG, "Starting download: DownloadId=" + id);
        return id;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static boolean installPackage(Context context, InputStream in, String packageName)
            throws IOException {
        PackageInstaller packageInstaller = context.getPackageManager().getPackageInstaller();
        PackageInstaller.SessionParams params = null;
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
            params = new PackageInstaller.SessionParams(
                    PackageInstaller.SessionParams.MODE_FULL_INSTALL);
        }
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
            params.setAppPackageName(packageName);
        }
        // set params
        int sessionId = packageInstaller.createSession(params);
        PackageInstaller.Session session = packageInstaller.openSession(sessionId);
        OutputStream out = session.openWrite("COSU", 0, -1);
        byte[] buffer = new byte[65536];
        int c;
        while ((c = in.read(buffer)) != -1) {
            out.write(buffer, 0, c);
        }
        session.fsync(out);
        in.close();
        out.close();

        session.commit(createIntentSender(context, sessionId));
        return true;
    }

    private static IntentSender createIntentSender(Context context, int sessionId) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                sessionId,
                new Intent(ACTION_INSTALL_COMPLETE),
                0);
        return pendingIntent.getIntentSender();
    }
}