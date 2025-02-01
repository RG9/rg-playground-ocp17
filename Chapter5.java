// import static java.util.Arrays;       // DOES NOT COMPILE - we shoud use .* (error: static import only from classes and interfaces)
import static java.util.Arrays.asList; // OK, but we should use "asList" instead "Arrays.asList(1)"
// static import java.util.Arrays.*;     // DOES NOT COMPILE because wrong order of modifiers
import static java.util.Arrays.*; // OK, "Arrays.asList" would be redundant

class Chapter7 {
     
    // zzz: void method () { } // DOES NOT COMPILE: <identifier> expected
    
    // void public method() { } // DOES NOT COMPILE: invalid method declaration; return type required
    
    static class Foo {
      static int bar = 5;
    }
    
    static class InstanceInitializers {
      int i;
      static long l;
      {
        i = 1;
        l = 1;
      }
      static {
        // i = 2; // DOES NOT COMPILE error: non-static variable i cannot be referenced from a static contex
        l = 2;
      }
    }
      
    static class VarargsPlayground {
        public static void method(int i, int... num) {
           System.out.println("i: "+i +", varargs len: "+ num.length);
        }
        public static void method(Integer i, int... num) { // OK, because Java tries to find "most specific method" 
           System.out.println("Integer: "+i +", varargs len: "+ num.length);
        }
        private static void method(int... num) { // COMPILES, but doesn't make sense because we cannot really use varargs (we have to pass array explicitly)
           System.out.println("varargs len: "+ num.length);
        }
        
       // static void method(int[] num) { } // DOES NO COMPILE: cannot declare both method(int[]) and method(int...)
        
       //  static void method(int... i, int... num) { } // DOES NOT COMPILE: varargs parameter must be the last parameter
       // static void method(int... i, int num) { } // DOES NOT COMPILE: varargs parameter must be the last parameter
    }
      
     public static void main(String[] args) throws Exception {
      System.out.println("CHAPTER 7");
       
       // Arrays.asList(1); DOES NOT COMPILE "error: cannot find symbol Arrays", because we imported "Arrays.asList"
       asList(1);
       
       Foo foo = null;
       System.out.println(foo.bar); // no NPE because static field is used
       
       System.out.println("initialization");
       InstanceInitializers ii = new InstanceInitializers();
       System.out.println(ii.i); 
       System.out.println(ii.l); // prints 1 because instance initializer is invoked before constructor
       
       System.out.println("varargs");
      //  VarargsPlayground.method(1); // DOES NOT COMPILE error: reference to method is ambiguous
      // VarargsPlayground.method(null); // NPE: java.lang.NullPointerException: Cannot read the array length because "<parameter1>" is null
       VarargsPlayground.method();
      // VarargsPlayground.method(1, 2); // DOES NOT COMPILE error: reference to method is ambiguous
       VarargsPlayground.method(new int[1]);
       VarargsPlayground.method(1, new int[1]);
       VarargsPlayground.method(Integer.valueOf(1), new int[2]);
       
       System.out.println("autoboxing");
       var a0 = 1;
       int a1 = 1;
       byte a2 = 1;
       short a3 = 1;
       long a4 = 1;
       float a5 = 1;
       double a6 = 1;
       char a7 = 1;
       boolean a8 = true;
       Integer b1 = 1;
       Byte b2 = 1;
       Short b3 =1;
      // Long b4 = 1; // DOES NOT COMPILE: incompatible types: int cannot be converted to Long
      // Float b5 = 1; // DOES NOT COMPILE: incompatible types: int cannot be converted to Float
     //  Double b6 = 1; // DOES NOT COMPILE: incompatible types: int cannot be converted to Double
       Long b4 = 1L;
       Float b5 = 1.0f;
       Double b6 = 1.0;
       Character b7 = 1;
       Boolean b8 = true;
       System.out.println("unboxing");
       a1 = b1; 
       a1 = b2;
       a1 = b3;
      // a1 = b4; // DOES NOT COMPILE; incompatible types: Long cannot be converted to int
      // a1 = (int) b4; DOES NOT COMPILE = error: incompatible types: Long cannot be converted to int
      // a1 = a4; // DOES NOT COMPILE: incompatible types: possible lossy conversion from long to int
      a1 = (int) a4;
      // a1 = b5; // DOES NOT COMPILE; incompatible types: Float cannot be converted to int
      // a1 = a5; // DOES NOT COMPILE:  incompatible types: possible lossy conversion from float to int
      a1 = (int) a5;
       a1 = b7;
      //  a1 = b8; // DOES NOT COMPILE: incompatible types: Boolean cannot be converted to int
       // a2 = b1; // DOES NOT COMPILE: incompatible types: Integer cannot be converted to byte
       a2 = b2;
       // a3 = b1; // DOES NOT COMPILE: incompatible types: Integer cannot be converted to short
       a3 = b2;
       a3 = b3;
       a5 = b3;
       a6 = b3;
       
    }
}


