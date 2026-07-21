package utilities;

/** Provides reusable input-validation rules without console or persistence responsibilities. */
public final class DataValidation {

    private static final String STUDENT_ID_PATTERN = "^(SE|HE|DE|QE|CE)\\d{6}$";
    private static final String PHONE_PATTERN = "^\\d{10}$";
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final String[] VIETNAM_PHONE_PREFIXES = {
        "086", "096", "097", "098", "032", "033", "034", "035", "036", "037", "038", "039",
        "088", "091", "094", "083", "084", "085", "081", "082",
        "089", "090", "093", "070", "079", "077", "076", "078",
        "092", "052", "056", "058",
        "099", "059", "087"
    };
    private static final String[] DISCOUNT_PREFIXES = {
        "086", "096", "097", "098", "032", "033", "034", "035", "036", "037", "038", "039",
        "088", "091", "094", "083", "084", "085", "081", "082"
    };

    private DataValidation() {
    }

    public static boolean checkNumberInMinMax(int number, int min, int max) {
        return number >= min && number <= max;
    }

    public static boolean checkStringEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean checkStringLengthInRange(String value, int min, int max) {
        if (!checkStringEmpty(value)) {
            return false;
        }
        int length = value.trim().length();
        return length >= min && length <= max;
    }

    public static boolean checkStringWithFormat(String value, String pattern) {
        return value != null && pattern != null && value.matches(pattern);
    }

    public static String normalizeStudentId(String studentId) {
        if (studentId == null) {
            return "";
        }
        return studentId.trim().toUpperCase();
    }

    public static boolean isValidStudentId(String studentId) {
        return normalizeStudentId(studentId).matches(STUDENT_ID_PATTERN);
    }

    public static boolean isValidStudentName(String name) {
        return checkStringLengthInRange(name, 2, 20);
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null || !phone.trim().matches(PHONE_PATTERN)) {
            return false;
        }
        String normalizedPhone = phone.trim();
        for (String prefix : VIETNAM_PHONE_PREFIXES) {
            if (normalizedPhone.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isDiscountPhone(String phone) {
        if (!isValidPhone(phone)) {
            return false;
        }
        String normalizedPhone = phone.trim();
        for (String prefix : DISCOUNT_PREFIXES) {
            if (normalizedPhone.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidCampusCode(String campusCode) {
        if (campusCode == null) {
            return false;
        }
        String campus = campusCode.trim().toUpperCase();
        return campus.equals("SE") || campus.equals("HE") || campus.equals("DE")
                || campus.equals("QE") || campus.equals("CE");
    }

    public static boolean isValidEmail(String email) {
        return email != null && email.trim().matches(EMAIL_PATTERN);
    }

    public static boolean isValidMountainCodeInput(String mountainCode) {
        return checkStringEmpty(mountainCode);
    }
}
