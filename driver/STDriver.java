/**
 * Ordered Symbol Table Testing Client
 * @author Maksym Ovcharenko
 * Course: SDT 202 Design and Analysis of Data Structures and Algorithms
 * Due Date: 38th of April
 * Time Spent: 4 hours
 * Description: Tests each operation in the ST implementation.
 * AI Tools: No AI code generation tools were used
 */

package driver;

import datastructure.ST;

public class STDriver {
    public static void main(String[] args) {
        ST<String, Integer> st = new ST<>();

        // Test put and size
        st.put("E", 5);
        st.put("B", 2);
        st.put("A", 1);
        st.put("D", 4);
        st.put("C", 3);
        st.put("F", 6);
        st.put("G", 7);

        System.out.println("Size: " + st.size());
        System.out.println("Contains 'D'? " + st.contains("D"));
        System.out.println("Get 'C': " + st.get("C"));

        // Test ordered operations
        System.out.println("Min: " + st.min());
        System.out.println("Max: " + st.max());
        System.out.println("Floor of 'E': " + st.floor("E"));
        System.out.println("Ceiling of 'C': " + st.ceiling("C"));

        // Test rank and select
        System.out.println("Rank of 'D': " + st.rank("D"));
        System.out.println("Select key at rank 3: " + st.select(3));

        // Test range operations
        System.out.println("Size between 'B' and 'F': " + st.size("B", "F"));
        System.out.print("Keys between 'B' and 'F': ");
        for (String key : st.keys("B", "F")) {
            System.out.print(key + " ");
        }
        System.out.println();

        // Test delete operations
        st.delete("D");
        System.out.println("After deleting 'D', size: " + st.size());
        st.deleteMin();
        System.out.println("After deleting min, new min: " + st.min());
        st.deleteMax();
        System.out.println("After deleting max, new max: " + st.max());

        // Test put with null value (should delete)
        st.put("C", null);
        System.out.println("After put('C', null), contains 'C'? " + st.contains("C"));
    }
}