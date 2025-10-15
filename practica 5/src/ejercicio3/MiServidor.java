package ejercicio3;

@Servidor(
        direccion = "127.0.0.1",      // IP local
        puerto = 8080,                // puerto donde escucha
        archivo = "log_servidor.txt"  // archivo donde se guardan las conexiones
)
public class MiServidor {

    @Invocar
    public void atenderCliente() {
        System.out.println("Atendiendo cliente...");
    }

    @Invocar
    public void enviarMensaje() {
        System.out.println("Enviando mensaje al cliente...");
    }

    // Método sin anotación -> no se invoca automáticamente
    public void metodoInterno() {
        System.out.println("Este método no se ejecuta automáticamente.");
    }
}
