package ms.homemonitor.shared.tools.commandline

import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

@Service
class CommandExecutor {


    @Throws(IOException::class)
    fun execCommand(cmd: String): List<String> {
        val cmdParts = cmd.split("\\s+".toRegex())
        val rt = Runtime.getRuntime()
        val proc = rt.exec(cmdParts.toTypedArray())

        val stdInput = BufferedReader(InputStreamReader(proc.inputStream)).lines().toList()
        val stdError = BufferedReader(InputStreamReader(proc.errorStream)).lines().toList()

        if (stdError.isNotEmpty()) {
            throw IOException(stdError.joinToString("\n"))
        }

        return stdInput
    }
}