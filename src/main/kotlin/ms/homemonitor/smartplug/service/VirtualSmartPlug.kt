package ms.homemonitor.smartplug.service

import java.math.BigDecimal

class VirtualSmartPlug() {
    companion object {
        val virtualDeviceList = listOf(
            VirtualDevice("WTW", BigDecimal.valueOf(44.25)),
            VirtualDevice("Meterkast (IoT)", BigDecimal.valueOf(18.16)),
        )
    }
}

data class VirtualDevice(val name: String, val wattHourPerHour: BigDecimal)