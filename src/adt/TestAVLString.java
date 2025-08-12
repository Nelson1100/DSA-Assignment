package adt;

public class TestAVLString {
    public static void main(String[] args) {
        AVL_Implementation<String> tree = new AVL_Tree<>();

        // Insert some strings
        tree.insert("Banana")
            .insert("Apple")
            .insert("Mango")
            .insert("Cherry")
            .insert("Blueberry")
            .insert("Avocado");

        System.out.print("Inorder after insert: ");
        tree.traverse(); 
        // Alphabetical order expected: Apple Avocado Banana Blueberry Cherry Mango

        System.out.println("Min = " + tree.getMin()); // Apple
        System.out.println("Max = " + tree.getMax()); // Mango

        // If helpers exist in interface:
        if (tree instanceof AVL_Tree<String> avl) {
            System.out.println("Contains 'Cherry'? " + avl.contains("Cherry")); // true
            System.out.println("Contains 'Durian'? " + avl.contains("Durian")); // false
            System.out.println("Size = " + avl.size());             
            System.out.println("Height = " + avl.height());         
            System.out.println("Valid AVL? " + avl.isValidAVL());   
        }

        // Delete a value
        tree.delete("Banana");

        System.out.print("Inorder after delete('Banana'): ");
        tree.traverse();
        // Should be: Apple Avocado Blueberry Cherry Mango
    }
}
