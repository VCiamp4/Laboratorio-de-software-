package ejercicio4;

public class Alumno {
    private int nro;
    private String nombre;
    private String apellidos;
    private int edad;
    private boolean MateriaAprobada;
    private int nota;
    private String curso;

    public Alumno(int nro, String nombre, String apellidos, int edad, boolean MateriaAprobada, int nota,String curso) {
        this.nro = nro;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.edad = edad;
        this.MateriaAprobada = MateriaAprobada;
        this.nota = nota;
        this.curso = curso;
    }

    public int getNro() {
        return nro;
    }
    public String getNombre() {
        return nombre;
    }
    public String getApellidos() {
        return apellidos;
    }
    public int getEdad() {
        return edad;
    }
    public boolean isMateriaAprobada() {
        return MateriaAprobada;
    }

    public String getCurso() {
        return curso;
    }

    public int getNota() {
        return nota;
    }
}
