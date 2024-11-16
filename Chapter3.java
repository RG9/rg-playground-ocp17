class Chapter3 {

     public static void main(String[] args){
     
     Integer count = 0;
     if (count > 2)
	    System.out.println("Good day");
      count++; // IT WILL INCREMENT - BE AWARE OF TRICKY INDENT ON EXAM!
     System.out.println("count: " + count);
     
     if (count instanceof Integer)
      System.out.println("we can use instanceof w/o pattern matching for the same type");
     // if (count instanceof Integer x) // DOES NOT COMPILE, but compiles in JAVA 21
     Object o = 2;
     if (o instanceof final Number n)
     System.out.println("cannot reassign n = " + n);
     //   i = 5; // DOES NOT COMPILE: error: cannot assign a value to final variable i
    
     // if (count instanceof Number n) // DOES NOT COMPILE because pattern variable must be subtype - here Number is super type of Integer
     
     flowScopingShowcase(2);
     
     System.out.println("switch statement");
     //switch (o) { // error: patterns in switch statements are a preview feature and are disabled by default.
     int switcher=4;
     switch(switcher) {
        case 1: {
          System.out.println("case 1");
        }
        // }; // compiles as well
        case 3: case 4: System.out.println("case 3,4"); // break; // w/0 break; all subsequent cases will be printed
        case 5,6: System.out.println("case 5,6");
        default: case 7: System.out.println("case 7");
        case 8: System.out.println("case 8");
        // case 9: 10: System.out.println("case 9,10"); // DOES NOT COMPILE
    }
    
    System.out.println("switch expression");
    switch(switcher) {
      case 1 -> {
        System.out.println("case 1");
      // }; DOES NOT COMPILE
      }
      case 4 -> System.out.println("case 4");  
      case 5,6 -> System.out.println("case 5");
      // default, 6 -> System.out.println("case 6"); // DOES NOT COMPILE
     // default -> System.out.println("case 6");
    }
    
    String y = switch(switcher) {
      case 1 -> "case 1";
      case 4 -> { 
        yield "case 4"; 
      // }; DOES NOT COMPILE
      }
      case 5,6 -> "case 5";
      default -> "case default";
    // } // DOES NOT COMPILE : error: ';' expected
    };
    System.out.println("y = " + y);
     
    
    }
    
    private static void flowScopingShowcase(Number num) {
      if(!(num instanceof Integer integer))
          return;
      System.out.println("flow scoping: it's integer = " + integer.intValue());
    }
}
