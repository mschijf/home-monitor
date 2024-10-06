package ms.homemonitor.domain.eneco.model

import java.math.BigDecimal
import java.time.LocalDateTime

data class EnecoDataModel(
    val data: EnecoData
)

data class EnecoData(
    val usages: List<EnecoUsage>
)

data class EnecoUsage (
//    val period: Period,
    val entries: List<EnecoUsageEntry>
)

data class EnecoUsageEntry(
    val actual: EnecoActual
)

data class EnecoActual(
    val date: LocalDateTime,
    val warmth: EnecoWarmth,
    val totalUsageCostInclVat: BigDecimal,
    val totalFixedCostInclVat: BigDecimal
)

data class EnecoWarmth(
    val status: String,
    val high: BigDecimal,
    val low: BigDecimal,
    val highCostInclVat: BigDecimal,
    val lowCostInclVat: BigDecimal,
    val fixedCostInclVat: BigDecimal,
    val fixedCostInclVatStandingCharge: BigDecimal,
    val fixedCostInclVatDeliveryCosts: BigDecimal,
    val fixedCostInclVatTaxDiscount: BigDecimal,
)



