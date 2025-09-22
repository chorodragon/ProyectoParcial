// src/main/java/app/core/ValidationUtil.java
package app.core;

import javax.swing.JOptionPane;

public class ValidationUtil {

    public static boolean isEmpty(String text) {
        return text == null || text.trim().isEmpty();
    }

    public static boolean isNumeric(String text) {
        if (isEmpty(text)) return false;
        try {
            Integer.parseInt(text.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isPositiveNumber(String text) {
        if (!isNumeric(text)) return false;
        return Integer.parseInt(text.trim()) > 0;
    }

    public static boolean isValidNit(String nit) {
        if (isEmpty(nit)) return true; // NIT es opcional
        // Validación básica: solo números o números con guión
        return nit.trim().matches("^[0-9-]+$");
    }

    public static boolean isValidTelefono(String telefono) {
        if (isEmpty(telefono)) return true; // Teléfono es opcional
        // Validación básica: solo números, espacios, guiones y signos +
        return telefono.trim().matches("^[0-9\\s\\-+()]+$");
    }

    public static void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Error de Validación", JOptionPane.ERROR_MESSAGE);
    }

    public static void showSuccess(String message) {
        JOptionPane.showMessageDialog(null, message, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean showConfirmation(String message) {
        int result = JOptionPane.showConfirmDialog(
            null, 
            message, 
            "Confirmación", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        return result == JOptionPane.YES_OPTION;
    }

    public static String validateRequired(String value, String fieldName) {
        if (isEmpty(value)) {
            return "El campo " + fieldName + " es obligatorio";
        }
        return null;
    }

    public static String validateYear(String yearStr) {
        if (isEmpty(yearStr)) {
            return "El año es obligatorio";
        }
        if (!isNumeric(yearStr)) {
            return "El año debe ser numérico";
        }
        int year = Integer.parseInt(yearStr.trim());
        if (year <= 0) {
            return "El año debe ser mayor a 0";
        }
        if (year > 2030) {
            return "El año no puede ser mayor a 2030";
        }
        return null;
    }

    public static String validatePasswordMatch(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            return "Las contraseñas no coinciden";
        }
        return null;
    }
}