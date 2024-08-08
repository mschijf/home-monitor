package ms.homemonitor.infra.homewizard.model

data class HomeWizardData(
    val energy: HomeWizardEnergyData,
    val water: HomeWizardWaterData
)