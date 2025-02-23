if [ -z $1 ]
then
        postfix=_postgres
else
        postfix=$1
fi
backuptime=$(date +%Y%m%d_%H%M%S)

echo "Backup/home-monitor/${backuptime}$postfix  1896	/tmp/home-monitor-backup/backup.tar.gz"
