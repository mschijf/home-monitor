if [ -z $1 ]
then
	echo "Need postfix input parameter"
	exit 0
fi

if [ -z $2 ]
then
	echo "Need keep (how many files with 'postfix' to keep) input parameter"
	exit 0
fi

postfix=$1
keep=$2
tmpFile=/tmp/list_data
~/dropbox_uploader.sh list Backup/home-monitor/ | grep $postfix > $tmpFile

totalLines=$(cat $tmpFile  | wc -l)
removeLines=$(($totalLines - $keep))
if [ $removeLines -gt 0 ]
then
	cat $tmpFile | awk {'print $3'} | sort | head -$removeLines | while read line
	do
		~/dropbox_uploader.sh delete Backup/home-monitor/$line
	done
fi
