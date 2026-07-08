package util;

// protects the system from bad inputs
public class Validator {
    public static boolean isNonEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        return isNonEmpty(email) && email.matches("^[^ \\s@]+@[^ \\s@]*\\.\\w+$");
    }

    public static boolean isPositive(double number) {
        return number > 0;
    }

    public static boolean isValidDays(int days) {
        return days > 0;
    }

    public static boolean passwordsMatch(String p1, String p2) {
        return p1.equals(p2);
    }
}