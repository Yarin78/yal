package yarin.yal.mathlib;

/**
 * Setups combinatorial tables for efficient modular arithmetic when MOD is a prime
 *
 * Requires O(N) space and time in setup, so only useful if mod is relatively small
 */
public class ModularPrimeCombinatorics {
  private final int MOD;

  private int[] inverse;
  private int[] fact;

  private int mod(int a, int m) {
    a %= m;
    if (a < 0) a += m;
    return a;
  }

  private int calcInverse(int a, int m) {
    a = mod(a, m);
    if (a == 1) return 1;
    return mod((1 - m * calcInverse(m % a, a)) / a, m);
  }

  private int chooseInternal(int n, int k) {
    if (k > n || k < 0) return 0;

    int d = fact[k] * fact[n-k];
    d %= MOD;
    return (fact[n] * inverse[d]) % MOD;
  }

  public int choose(int n, int k) {
    int v = 1;
    while (n > 0) {
      v *= chooseInternal(n % MOD, k % MOD);
      v %= MOD;
      n /= MOD;
      k /= MOD;
    }
    return v;
  }

  public int factorial(int n) {
    // TODO: If n >= MOD, use Wilson's theorem, http://en.wikipedia.org/wiki/Wilson%27s_theorem
    return fact[n];
  }

  public ModularPrimeCombinatorics(int mod) {
    MOD = mod;
    fact = new int[mod];
    inverse = new int[mod];
    fact[0] = 1;
    inverse[0] = 0;
    for (int i = 1; i < MOD; i++) {
      fact[i] = (i * fact[i-1]) % MOD;
      inverse[i] = calcInverse(i, MOD);
    }
  }
}
