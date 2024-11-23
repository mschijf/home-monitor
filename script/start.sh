program="home-monitor"
path="home-monitor"
port="8080"

cd ~/$path
bash ./script/stop.sh
program_version=`ls -1 $program*.jar | tail -1`
java -Dspring.profiles.active=prod -Dserver.port=$port -jar $program_version >> ./log/$program-log 2>> ./log/$program-log &
