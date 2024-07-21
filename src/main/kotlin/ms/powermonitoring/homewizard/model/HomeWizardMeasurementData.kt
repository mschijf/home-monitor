package ms.powermonitoring.homewizard.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

// see https://api-documentation.homewizard.com/docs/endpoints/api-v1-data/

data class HomeWizardMeasurementData(

    @JsonProperty(value = "wifi_strength")
    val wifiStrength: Int,
    @JsonProperty(value = "smr_version")
    val smrVersion: Int,
    @JsonProperty(value = "meter_model")
    val meterModel: String,
    @JsonProperty(value = "unique_id")
    val uniqueId: String,
    @JsonProperty(value = "active_tariff")
    val activeTarrif: Int,
    @JsonProperty(value = "total_power_import_kwh")
    val totalPowerImportKwh: BigDecimal,
    @JsonProperty(value = "total_power_import_t1_kwh")
    val totalPowerImportT1Kwh: BigDecimal,
    @JsonProperty(value = "total_power_import_t2_kwh")
    val totalPowerImportT2Kwh: BigDecimal,
    @JsonProperty(value = "total_power_export_kwh")
    val totalPowerExportKwh: BigDecimal,
    @JsonProperty(value = "total_power_export_t1_kwh")
    val totalPowerExportT1Kwh: BigDecimal,
    @JsonProperty(value = "total_power_export_t2_kwh")
    val totalPowerExportT2Kwh: BigDecimal,
    @JsonProperty(value = "active_power_w")
    val activePowerWatt: BigDecimal,
    @JsonProperty(value = "active_power_l1_w")
    val activePowerL1Watt: BigDecimal,
    @JsonProperty(value = "active_power_l2_w")
    val activePowerL2Watt: BigDecimal,
    @JsonProperty(value = "active_power_l3_w")
    val activePowerL3Watt: BigDecimal,
    @JsonProperty(value = "active_voltage_l1_v")
    val activeVoltageL1Volt: BigDecimal,
    @JsonProperty(value = "active_voltage_l2_v")
    val activeVoltageL2Volt: BigDecimal,
    @JsonProperty(value = "active_voltage_l3_v")
    val activeVoltageL3Volt: BigDecimal,
    @JsonProperty(value = "active_current_l1_a")
    val activeCurrentL1Ampere: BigDecimal,
    @JsonProperty(value = "active_current_l2_a")
    val activeCurrentL2Ampere: BigDecimal,
    @JsonProperty(value = "active_current_l3_a")
    val activeCurrentL3Ampere: BigDecimal,
    @JsonProperty(value = "voltage_sag_l1_count")
    val voltageSagL1Count: Int,
    @JsonProperty(value = "voltage_sag_l2_count")
    val voltageSagL2Count: Int,
    @JsonProperty(value = "voltage_sag_l3_count")
    val voltageSagL3Count: Int,
    @JsonProperty(value = "voltage_swell_l1_count")
    val voltageSwellL1Count: Int,
    @JsonProperty(value = "voltage_swell_l2_count")
    val voltageSwellL2Count: Int,
    @JsonProperty(value = "voltage_swell_l3_count")
    val voltageSwellL3Count: Int,
    @JsonProperty(value = "any_power_fail_count")
    val anyPowerFailCount: Int,
    @JsonProperty(value = "long_power_fail_count")
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