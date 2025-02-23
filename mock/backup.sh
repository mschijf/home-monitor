if [ -z $1 ]
then
        postfix=_postgres
else
        postfix=$1
fi

backuptime=$(date +%Y%m%d_%H%M%S)
echo "Starting backup with postfix '$postfix'"
echo "2021-02-03 04:05:06  1892	backup.tar.gz"
echo " > Uploading \"/tmp/home-monitor-backup/backup.tar.gz\" to \"/Backup/home-monitor/${backuptime}${postfix}\"... DONE"
