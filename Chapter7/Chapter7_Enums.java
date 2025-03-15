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
     
        abstract int getNumberOfMonths();
     }
     
     public static void main(String[] args) throws Exception {
       System.out.println("enum value of");
       System.out.println(Seasons.valueOf("SPRING")); // prints SPRING
       // System.out.println(Seasons.valueOf("Spring")); // IllegalArgumentException: No enum constant Chapter7.Seasons.Spring
       
       var season = Seasons.WINTER;
       switch (season) {
          // case Seasons.WINTER -> { System.out.println("winter!"); } // DOES NOT COMPILE:  an enum switch case label must be the unqualified name of an enumeration constant
          case WINTER -> { System.out.println("winter!"); } 
          default -> { System.out.println("other season"); }
       }

    }
}


