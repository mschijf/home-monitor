backuptime=$(date +%Y%m%d_%H%M%S)

cd ~/home-monitor/backup
docker exec -i home-monitor /usr/local/bin/pg_dump -U home-monitor-app home-monitor > backup_postgres.sql
tar -czf backup_postgres.tar.gz ./backup_postgres.sql
rm backup_postgres.sql

echo -n $(date '+%Y-%m-%d %H:%M:%S')  
echo -n '  '
du -h0 ../backup/backup_postgres.tar.gz
echo -n '  '
~/dropbox-uploader/dropbox_uploader.sh upload ~/home-monitor/backup/backup_postgres.tar.gz Backup/home-monitor/${backuptime}_postgres
~/home-monitor/script/cleanup_backup.sh _postgres 168

