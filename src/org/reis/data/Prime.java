package org.reis.data;

import java.util.*;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;

public class Prime {
    TreeMap<Integer, Integer> wheel = new TreeMap<>();
    Integer next = 1;
    Integer turn() {
        next += 1;

        Map.Entry<Integer, Integer> first = wheel.firstEntry();
        Integer prime = ( first != null && first.getKey().equals(next) && first.getValue() != null )
            ? wheel.remove(next)
            : next;

        Integer multiple = next + prime;
        while(wheel.get(multiple) != null) multiple += prime;
        wheel.put(multiple,prime);

        return next == prime ? next : turn();
    }

    public static void main(String... args) {
        long start = System.currentTimeMillis();
        IntStream
            .generate(new Prime()::turn)
            .forEach(System.out::println);
        long end = System.currentTimeMillis();
        System.out.println((end-start)/1000);
    }
}

/*
public class PrimeFactors {
    TreeMap<Integer, HashSet<Integer>> wheel = new TreeMap<>();
    Integer last = 1;
    Integer next() {
        last += 1;
        Map.Entry<Integer, HashSet<Integer>> next = wheel.firstEntry();
        if(next != null && next.getKey().equals(last)) {
            for(Integer prime: next.getValue())
                wheel.computeIfAbsent(last+prime, k -> new HashSet<>()).add(prime);
            wheel.remove(last);
            return next();
        }
        else {
            wheel.computeIfAbsent(last+last, k -> new HashSet<>()).add(last);
            return last;
        }
    }

    public static void main(String... args) {
        long start = System.currentTimeMillis();
        IntStream.generate(new Prime()::next).limit(1_000_000).forEach((n) -> {});
        long end = System.currentTimeMillis();
        System.out.println((end-start)/1000);
    }
}
*/
/*
public class Prime {

    public static IntStream sieve(int n) {
        BitSet composite = new BitSet(n + 1);
        IntStream
            .rangeClosed(2, (int) Math.sqrt(n))
            .filter(x -> composite.get(x) == false)
            .flatMap(x -> IntStream.iterate(x*x, y -> y <= n, y -> y + x))
    }

    public static void main(String... args) {
        compose().forEach(System.out::println);
    }
}
*/