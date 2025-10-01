package ejercicio3;

import java.util.*;

public class QGramMatcher {


    private final Map<String, Set<String>> index = new HashMap<>();

    public void addWord(String word) {
        for (String g : qgrams2(word)) {
            index.computeIfAbsent(g, k -> new HashSet<>()).add(word);
        }
    }


    public void addAll(Collection<String> words) {
        for (String w : words) addWord(w);
    }

    public Map<String, List<String>> asListMap() {
        Map<String, List<String>> out = new HashMap<>();
        for (var e : index.entrySet()) {
            List<String> lst = new ArrayList<>(e.getValue());
            Collections.sort(lst);
            out.put(e.getKey(), Collections.unmodifiableList(lst));
        }
        return Collections.unmodifiableMap(out);
    }


    public List<String> searchSimilar(Map<String, List<String>> gramDict, String query) {

        Set<String> candidates = new HashSet<>();
        for (String g : qgrams2(query)) {
            List<String> bucket = gramDict.get(g);
            if (bucket != null) candidates.addAll(bucket);
        }


        final int MAX_DIST = 2;
        List<Result> passed = new ArrayList<>();
        for (String cand : candidates) {
            int d = boundedLevenshtein(cand, query, MAX_DIST);
            if (d <= MAX_DIST) {
                passed.add(new Result(cand, d));
            }
        }

        passed.sort(Comparator.comparingInt((Result r) -> r.dist)
                .thenComparing(r -> r.word));

        List<String> out = new ArrayList<>(passed.size());
        for (Result r : passed) out.add(r.word);
        return out;
    }

    private static List<String> qgrams2(String word) {
        String w = "#" + word + "$";
        List<String> grams = new ArrayList<>(Math.max(0, w.length() - 1));
        for (int i = 0; i < w.length() - 1; i++) {
            grams.add(w.substring(i, i + 2));
        }
        return grams;
    }


    public static int boundedLevenshtein(String a, String b, int maxDist) {
        // Normalización opcional: descomentar si querés case-insensitive
        // a = a.toLowerCase(); b = b.toLowerCase();

        int n = a.length();
        int m = b.length();

        // Diferencia de longitudes ya excede el umbral => imposible estar <= maxDist
        if (Math.abs(n - m) > maxDist) return maxDist + 1;

        // Garantizar que a sea la corta (ahorra memoria)
        if (n > m) { String tmp = a; a = b; b = tmp; n = a.length(); m = b.length(); }

        // DP con banda: solo se calcula j en [i-maxDist, i+maxDist]
        int[] prev = new int[m + 1];
        int[] curr = new int[m + 1];

        // Inicialización de la primera fila con banda
        for (int j = 0; j <= m; j++) {
            prev[j] = (j <= maxDist) ? j : (maxDist + 1);
        }

        for (int i = 1; i <= n; i++) {
            int from = Math.max(1, i - maxDist);
            int to   = Math.min(m, i + maxDist);

            // Inicializar fuera de banda con > maxDist
            Arrays.fill(curr, maxDist + 1);

            // Costo de borrar hasta i (si está dentro de la banda)
            if (from == 1) curr[0] = (i <= maxDist) ? i : (maxDist + 1);

            for (int j = from; j <= to; j++) {
                int cost = (a.charAt(i - 1) == b.charAt(j - 1)) ? 0 : 1;
                int del = prev[j] + 1;      // borrar en a
                int ins = curr[j - 1] + 1;  // insertar en a
                int sub = prev[j - 1] + cost; // sustituir
                int v = Math.min(sub, Math.min(del, ins));
                curr[j] = v;
            }

            // Early exit: si en esta fila todo > maxDist, ya no puede mejorar
            boolean allLarge = true;
            for (int j = from; j <= to; j++) {
                if (curr[j] <= maxDist) { allLarge = false; break; }
            }
            if (allLarge) return maxDist + 1;

            // swap filas
            int[] tmp = prev; prev = curr; curr = tmp;
        }

        int dist = prev[m];
        return dist;
    }

    private static class Result {
        final String word;
        final int dist;
        Result(String w, int d) { this.word = w; this.dist = d; }
    }

    // ------------------- Demo -------------------

    public static void main(String[] args) {
        QGramMatcher matcher = new QGramMatcher();
        matcher.addAll(List.of(
                "casa", "caso", "casita", "calle", "costa", "cama", "cana",
                "Glucosa", "Glutosa", "Fructosa", "Glosa"
        ));

        Map<String, List<String>> dict = matcher.asListMap();

        // Ejemplos de búsqueda:
        runQuery(matcher, dict, "caza");     // a 2 ediciones de "casa"
        runQuery(matcher, dict, "calle");    // exacta, y cercanas
        runQuery(matcher, dict, "glucisa");  // cerca de "Glucosa"
    }

    private static void runQuery(QGramMatcher matcher, Map<String, List<String>> dict, String q) {
        List<String> res = matcher.searchSimilar(dict, q);
        System.out.println("Query: " + q + " -> " + res);
    }
}

