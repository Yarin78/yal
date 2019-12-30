package yarin.yal.mathlib;

public class Frac implements Comparable<Frac> {
    public long nom, denom;

    public Frac(long nominator, long denominator) {
        nom = nominator;
        denom = denominator;

        if (denominator != 1)
            fix();
    }

    public String toString() {
        if (denom == 1)
            return Long.toString(nom);
        return nom + "/" + denom;
    }

    private void fix() {
        if (denom < 0) {
            nom = -nom;
            denom = -denom;
        }
        if (nom == 0)
            denom = 1;
        long d = gcd(Math.abs(nom), denom);
        nom /= d;
        denom /= d;
    }

    private static long gcd(long a, long b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    public Frac add(Frac b) {
        return new Frac(nom * b.denom + denom * b.nom, denom * b.denom);
    }

    public Frac sub(Frac b) {
        return new Frac(nom * b.denom - denom * b.nom, denom * b.denom);
    }

    public Frac mul(Frac b) {
        return new Frac(nom * b.nom, denom * b.denom);
    }

    public Frac mul(long b) {
        return new Frac(nom * b, denom);
    }

    public Frac div(Frac b) {
        return new Frac(nom * b.denom, denom * b.nom);
    }

    public Frac div(long b) {
        return new Frac(nom, denom * b);
    }

    public boolean equals(Frac b) {
        return nom == b.nom && denom == b.denom;
    }

    public boolean equals(Frac a, long b) {
        return a.nom == b && a.denom == 1;
    }

    public Frac neg() {
        return new Frac(-nom, denom);
    }

    public boolean less(Frac a, Frac b) {
        return compare(a, b) < 0;
    }

    public boolean less(Frac a, long b) {
        return compare(a, new Frac(b, 1)) < 0;
    }

    public boolean greater(Frac a, Frac b) {
        return compare(a, b) > 0;
    }

    public boolean greater(Frac a, long b) {
        return compare(a, new Frac(b, 1)) > 0;
    }

    public boolean greaterEqual(long b) {
        return compare(this, new Frac(b, 1)) >= 0;
    }

    public boolean greaterEqual(Frac a, Frac b) {
        return compare(a, b) >= 0;
    }

    public boolean lessEqual(Frac b) {
        return compare(this, b) <= 0;
    }

    public boolean lessEqual(long b) {
        return compare(this, new Frac(b, 1)) <= 0;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Frac)) return false;
        return equals((Frac)obj);
    }

    public int hashCode() {
        return (int) nom + 29 * (int) denom;
    }

    public static int compare(Frac x, Frac y) {
        if (y.denom == 1) {
            long b = y.nom * x.denom;
            if (x.nom < b)
                return -1;
            if (x.nom > b)
                return 1;
            return 0;
        }
        Frac t = x.sub(y);
        if (t.nom < 0)
            return -1;
        if (t.nom == 0)
            return 0;
        return 1;
    }

    public int compareTo(Frac other) {
        return compare(this, other);
    }
}
