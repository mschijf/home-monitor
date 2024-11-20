backuptime=$(date +%Y%m%d_%H%M%S)
echo starting backup at $backuptime

cd ~/home-monitor/backup
docker exec -i home-monitor /usr/bin/pg_dump -U home-monitor-app home-monitor > backup_postgres.sql
tar -czf backup_postgres.tar.gz ./backup_postgres.sql
rm backup_postgres.sql


~/dropbox_uploader.sh upload ~/home-monitor/backup/backup_postgres.tar.gz Backup/home-monitor/${backuptime}_postgres
echo clean up old backup files
~/home-monitor/script/cleanup_backup.sh _postgres

echo ================================
