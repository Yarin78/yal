package yarin.yal.mathlib;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class TestPrimes {
    @Test
    public void testPrimes() {
        Primes p = new Primes(1000);
        Assert.assertTrue(p.primeList.size() == 168);
        Assert.assertTrue(p.primeList.get(0) == 2);
        Assert.assertTrue(p.primeList.get(1) == 3);
        Assert.assertTrue(p.primeList.get(2) == 5);
        Assert.assertTrue(p.primeList.get(167) == 997);
        int noPrimes = 0;
        for (int i = 1; i <= 1000000; i++) {
            boolean prime = p.isPrime(i);
            if (prime)
                noPrimes++;
        }
        Assert.assertTrue(noPrimes == 78498);
    }

    @Test
    public void testPrimeFactorize() {
        Primes p = new Primes(1000);
        List<Long> all = p.getAllPrimeFactors(2 * 3 * 5 * 5 * 19 * 23);
        Assert.assertTrue(all.size() == 6);
        Assert.assertTrue(all.get(0) == 2 && all.get(1) == 3 && all.get(2) == 5 && all.get(3) == 5 && all.get(4) == 19 && all.get(5) == 23);
        List<Long> unique = p.getUniquePrimeFactors(2 * 3 * 5 * 5 * 19 * 23);
        Assert.assertTrue(unique.size() == 5);
        Assert.assertTrue(unique.get(0) == 2 && unique.get(1) == 3 && unique.get(2) == 5 && unique.get(3) == 19 && unique.get(4) == 23);

        all = p.getAllPrimeFactors(1009);
        Assert.assertTrue(all.size() == 1 && all.get(0) == 1009);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFailedPrimeFactorize() {
        Primes p = new Primes(10);
        p.getAllPrimeFactors(101);
    }
}
