if [ $1 = "space" ]; then
  echo Dropbox Uploader v1.0
  echo
  echo
  echo Quota:	8576 Mb
  echo Used:	158 Mb
  echo Free:	1234 Mb
elif [ $1 = "list" ]; then
   echo "> Listing \"$2\"... DONE"
   echo " [F] 944427  20250105_060001_postgres"
   echo " [F] 945244  20250105_070001_postgres"
   echo " [F] 946089  20250105_080001_postgres"
   echo " [F] 946804  20250105_090001_postgres"
   echo " [F] 947577  20250105_100001_postgres"
elif [ $1 = "delete" ]; then
  echo " > Deleting \"$2\"... DONE"
fi
