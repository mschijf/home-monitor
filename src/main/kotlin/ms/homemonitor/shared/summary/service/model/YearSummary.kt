package ms.homemonitor.shared.summary.service.model

data class YearSummary(
    val actualPreviousYear: Double,
    val actualYTD: Double,
    val actualYTDPreviousYear: Double,
    val remainderPreviousYear: Double,
    val prognose: Prognose,
)

data class Prognose(
    val followPreviousYear: Double,
    val extrapolate: Double,
    val followTrendYTD: Double,
    val followTrend28Days: Double,
    val followTrendWeighted: Double,
    val followTrendRollingYear: Double,
)