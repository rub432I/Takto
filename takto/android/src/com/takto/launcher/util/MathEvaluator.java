package com.takto.launcher.util;

import java.util.Locale;

/**
 * MathEvaluator: Deterministic offline arithmetic parser.
 * Supports +, -, *, /, %, ^, parentheses, decimals, commas, and negative numbers.
 */
public class MathEvaluator {

    public static boolean isPotentialMath(String expr) {
        if (expr == null) {
            return false;
        }
        String trimmed = expr.trim();
        if (trimmed.isEmpty()) {
            return false;
        }
        // Must contain only math characters: digits, spaces, parentheses, operators, dots, commas
        if (!trimmed.matches("^[0-9+\\-*/%^.(), ]+$")) {
            return false;
        }
        // Must have at least one digit
        if (!trimmed.matches(".*[0-9].*")) {
            return false;
        }
        // Must contain at least one operator symbol (+ - * / % ^)
        return trimmed.matches(".*[+\\-*/%^].*");
    }

    public static String evaluate(String expr) {
        if (expr == null) return null;
        String cleanExpr = expr.trim().replace(",", "");
        try {
            double res = parse(cleanExpr);
            if (Double.isNaN(res) || Double.isInfinite(res)) {
                return null;
            }
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
                if (pos < str.length()) throw new RuntimeException("Unexpected character: " + (char) ch);
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
                        if (divisor == 0.0) throw new ArithmeticException("Division by zero");
                        x /= divisor;
                    }
                    else if (eat('%')) {
                        double divisor = parseFactor();
                        if (divisor == 0.0) throw new ArithmeticException("Modulo by zero");
                        x %= divisor;
                    }
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
                    if (!eat(')')) throw new RuntimeException("Missing closing parenthesis");
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected character: " + (char) ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor());

                return x;
            }
        }.parse();
    }
}
