backuptime=$(date +%Y%m%d_%H%M%S)
echo starting backup at $backuptime

cd ~/home-monitor/backup
tar -czf backup_prom.tar.gz /var/lib/prometheus/
~/dropbox_uploader.sh upload ~/home-monitor/backup/backup_prom.tar.gz Backup/home-monitor/${backuptime}_prometheus

echo clean up old backup files
~/home-monitor/script/cleanup_backup.sh _data
~/home-monitor/script/cleanup_backup.sh _prometheus

echo ================================
