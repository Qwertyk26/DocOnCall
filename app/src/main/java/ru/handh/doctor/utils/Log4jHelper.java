package ru.handh.doctor.utils;

import android.os.Environment;

import org.apache.log4j.Level;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.mindpipe.android.logging.log4j.LogConfigurator;

/**
 * Created by antonnikitin on 14.10.16.
 */

public class Log4jHelper {
    private final static LogConfigurator mLogConfigrator = new LogConfigurator();

    static {
        configureLog4j();
    }

    private static void configureLog4j() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        String fileName = Environment.getExternalStorageDirectory() + File.separator + "logs/" + timeStamp + "_doconcall.log";
        String filePattern = "%d %-5p [%c{2}]-[%L] %m%n";
        int maxBackupSize = Constants.MAX_BACKUP_FILE;
        long maxFileSize = Constants.MAX_FILE_SIZE;
        configure(fileName, filePattern, maxBackupSize, maxFileSize);
    }

    private static void configure(String fileName, String filePattern, int maxBackupSize, long maxFileSize) {
//        String path = Environment.getExternalStorageDirectory() + File.separator + "logs/";
//        File directory = new File(path);
//        File[] files = directory.listFiles();
//        for (int i = 0; i < files.length; i++) {
//            if (files[i].getTotalSpace() > 1024) {
                mLogConfigrator.setFileName(fileName);
                mLogConfigrator.setMaxFileSize(maxFileSize);
                mLogConfigrator.setFilePattern(filePattern);
                mLogConfigrator.setMaxBackupSize(maxBackupSize);
                mLogConfigrator.setUseLogCatAppender(true);
                mLogConfigrator.setRootLevel(Level.ALL);
                mLogConfigrator.setImmediateFlush(true);
                mLogConfigrator.setUseFileAppender(true);
                mLogConfigrator.configure();
         //   }
        //}
    }

    public static org.apache.log4j.Logger getLogger(String name) {
        org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(name);
        return logger;
    }
}
