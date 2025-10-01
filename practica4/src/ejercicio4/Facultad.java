package ejercicio4;

import java.util.List;
import java.util.stream.Collectors;

public class Facultad {
    private List<Alumno> Alumnos;

    public Facultad(List<Alumno> Alumnos) {
        this.Alumnos = Alumnos;
    }

    public List<Alumno> getAlumnos() {
        return Alumnos;
    }


    public Alumno maxNota(){
        return Alumnos.stream().max((a,b) -> a.getNota() - b.getNota()).get();
    }

    public List<Alumno> dosEstudiantes(){
        return Alumnos.stream().limit(2).toList();
    }

    public Alumno tomoLaboratorio(){
        return Alumnos.stream().filter(a -> a.getCurso().equals("Laboratorio")).findFirst().get();
    }

    public List<Alumno> AlumnosConP(){
        return Alumnos.stream().filter(a -> a.getNombre().startsWith("P"))
                .filter(a -> a.getNombre().length() <= 6)
                .collect(Collectors.toList());
    }

    
}
