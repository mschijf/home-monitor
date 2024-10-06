package ms.homemonitor.domain.homewizard.model

data class HomeWizardData(
    val energy: HomeWizardEnergyData,
    val water: HomeWizardWaterData
)