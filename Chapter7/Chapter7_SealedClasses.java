class Chapter7_SealedClasses {
 
    sealed class Sealed permits SubClass, NonSealedSubClass, SealedSubClass { }
    // class SubClass extends Sealed { } // error: sealed, non-sealed or final modifiers expected
    // final class SubClass { } // DOES NOT COMPILE: subclass Chapter7_SealedClasses.SubClass must extend sealed class
    final class SubClass extends Sealed { } 
    non-sealed class NonSealedSubClass extends Sealed { }
    // sealed class SealedSubClass permits MyClass extends Sealed  { } // DOES NOT COMPILE: wrong order
    sealed class SealedSubClass extends Sealed permits MyClass  { }
    final class MyClass extends SealedSubClass { } 
    
    
    public static void main(String[] args) throws Exception {
      Sealed v = new Chapter7_SealedClasses().new MyClass();
      // note: use "java --enable-preview --source 17" to enable patterns in switch statements
      switch(v) {
        // case SubClass -> System.out.println("Subclass"); error: type pattern expected
        case SubClass s -> System.out.println("Subclass");
        default ->  System.out.println("default"); // without default, there is error: the switch statement does not cover all possible input values
        case MyClass m -> System.out.println("MyClass");
      }
    }
}


