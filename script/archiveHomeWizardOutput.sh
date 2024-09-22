cd ~/home-monitor
tar -czf history/$(date +%Y%m%d_%H%M%S)_homeWizardOutput.tar.gz data/homeWizardOutput.csv
rm data/homeWizardOutput.csv
