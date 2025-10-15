package ejercicio3;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Contenedor {

    public static void iniciar(Class<?> claseServidor) {
        // 1) Validar anotación @Servidor en la clase
        if (!claseServidor.isAnnotationPresent(Servidor.class)) {
            throw new IllegalArgumentException("La clase no está anotada con @Servidor");
        }
        Servidor cfg = claseServidor.getAnnotation(Servidor.class);

        String ip = cfg.direccion();
        int puerto = cfg.puerto();
        Path logPath = Paths.get(cfg.archivo());

        // 2) Crear instancia del "servidor" anotado
        Object instancia = crearInstancia(claseServidor);

        // 3) Preparar lista de métodos @Invocar (sin parámetros)
        Method[] metodosInvocar = filtrarMetodosInvocar(claseServidor);

        // 4) Abrir socket en IP/puerto
        try (ServerSocket server = new ServerSocket(puerto, 50, InetAddress.getByName(ip))) {
            System.out.printf("Servidor escuchando en http://%s:%d%n", ip, puerto);

            while (true) {
                Socket cliente = server.accept();
                // 4.a Loguear fecha/hora e IP del cliente
                logConexion(logPath, cliente);

                // 4.b Invocar todos los métodos @Invocar
                String[] ejecutados = invocarMetodos(instancia, metodosInvocar);

                // 4.c Responder HTTP simple (para probar con navegador)
                responderHTTP(cliente, ejecutados);

                cliente.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error de E/S del servidor", e);
        }
    }

    private static Object crearInstancia(Class<?> clazz) {
        try {
            Constructor<?> c = clazz.getDeclaredConstructor();
            c.setAccessible(true);
            return c.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("No se pudo instanciar la clase del servidor", e);
        }
    }

    private static Method[] filtrarMetodosInvocar(Class<?> clazz) {
        return java.util.Arrays.stream(clazz.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(Invocar.class))
                .peek(m -> m.setAccessible(true))
                .toArray(Method[]::new);
    }

    private static void logConexion(Path logPath, Socket cliente) {
        String ipCliente = cliente.getInetAddress().getHostAddress();
        String fecha = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String linea = String.format("%s - %s%n", fecha, ipCliente);
        try {
            Files.writeString(
                    logPath, linea,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            System.err.println("No se pudo escribir el log: " + e.getMessage());
        }
    }

    private static String[] invocarMetodos(Object instancia, Method[] metodos) {
        java.util.List<String> nombres = new java.util.ArrayList<>();
        for (Method m : metodos) {
            // Solo invocar si no tiene parámetros
            if (m.getParameterCount() == 0) {
                try {
                    m.invoke(instancia);
                    nombres.add(m.getName());
                } catch (Exception e) {
                    System.err.println("Fallo invocando " + m.getName() + ": " + e.getMessage());
                }
            }
        }
        return nombres.toArray(new String[0]);
    }

    private static void responderHTTP(Socket cliente, String[] ejecutados) {
        try (OutputStream os = cliente.getOutputStream();
             PrintWriter out = new PrintWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8), true)) {

            String body = "<html><body>" +
                    "<h1>Servidor activo</h1>" +
                    "<p>Métodos @Invocar ejecutados: " + String.join(", ", ejecutados) + "</p>" +
                    "</body></html>";

            out.print("HTTP/1.1 200 OK\r\n");
            out.print("Content-Type: text/html; charset=UTF-8\r\n");
            out.print("Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n");
            out.print("Connection: close\r\n");
            out.print("\r\n");
            out.print(body);
            out.flush();
        } catch (IOException e) {
            System.err.println("No se pudo responder al cliente: " + e.getMessage());
        }
    }
}
