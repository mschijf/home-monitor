if [ -z $1 ]
then
        postfix=_postgres
else
        postfix=$1
fi

if [ -z $2 ]
then
        keep=672
else
        keep=$2
fi

backuptime=$(date +%Y%m%d_%H%M%S)
echo "Starting backup with postfix '$postfix' and keeping last $keep backups"
echo "2021-02-03 04:05:06  1892	backup.tar.gz"
echo " > Uploading "/tmp/home-monitor-backup/backup.tar.gz" to "/Backup/home-monitor/${backuptime}${postfix}"... DONE"
#echo " > Deleting "/Backup/home-monitor/20250125_060001_${postfix}"... DONE"
#echo " > Deleting "/Backup/home-monitor/20250125_070001_${postfix}"... DONE"
#echo " > Deleting "/Backup/home-monitor/20250125_080001_${postfix}"... DONE"
#echo 0 or more deleting lines possible
