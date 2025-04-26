/*
 * System.java
 */

// All built-in functions for VC are assumed to be static Java methods

package VC.lang;

import java.util.Scanner;
import java.util.NoSuchElementException;

public class System {

    private static final Scanner scanner = new Scanner(java.lang.System.in);

    public static final int getInt() {
        try {
            java.lang.System.out.print("Enter an integer: ");
            String line = scanner.nextLine();
            String[] tokens = line.trim().split("\\s+");

            if (tokens.length == 0 || tokens[0].isEmpty()) {
                throw new NumberFormatException("No input provided");
            }

            int value = Integer.parseInt(tokens[0]);
            java.lang.System.out.println("You have entered " + value + ".");
            return value;
        } catch (NoSuchElementException e) {
            handleError("Input error: " + e.getMessage());
            return -1; // Unreachable due to exit
        } catch (NumberFormatException e) {
            handleError("Invalid integer format: " + e.getMessage());
            return -1; // Unreachable due to exit
        }
    }

    public static final void putInt(int i) {
        java.lang.System.out.print(i);
    }

    public static final void putIntLn(int i) {
        java.lang.System.out.println(i);
    }

    public static final float getFloat() {
        try {
            java.lang.System.out.print("Enter a float: ");
            String line = scanner.nextLine();
            String[] tokens = line.trim().split("\\s+");

            if (tokens.length == 0 || tokens[0].isEmpty()) {
                throw new NumberFormatException("No input provided");
            }

            float value = Float.parseFloat(tokens[0]);
            java.lang.System.out.println("You have entered " + value + ".");
            return value;
        } catch (NoSuchElementException e) {
            handleError("Input error: " + e.getMessage());
            return -1.0F; // Unreachable due to exit
        } catch (NumberFormatException e) {
            handleError("Invalid float format: " + e.getMessage());
            return -1.0F; // Unreachable due to exit
        }
    }

    public static final void putFloat(float f) {
        java.lang.System.out.print(f);
    }

    public static final void putFloatLn(float f) {
        java.lang.System.out.println(f);
    }

    public static final void putBool(boolean b) {
        java.lang.System.out.print(b);
    }

    public static final void putBoolLn(boolean b) {
        java.lang.System.out.println(b);
    }

    public static final void putString(String s) {
        java.lang.System.out.print(s);
    }

    public static final void putStringLn(String s) {
        java.lang.System.out.println(s);
    }

    public static final void putLn() {
        java.lang.System.out.println();
    }

    private static void handleError(String message) {
        java.lang.System.out.println("Error: " + message);
        java.lang.System.exit(1);
    }
}
