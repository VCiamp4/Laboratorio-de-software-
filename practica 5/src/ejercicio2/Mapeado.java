package ejercicio2;

import ejercicio2.AlmacenarAtributo;
import ejercicio2.Archivo;

@Archivo(nombre = "ArchivoMapeado.txt")
public class Mapeado {

    @AlmacenarAtributo
    private String valor = "Default1";

    @AlmacenarAtributo
    private Integer valor2 = 20;

    @AlmacenarAtributo
    private Float valor3 = 30.20f;

    private Float valor4 = 50.5f; // no se almacena
}

