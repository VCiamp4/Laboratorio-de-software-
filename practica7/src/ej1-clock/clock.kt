package `ej1-clock`
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class clock {

    fun main() {
        var activo = true

        val reloj = thread(start = true) {
            val formato = DateTimeFormatter.ofPattern("HH:mm:ss")
            while (activo) {
                val horaActual = LocalTime.now().format(formato)
                println("hora actual:  $horaActual ")
                TimeUnit.SECONDS.sleep(1)
            }
        }

        Thread.sleep(5000)
        activo = false
        reloj.join()
        println("reloj detenido")
    }
}