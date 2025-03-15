class Chapter7_Interfaces {
 
     public abstract interface AbstractIsImplicitInInterface { // abstract is implicit (added by compiler)
        public static final String MY_CONSTANT = "ABC"; // "public static final" is implicit
        public abstract void myMethod(); // public and abstract is implicit
     }
     
     // final interface FinalInterface {  } // DOES NOT COMPILE: illegal combination of modifiers: interface and final
     
     
     interface InterfaceAllowedVisibilityModifiers {
       private static void privateStaticMethodWithBody() { }
       // protected static void staticMethodWithBody() { } DOES NOT COMPILE:  error: modifier protected not allowed here
       static void staticMethodWithBody() { } // implicitly "public"

       private void methodWithBody(){}
       // protected void methodWithBody(){} DOES NOT COMPILE: interface abstract methods cannot have body
       // void methodWithBody(){} // DOES NOT COMPILE: interface abstract methods cannot have body
       default void defaultMethodWithBody(){}
     }
     
     static abstract class SomeAbstractClass {
        abstract void methodToImplement();
     }
     
     interface SomeInterface {
        void methodToImplement();
     }
     
     interface SameDefaultSignature1 {
        default int getInt() { return 1; }
     }
     
     interface SameDefaultSignature2 {
        default int getInt() { return 2; }
     }
     
     // DOES NOT COMPILE:   class MustOverrideDefault inherits unrelated defaults for getInt() from types SameDefaultSignature1 and SameDefaultSignature2
     // static class MustOverrideDefault implements SameDefaultSignature1, SameDefaultSignature2 { } 

     static class MustOverrideDefault implements SameDefaultSignature1, SameDefaultSignature2 {
        public int getInt() {
          return SameDefaultSignature1.super.getInt() + SameDefaultSignature2.super.getInt(); // prints 3
        }
     }
      
     public static void main(String[] args) throws Exception {
     
      new SomeAbstractClass() {
         void methodToImplement() { } 
      };
      new SomeInterface() {
         // void methodToImplement() { } // DOES NOT COMPILE: attempting to assign weaker access privileges; was public
         public void methodToImplement() { }
      };
      
       System.out.println("MustOverrideDefault");
      System.out.println(new MustOverrideDefault().getInt());
    }
}


