package ms.homemonitor.shared.summary.service.model

data class YearSummary(
    val actualPreviousYear: Double,
    val actualYTD: Double,
    val actualYTDPreviousYear: Double,
    val actualRunningYear: Double,
    val yearExpectationExtrapolate: Double,
    val yearExpectationComparedWithLastYear: Double) {
}