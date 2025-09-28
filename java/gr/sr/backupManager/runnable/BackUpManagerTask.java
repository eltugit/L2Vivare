package gr.sr.backupManager.runnable;

import gr.sr.backupManager.DatabaseBackupManager;

public class BackUpManagerTask implements Runnable {
    public BackUpManagerTask() {
    }

    public void run() {
        DatabaseBackupManager.makeBackup();
    }
}
