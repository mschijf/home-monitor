package ms.homemonitor.infra.homewizard.model

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.time.LocalDateTime

// see https://api-documentation.homewizard.com/docs/endpoints/api-v1-data/

data class HomeWizardEnergyData(
    @JsonProperty("time")
    val time: LocalDateTime = LocalDateTime.now(),

    @JsonProperty(value = "wifi_strength")
    @JsonAlias("wifiStrength")
    val wifiStrength: Int,

    @JsonProperty(value = "smr_version")
    @JsonAlias("smrVersion")
    val smrVersion: Int,

    @JsonProperty(value = "meter_model")
    @JsonAlias("meterModel")
    val meterModel: String,

    @JsonProperty(value = "unique_id")
    @JsonAlias("uniqueId")
    val uniqueId: String,

    @JsonProperty(value = "active_tariff")
    @JsonAlias("activeTariff")
    val activeTariff: Int,

    @JsonProperty(value = "total_power_import_kwh")
    @JsonAlias("totalPowerImportKwh")
    val totalPowerImportKwh: BigDecimal,

    @JsonProperty(value = "total_power_import_t1_kwh")
    @JsonAlias("totalPowerImportT1Kwh")
    val totalPowerImportT1Kwh: BigDecimal,

    @JsonProperty(value = "total_power_import_t2_kwh")
    @JsonAlias("totalPowerImportT2Kwh")
    val totalPowerImportT2Kwh: BigDecimal,

    @JsonProperty(value = "total_power_export_kwh")
    @JsonAlias("totalPowerExportKwh")
    val totalPowerExportKwh: BigDecimal,

    @JsonProperty(value = "total_power_export_t1_kwh")
    @JsonAlias("totalPowerExportT1Kwh")
    val totalPowerExportT1Kwh: BigDecimal,

    @JsonProperty(value = "total_power_export_t2_kwh")
    @JsonAlias("totalPowerExportT2Kwh")
    val totalPowerExportT2Kwh: BigDecimal,

    @JsonProperty(value = "active_power_w")
    @JsonAlias("activePowerWatt")
    val activePowerWatt: BigDecimal,

    @JsonProperty(value = "active_power_l1_w")
    @JsonAlias("activePowerL1Watt")
    val activePowerL1Watt: BigDecimal,

    @JsonProperty(value = "active_power_l2_w")
    @JsonAlias("activePowerL2Watt")
    val activePowerL2Watt: BigDecimal,

    @JsonProperty(value = "active_power_l3_w")
    @JsonAlias("activePowerL3Watt")
    val activePowerL3Watt: BigDecimal,

    @JsonProperty(value = "active_voltage_l1_v")
    @JsonAlias("activeVoltageL1Volt")
    val activeVoltageL1Volt: BigDecimal,

    @JsonProperty(value = "active_voltage_l2_v")
    @JsonAlias("activeVoltageL2Volt")
    val activeVoltageL2Volt: BigDecimal,

    @JsonProperty(value = "active_voltage_l3_v")
    @JsonAlias("activeVoltageL3Volt")
    val activeVoltageL3Volt: BigDecimal,

    @JsonProperty(value = "active_current_l1_a")
    @JsonAlias("activeCurrentL1Ampere")
    val activeCurrentL1Ampere: BigDecimal,

    @JsonProperty(value = "active_current_l2_a")
    @JsonAlias("activeCurrentL2Ampere")
    val activeCurrentL2Ampere: BigDecimal,

    @JsonProperty(value = "active_current_l3_a")
    @JsonAlias("activeCurrentL3Ampere")
    val activeCurrentL3Ampere: BigDecimal,

    @JsonProperty(value = "voltage_sag_l1_count")
    @JsonAlias("voltageSagL1Count")
    val voltageSagL1Count: Int,

    @JsonProperty(value = "voltage_sag_l2_count")
    @JsonAlias("voltageSagL2Count")
    val voltageSagL2Count: Int,

    @JsonProperty(value = "voltage_sag_l3_count")
    @JsonAlias("voltageSagL3Count")
    val voltageSagL3Count: Int,

    @JsonProperty(value = "voltage_swell_l1_count")
    @JsonAlias("voltageSwellL1Count")
    val voltageSwellL1Count: Int,

    @JsonProperty(value = "voltage_swell_l2_count")
    @JsonAlias("voltageSwellL2Count")
    val voltageSwellL2Count: Int,

    @JsonProperty(value = "voltage_swell_l3_count")
    @JsonAlias("voltageSwellL3Count")
    val voltageSwellL3Count: Int,

    @JsonProperty(value = "any_power_fail_count")
    @JsonAlias("anyPowerFailCount")
    val anyPowerFailCount: Int,

    @JsonProperty(value = "long_power_fail_count")
    @JsonAlias("longPowerFailCount")
    val longPowerFailCount: Int,
)


//
//{
//    "wifi_ssid": "MartinsKPN",
//    "wifi_strength": 100,
//    "smr_version": 50,
//    "meter_model": "ISKRA 2M550T-1013",
//    "unique_id": "4530303534303037373638303537303230",
//    "active_tariff": 1,
//    "total_power_import_kwh": 5130.155,
//    "total_power_import_t1_kwh": 2111.962,
//    "total_power_import_t2_kwh": 3018.193,
//    "total_power_export_kwh": 0,
//    "total_power_export_t1_kwh": 0,
//    "total_power_export_t2_kwh": 0,
//    "active_power_w": 141,
//    "active_power_l1_w": 16,
//    "active_power_l2_w": 61,
//    "active_power_l3_w": 63,
//    "active_voltage_l1_v": 236.5,
//    "active_voltage_l2_v": 234.3,
//    "active_voltage_l3_v": 235.6,
//    "active_current_l1_a": 0.068,
//    "active_current_l2_a": 0.26,
//    "active_current_l3_a": 0.267,
//    "voltage_sag_l1_count": 9,
//    "voltage_sag_l2_count": 11,
//    "voltage_sag_l3_count": 12,
//    "voltage_swell_l1_count": 2,
//    "voltage_swell_l2_count": 1,
//    "voltage_swell_l3_count": 2,
//    "any_power_fail_count": 5,
//    "long_power_fail_count": 3,
//    "external": []
//}