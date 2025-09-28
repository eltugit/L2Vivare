package gr.sr.backupManager;


import gr.sr.configsEngine.configs.impl.BackupManagerConfigs;
import l2r.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public final class DatabaseBackupManager {
    private static final Logger _log = LoggerFactory.getLogger(DatabaseBackupManager.class);

    public DatabaseBackupManager() {
    }

    
    public static void makeBackup() {
        _log.info("Initializing Backup Manager.");
        File file;
        if (!(file = new File(BackupManagerConfigs.DATABASE_BACKUP_SAVE_PATH)).mkdirs() && !file.exists()) {
            _log.info("Could not create folder " + file.getAbsolutePath());
        } else {
            Process process;
            try {
                process = Runtime.getRuntime().exec(BackupManagerConfigs.DATABASE_BACKUP_MYSQLDUMP_PATH + "/mysqldump --user=" + Config.DATABASE_LOGIN + " --password=" + Config.DATABASE_PASSWORD + " --compact --complete-insert --default-character-set=utf8 --extended-insert --lock-tables --quick --skip-triggers " + BackupManagerConfigs.DATABASE_BACKUP_DATABASE_NAME, (String[])null);
            } catch (Exception e) {
                _log.error(DatabaseBackupManager.class.getSimpleName() + ": Could not make backup: " + e.getMessage(), e);
                return;
            }

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
                Date date = new Date();
                if (!(file = new File(file, sdf.format(date) + (BackupManagerConfigs.DATABASE_BACKUP_COMPRESSION ? ".zip" : ".sql"))).createNewFile()) {
                    throw new IOException("Cannot create backup file: " + file.getCanonicalPath());
                } else {
                    InputStream is = process.getInputStream();
                    OutputStream fileOutputStream = new FileOutputStream(file);
                    if (BackupManagerConfigs.DATABASE_BACKUP_COMPRESSION) {
                        ZipOutputStream zipOutputStream;
                        (zipOutputStream = new ZipOutputStream(fileOutputStream)).setMethod(8);
                        zipOutputStream.setLevel(9);
                        zipOutputStream.setComment("L2jSunrise Schema Backup Utility\r\n\r\nBackup date: " + (new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss:SSS z")).format(date));
                        zipOutputStream.putNextEntry(new ZipEntry(BackupManagerConfigs.DATABASE_BACKUP_DATABASE_NAME + ".sql"));
                        fileOutputStream = zipOutputStream;
                    }

                    byte[] bytes = new byte[4096];

                    int i;
                    int y;
                    for(i = 0; (y = is.read(bytes)) != -1; i += y) {
                        fileOutputStream.write(bytes, 0, y);
                    }

                    is.close();
                    fileOutputStream.close();
                    if (i == 0) {
                        file.delete();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

                        String var10;
                        while((var10 = bufferedReader.readLine()) != null) {
                            _log.info(DatabaseBackupManager.class.getSimpleName() + ": " + var10);
                        }

                        bufferedReader.close();
                    } else {
                        _log.info(DatabaseBackupManager.class.getSimpleName() + ": DB `" + BackupManagerConfigs.DATABASE_BACKUP_DATABASE_NAME + "` backed up in " + (System.currentTimeMillis() - date.getTime()) / 1000L + " s.");
                    }

                    process.waitFor();
                }
            } catch (Exception var9) {
                _log.error(DatabaseBackupManager.class.getSimpleName() + ": Could not make backup: " + var9.getMessage(), var9);
            }
        }
    }
}
