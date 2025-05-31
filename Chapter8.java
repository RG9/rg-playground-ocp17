import java.util.function.*;

class Chapter8 {

  @FunctionalInterface // produces compilation error when interface breaks functional interface rules
  interface MyFunctionalInterface {
     void print();
     // void print2(); // DOES NOT COMPILE MyFunctionalInterface is not a functional interface- multiple non-overriding abstract methods found in interface MyFunctionalInterface
     String toString(); // Object method doesn't count
     int hashCode(); // Object method doesn't count
     boolean equals(Object otherObject); // Object method doesn't count
     default int one() { return 1; } // default method doesn't count
     private int two() { return 2; } // private method doesn't count
  }
  
  
  public static void main(String[] args) throws Exception {
    System.out.println("CHAPTER 8");
    
    UnaryOperator<Integer> a1 = i -> i * 3;
    UnaryOperator<Integer> a2 = i -> i + 2;
    System.out.println(a1.compose(a2).apply(2)); // should be 12, because first a2 will be called
    
    for (int i = 0; i < 2; i++) {
      // Supplier<Integer> supplier = () -> i;  //  error: local variables referenced from a lambda expression must be final or effectively final
      int j = i; // if we can set "final" modifier then it's definitely effectively final
      Supplier<Integer> supplier = () -> j; 
      System.out.println(supplier.get());
    }
    
    // Predicate<String> p2 = s -> {s.length() < 5}; // error: not a statement
    // Predicate<String> p2 = s -> {return s.length() < 5}; // error: ';' expected
    Predicate<String> p2 = s -> {return s.length() < 5; };
    System.out.println(p2.test("")); // prints true 
    p2 = s -> s.length() < 5; // or shorter
    System.out.println(p2.test("")); // prints true
    // System.out.println((s -> s.length() < 5).test("")); //  error: lambda expression not expected here
    p2 = __ -> true; // we can return boolean directly
    p2 = (s) -> true; // we can use brackets
    p2 = (String s) -> true; // define type explicitly
    // p2 = String s -> true; // error: not a statement, but brackets are required
 

    var list = java.util.List.of("a", "b", "c");
    Consumer<String> print = s -> System.out.println(s);
    list.forEach(print);
    
    System.out.println();
    list.forEach(s -> System.out.println(s)); // lambda type deducted based on method parameter type
    
    System.out.println();
    list.forEach(System.out::println); // or use method reference
    
    String s = "";
    // Predicate<String> p3 = s -> s.length() < 5; // error: variable s is already defined in method main(String[])
    Predicate<String> p3 = string -> string.length() < 5; // correct
  }
 
}


