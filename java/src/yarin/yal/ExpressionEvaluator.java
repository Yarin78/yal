package yarin.yal;

public class ExpressionEvaluator
{
    private static final String[] operators = new String[] { "[\\+~]", "[\\*/]", "[\\^]" };

    private static double eval(String expr, int level) {
        if (level == 3) return (expr.length() == 0) ? 0 : Double.parseDouble(expr);
        String[] parts = expr.split(operators[level]);
        double val = eval(parts[0], level + 1);
        for (int i = 1, cur = parts[0].length(); i < parts.length; cur += parts[i++].length() + 1) {
            double nval = eval(parts[i], level + 1);
            switch (expr.charAt(cur)) {
                case '+': val += nval; break;
                case '~': val -= nval; break;
                case '*': val *= nval; break;
                case '/': val /= nval; break;
                case '^': val = Math.pow(val, nval); break;
            }
        }
        return val;
    }

    public static double funcEval(String func, double val) {
        if (func.length() == 0) {
            return val;
        } else if (func.equals("sin")) {
            return Math.sin(val);
        } else if (func.equals("cos")) {
            return Math.cos(val);
        }
        throw new RuntimeException("Unknown function: " + func);
    }

    public static double Evaluate(String expr) {
        // TODO: This doesn't allow unary minus in the input
        expr = expr.replace('-', '~'); // Distinguish between unary and binary minus
        int rp;
        while ((rp = expr.indexOf(')')) >= 0) {
            int lp = expr.substring(0, rp).lastIndexOf('('), llp = lp;
            double val = eval(expr.substring(lp + 1, rp), 0);
            while (llp > 0 && Character.isLetter(expr.charAt(llp - 1))) llp--;
            expr = expr.substring(0, llp) + Double.toString(funcEval(expr.substring(llp, lp).toLowerCase(), val)) + expr.substring(rp + 1);
        }
        return eval(expr, 0);
    }
}