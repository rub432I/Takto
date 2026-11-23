package com.takto.launcher.util;

import java.util.Locale;

/**
 * Deterministic Offline Math Evaluator
 * Recursive descent arithmetic parser for +, -, *, /, %, ^, decimals and parentheses.
 * Runs instantly without external libraries.
 */
public class MathEvaluator {

    public static boolean isPotentialMath(String expr) {
        if (expr == null || expr.trim().isEmpty()) {
            return false;
        }
        String trimmed = expr.trim();
        // Check if expression consists only of math symbols and digits
        if (!trimmed.matches("^[0-9+\\-*/%^.() ]+$")) {
            return false;
        }
        // Must have at least one operator to be considered an expression
        return trimmed.matches(".*[+\\-*/%^].*") && trimmed.matches(".*[0-9].*");
    }

    public static String evaluate(String expr) {
        try {
            double res = parse(expr);
            if (Double.isNaN(res) || Double.isInfinite(res)) {
                return null;
            }
            // Format without trailing zero if integer
            if (res == (long) res) {
                return String.format(Locale.US, "%d", (long) res);
            } else {
                return String.format(Locale.US, "%.4f", res).replaceAll("0+$", "").replaceAll("\\.$", "");
            }
        } catch (Exception e) {
            return null;
        }
    }

    private static double parse(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor();
                    else if (eat('/')) {
                        double divisor = parseFactor();
                        if (divisor == 0) throw new ArithmeticException("Division by zero");
                        x /= divisor;
                    }
                    else if (eat('%')) x %= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return +parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    if (!eat(')')) throw new RuntimeException("Missing ')'");
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor());

                return x;
            }
        }.parse();
    }
}
