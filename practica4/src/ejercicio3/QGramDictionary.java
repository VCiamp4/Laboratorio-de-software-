package ejercicio3;

import java.util.*;

public class QGramDictionary {
    private Map<String, Set<String>> dictionary = new HashMap<>();

    // Generar los q-gramas (q=2 por defecto)
    private List<String> getQGrams(String word, int q) {
        String padded = "#" + word + "$";
        List<String> grams = new ArrayList<>();
        for (int i = 0; i <= padded.length() - q; i++) {
            grams.add(padded.substring(i, i + q));
        }
        return grams;
    }

    // Agregar palabra al diccionario
    public void addWord(String word) {
        for (String g : getQGrams(word, 2)) {
            dictionary
                    .computeIfAbsent(g, k -> new HashSet<>())
                    .add(word);
        }
    }

    // Devolver como listas (por si el enunciado pide listas en vez de sets)
    public Map<String, List<String>> getDictionary() {
        Map<String, List<String>> result = new HashMap<>();
        for (var entry : dictionary.entrySet()) {
            result.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return result;
    }

    // Demo
    public static void main(String[] args) {
        QGramDictionary dict = new QGramDictionary();
        dict.addWord("Glucosa");
        dict.addWord("Glutosa");
        dict.addWord("Fructosa");
        dict.addWord("Glosa");

        Map<String, List<String>> map = dict.getDictionary();

        System.out.println("Grama 'Gl': " + map.get("Gl"));
        System.out.println("Grama 'os': " + map.get("os"));
        System.out.println("Grama 'a$': " + map.get("a$"));
    }
}

