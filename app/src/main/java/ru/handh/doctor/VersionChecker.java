package ru.handh.doctor;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import org.jsoup.Jsoup;

import java.io.IOException;

/**
 * Created by samsonov on 21.07.2016.
 */
public class VersionChecker extends AsyncTask<String, String, String> {

    public interface Callback {
        void onComplete(boolean needUpdate, String newVersion);
    }

    private Callback callback;

    private String oldVersion;

    private String packageName;

    public VersionChecker(Callback callback) {
        this.callback = callback;

        packageName = MyApplication.appContext.getPackageName();
        try {
            PackageInfo pInfo = MyApplication.appContext.getPackageManager().getPackageInfo(packageName, 0);
            oldVersion = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String doInBackground(String... params) {
        String newVersion=null;
        try {
            newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + packageName + "&hl=en")
                    .timeout(30000)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get()
                    .select("div[itemprop=softwareVersion]")
                    .first()
                    .ownText();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return newVersion;
    }

    @Override
    protected void onPostExecute(String newVersion) {
        boolean needUpdate = newVersion!=null && newVersion.compareTo(oldVersion)>0;
        callback.onComplete(needUpdate, newVersion);
    }
}
