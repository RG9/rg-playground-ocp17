class Chapter7_Enums {
 
     enum Seasons {
      WINTER, SPRING, SUMMER, AUTUMN
      , // allowed, but optional
      // , // another one DOES NOT COMPILE
      ; // optional
     }
     
     enum EnumWithAbstractMethod { 
        WINTER { int getNumberOfMonths() { return 4; } }, // anonymous class
        SPRING, SUMMER, AUTUMN
        { int getNumberOfMonths() { return 4; } };
     
         // abstract int getNumberOfMonths(); DOES NOT COMPILE - every enum value must implement method
         
         int getNumberOfMonths() { return 0; }
     }
     
     enum EnumWithPublicConstructor {
      ONE(1), TWO("two")
      //; // error: ',', '}', or ';' expected - be aware that semicolon is required when enum have methods and constructors
      ;
      // public EnumWithPublicConstructor(int val){ } // compilation error: modifier public not allowed here
        EnumWithPublicConstructor(int val){ System.out.println(val); }  // constructor is implicitly private
        private EnumWithPublicConstructor(Object val){ System.out.println(val);  }
      }
     
     
     public static void main(String[] args) throws Exception {
       System.out.println("enum value of");
       System.out.println(Seasons.valueOf("SPRING")); // prints SPRING
       // System.out.println(Seasons.valueOf("Spring")); // IllegalArgumentException: No enum constant Chapter7.Seasons.Spring
       
       var season = Seasons.WINTER;
       // Seasons season = null; // throws NPE
       switch (season) {
          // case Seasons.WINTER -> { System.out.println("winter!"); } // DOES NOT COMPILE:  an enum switch case label must be the unqualified name of an enumeration constant
          case WINTER -> { System.out.println("winter!"); } 
          default -> { System.out.println("other season"); }
       }
       
       EnumWithPublicConstructor.TWO.ordinal(); // I need to first use enum in order to execute code in constructors

    }
}


