import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;
import java.util.stream.*;

public class Main {

    // Regex patterns for password rules
    private static final Pattern UPPERCASE = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE = Pattern.compile("[a-z]");
    private static final Pattern DIGIT = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL = Pattern.compile("[^a-zA-Z0-9]");

    // Evaluate a single password
    public static String evaluatePassword(String password) {
        int score = 0;
        StringBuilder feedback = new StringBuilder("Suggestions: ");

        if (password.length() >= 12) score++;
        else feedback.append("Use at least 12 characters. ");

        if (UPPERCASE.matcher(password).find()) score++;
        else feedback.append("Add uppercase letters. ");

        if (LOWERCASE.matcher(password).find()) score++;
        else feedback.append("Add lowercase letters. ");

        if (DIGIT.matcher(password).find()) score++;
        else feedback.append("Include numbers. ");

        if (SPECIAL.matcher(password).find()) score++;
        else feedback.append("Include special symbols. ");

        String strength;
        switch (score) {
            case 5 -> strength = "🔥 Strong";
            case 3, 4 -> strength = "🟡 Medium";
            default -> strength = "🔴 Weak";
        }

        return String.format("%-20s | %-10s | %s", password, strength, feedback);
    }

    // Threaded execution for multiple passwords
    public static void analyzePasswords(List<String> passwords) {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        List<Future<String>> results = passwords.stream()
                .map(pwd -> executor.submit(() -> evaluatePassword(pwd)))
                .collect(Collectors.toList());

        results.forEach(f -> {
            try {
                System.out.println(f.get());
            } catch (Exception e) {
                System.err.println("Error evaluating password: " + e.getMessage());
            }
        });
        executor.shutdown();
    }

    public static void main(String[] args) {
        System.out.println("=== 🔐 Password Strength Analyzer ===\n");

        List<String> passwords = new ArrayList<>();

        try (Scanner sc = new Scanner(System.in)) {
            System.out.print("Enter number of passwords to analyze: ");
            int n = sc.nextInt();
            sc.nextLine();

            for (int i = 1; i <= n; i++) {
                System.out.print("Enter password " + i + ": ");
                passwords.add(sc.nextLine());
            }
        }

        System.out.println("\nAnalyzing...\n");
        analyzePasswords(passwords);
    }
}

