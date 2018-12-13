package org.reis.data;

public class Hello {
    public static void main(String... args) {
        //for (long l = 4946144450195624L; l > 0; l >>= 5)
        //    System.out.print((char) (((l & 31 | 64) % 95) + 32));

        // l = 0b1_11000_10110_00011_11000_11111_01000_00011_00000_00111_10011_11111L
        for(long l=63828607695330943L, m=0L, c=0; l>0; c=(l&31)==31?32:0, m+=(l&31)<31?(l&16)==0?l&15:0-(l&15):0, l>>=5)
            System.out.print(((l&31)<31?(char)(m+104-c):"")+(l==1?"!\n":l==59445023?", ":""));
    }
}

