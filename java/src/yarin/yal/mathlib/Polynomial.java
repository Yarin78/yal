package yarin.yal.mathlib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Polynomial {
    private final List<Double> a;

    public Polynomial(double[] coeff) {
        a = new ArrayList<Double>(coeff.length);
        for (double v : coeff) {
            a.add(v);
        }

        while (a.size() > 1 && a.get(a.size() - 1) == 0)
            a.remove(a.size() - 1);
    }

    public Polynomial(Collection<Double> coeff) {
        a = new ArrayList<Double>(coeff);
        while (a.size() > 1 && a.get(a.size() - 1) == 0)
            a.remove(a.size() - 1);
    }

    public double get(int ix) {
        return a.get(ix);
    }

    public int getDegree() {
        return a.size() - 1;
    }

    public double eval(double x) {
        double val = 0;
        for (int i = a.size() - 1; i >= 0; --i)
            val = (val*x) + a.get(i);
        return val;
    }

    public Polynomial diff() {
        List<Double> p = new ArrayList<Double>(a.size() - 1);
        for (int i = 1; i < a.size(); i++)
            p.add(i*a.get(i));
        return new Polynomial(p);
    }

    public Polynomial divRoot(double x0) {
        // divide by (x-x0), ignore remainder
        double[] p = new double[a.size() - 1];
        for (int i = a.size() - 2; i >= 0; i--)
            p[i] = (i == a.size() - 2 ? 0 : p[i + 1]) * x0 + a.get(i+1);
        return new Polynomial(p);
    }

    public List<Double> findRoots(double xmin, double xmax)	{
        if (getDegree() == 1)
            return Arrays.asList(-a.get(0)/a.get(1));
        Polynomial d = diff();
        List<Double> droots = d.findRoots(xmin, xmax);
        List<Double> roots = new ArrayList<Double>();
        for (int i = -1; i < droots.size(); i++)
        {
            double lo = i < 0 ? xmin - 1 : droots.get(i);
            double hi = i + 1 == droots.size() ? xmax + 1 : droots.get(i + 1);
            boolean loSign = eval(lo) > 0, hiSign = eval(hi) > 0;
            if (loSign != hiSign) {
                while (hi-lo > 1e-9) {
                    double m = (lo + hi)/2, f = eval(m);
                    if ((f <= 0) != loSign)
                        lo = m;
                    else
                        hi = m;
                }
                roots.add((lo + hi)/2);
            }
        }
        return roots;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = a.size() - 1; i >= 0; i--) {
            if (a.get(i) == 0)
                continue;
            if (sb.length() > 0)
                sb.append(a.get(i) > 0 ? " + " : " - ");
            else if (a.get(i) < 0)
                sb.append("-");

            if (i == 0 || Math.abs(a.get(i)) != 1)
                sb.append(Math.abs(a.get(i)));
            if (i > 0)
                sb.append("x");
            if (i > 1)
                sb.append("^" + i);
        }
        return sb.toString();
    }
}