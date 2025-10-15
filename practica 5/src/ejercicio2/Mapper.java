package ejercicio2;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;

public class Mapper {

    public static void guardar(Object obj) {
        Class<?> clase = obj.getClass();
        String nombreArchivo = clase.getSimpleName() + ".txt";

        // Si la clase tiene la anotación @Archivo, usar su nombre
        if (clase.isAnnotationPresent(Archivo.class)) {
            Archivo a = clase.getAnnotation(Archivo.class);
            if (!a.nombre().isEmpty()) {
                nombreArchivo = a.nombre();
            }
        }

        try (FileWriter writer = new FileWriter(nombreArchivo)) {

            writer.write("<nombreClase>" + clase.getSimpleName() + "</nombreClase>\n");

            for (Field f : clase.getDeclaredFields()) {
                if (f.isAnnotationPresent(AlmacenarAtributo.class)) {
                    f.setAccessible(true);
                    Object valor = f.get(obj);
                    writer.write("<nombreAtributo>" + f.getName() + "</nombreAtributo>\n");
                    writer.write("<nombreValor>" + valor + "</nombreValor>\n");
                }
            }

            System.out.println("Archivo generado: " + nombreArchivo);

        } catch (IOException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

