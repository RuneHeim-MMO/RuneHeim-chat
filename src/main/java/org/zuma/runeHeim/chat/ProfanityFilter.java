package org.zuma.runeHeim.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

public class ProfanityFilter {

    // store bad words as normalized lower-case strings (no spaces)
    private static final Map<String, String[]> badWords = new HashMap<>();
    private static int largestWordLength = 0;

    // leetspeak map: char in input -> normalized letter
    private static final Map<Character, Character> LEET_MAP = new HashMap<>();

    static {
        LEET_MAP.put('0', 'o');
        LEET_MAP.put('1', 'i');
        LEET_MAP.put('!', 'i');
        LEET_MAP.put('3', 'e');
        LEET_MAP.put('4', 'a');
        LEET_MAP.put('@', 'a');
        LEET_MAP.put('$', 's');
        LEET_MAP.put('5', 's');
        LEET_MAP.put('6', 'g');
        LEET_MAP.put('7', 't');
        LEET_MAP.put('9', 'g');
        LEET_MAP.put('+', 't');
        LEET_MAP.put('(', 'c');
        LEET_MAP.put(')', 'd');
        // add more as desired
    }

    /**
     * Load CSV of bad words; CSV first column is word, optional second column underscore-separated ignore words.
     */
    public static void loadConfigs() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new URL("https://docs.google.com/spreadsheets/d/1hIEi2YG3ydav1E06Bzf2mQbGZ12kh2fe4ISgLg_UBuM/export?format=csv")
                        .openConnection().getInputStream()))) {
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] cols = line.split(",", -1);
                String word = cols[0].trim().toLowerCase().replaceAll("\\s+", "");
                if (word.isEmpty()) continue;
                String[] ignore = new String[0];
                if (cols.length > 1 && !cols[1].trim().isEmpty()) {
                    ignore = cols[1].trim().toLowerCase().split("_");
                }
                flag(word, ignore);
                count++;
            }
            System.out.println("Loaded " + count + " bad words");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Manually add a bad word and optional ignore words (ignore words are normalized too)
     */
    public static void flag(String word, String... ignoreWith) {
        if (word == null || word.isEmpty()) return;
        String normalized = word.toLowerCase().replaceAll("\\s+", "");
        String[] normalizedIgnore = Arrays.stream(ignoreWith == null ? new String[0] : ignoreWith)
                .filter(Objects::nonNull)
                .map(s -> s.toLowerCase().replaceAll("\\s+", ""))
                .toArray(String[]::new);

        badWords.put(normalized, normalizedIgnore);
        largestWordLength = Math.max(largestWordLength, normalized.length());
    }

    /**
     * Normalize input into letters-only sequence and map each normalized char back to original index.
     * Returns pair: normalized string, list of original indices (same length as normalized).
     */
    private static Normalized buildNormalizedAndIndexMap(String input) {
        StringBuilder norm = new StringBuilder(input.length());
        IntList origIdx = new IntList(input.length()); // simple int list (see below)
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            char lower = Character.toLowerCase(c);
            // if it's a letter => keep mapped letter
            if (Character.isLetter(lower)) {
                norm.append(lower);
                origIdx.add(i);
                continue;
            }
            // if it's in leet map => map and keep
            Character mapped = LEET_MAP.get(lower);
            if (mapped != null && Character.isLetter(mapped)) {
                norm.append(mapped);
                origIdx.add(i);
                continue;
            }
            // else skip (spaces, punctuation not used for leetspeak) so matches can cross them
        }
        return new Normalized(norm.toString(), origIdx.toArray());
    }

    /**
     * Censor bad words and return censored string.
     * Replaces full original-range (from first orig index to last orig index of match) with asterisks.
     */
    public static Map.Entry<String, Boolean> censorBadWords(String input) {
        if (input == null || input.isEmpty()) return Map.entry("", false);

        boolean containsProfanity = false;
        Normalized n = buildNormalizedAndIndexMap(input);
        String normalized = n.normalized;
        int[] origIdx = n.origToOriginalIndex;

        char[] out = input.toCharArray();
        int normLen = normalized.length();

        // scan normalized string for bad words
        for (int start = 0; start < normLen; start++) {
            // limit by largestWordLength and remaining length
            int maxEnd = Math.min(normLen, start + largestWordLength);
            for (int end = start + 1; end <= maxEnd; end++) {
                String sub = normalized.substring(start, end);
                String[] ignore = badWords.get(sub);
                if (ignore == null) continue;

                // check ignore list: if any ignore string appears in normalized (anywhere), then skip censoring
                boolean shouldIgnore = false;
                for (String ig : ignore) {
                    if (ig == null || ig.isEmpty()) continue;
                    if (normalized.contains(ig)) {
                        shouldIgnore = true;
                        break;
                    }
                }
                if (shouldIgnore) continue;

                // map back to original indices and replace full range
                int origStart = origIdx[start];
                int origEnd = origIdx[end - 1];

                // replace characters in original string between origStart..origEnd inclusive
                for (int i = origStart; i <= origEnd && i < out.length; i++) {
                    out[i] = '*';
                    containsProfanity = true;
                }
                // advance start to end-1 to avoid overlapping checks that would be redundant
                start = end - 1;
                break; // move to next start
            }
        }

        return Map.entry(new String(out), containsProfanity);
    }

    // small helper holder class
    private static class Normalized {
        final String normalized;
        final int[] origToOriginalIndex;

        Normalized(String normalized, int[] idx) {
            this.normalized = normalized;
            this.origToOriginalIndex = idx;
        }
    }

    // tiny dynamic int array without external deps
    private static class IntList {
        private int[] a;
        private int size = 0;

        IntList(int initial) {
            a = new int[Math.max(8, initial)];
        }

        void add(int v) {
            if (size == a.length) a = Arrays.copyOf(a, a.length * 2);
            a[size++] = v;
        }

        int[] toArray() {
            return Arrays.copyOf(a, size);
        }
    }
}
