cd ~/home-monitor
tar -czf history/$(date +%Y%m%d_%H%M%S)_smartMeterOutput.tar.gz data/smartMeterOutput.csv
rm data/smartMeterOutput.csv
