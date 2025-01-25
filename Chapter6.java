class Chapter6 {

    static class HiddenInstanceVariable {
       int num;
       void setNum(int num){
          num = num; // should be "this.num = num" to assign instance variable
       }
       int getNum() { return num; }
    }
    
    static class HiddenInheritedVariable extends HiddenInstanceVariable {
       int num; // hidden, because same type and name as inherited - comment to unhide
       void setNum(int num){
          this.num = num;
       }
       void setSuperNum(int num){
          super.num = num;
       }
    }
    
    static class LowerCaseConstructorDoesNoCompile {
       // public lowerCaseConstructorDoesNoCompile() {} // DOES NOT COMPILE: error: invalid method declaration; return type required
        public LowerCaseConstructorDoesNoCompile() {} // ok
    }
    
    // "compiler only inserts default constructor when no constructor is defined"
    static class NoDefaultConstructor {
      NoDefaultConstructor(int num) {  }
    }
    
    static class PrivateNoArgsConstructor {
      private PrivateNoArgsConstructor() { }
    }
    
    static class ExtendFromClassWithPrivateNoArgsConstructor extends PrivateNoArgsConstructor {
    }
    
    static class PrivateNumArgConstructor {
      private PrivateNumArgConstructor(int num) { }
    }
   
   static class ExtendFromClassWithPrivateNumArgConstructor extends PrivateNumArgConstructor {
       // we must define constructor, otherwise DOES NOT COMPILE: error: constructor PrivateNumArgConstructor in class PrivateNumArgConstructor cannot be applied to given types;
      ExtendFromClassWithPrivateNumArgConstructor(int num) {
        super(num); // must call super constructor, otherwise DOES NOT COMPILE
      }
    
   }
   
   static class NoLineAllowedBeforeSuper {
      NoLineAllowedBeforeSuper(){
        // comment is allowed
        // System.out.println("line before super"); // DOES NOT COMPILE error: call to super must be first statement in constructor
        super();
      }
   }
   
   static class ConstructorCicleDoesNotCompile {
      ConstructorCicleDoesNotCompile(){ 
          //this(5); // DOES NOT COMPILE error: recursive constructor invocation
      }
      ConstructorCicleDoesNotCompile(int num){
          this(); 
      }
   }
   
   static class InitializationOrderSuperClass {
      static { System.out.println("static superclass init"); }
      { System.out.println("superclass init"); }
      InitializationOrderSuperClass() {  System.out.println("superclass constructor"); }
   }
   
   static class InitializationOrderSubClass extends InitializationOrderSuperClass {
      static { System.out.println("static subclass init"); }
      { System.out.println("subclass init"); }
      InitializationOrderSubClass() {  System.out.println("subclass constructor"); }
   }
   
    static class ClassWithMethodToOverride {
      protected Number get(Number num) throws java.io.IOException {
         return 1;
      }
    }
    
    static class ClassWithOverriddenMethod extends ClassWithMethodToOverride {
      @Override
      // public Integer get(Integer num) throws java.io.IOException { // DOES NOT COMPILE - signature must much - "covariant" types are not allowed
      // public Integer get(Number num) throws Exception { // DOES NOT COMPILE - exception should be the same or subtype
      // public Integer get(Number num) throws java.text.ParseException { // DOES NOT COMPILE: overridden method does not throw ParseException
      public Integer get(Number num) throws java.net.SocketException { 
         return 2;
      }
      void nothingToDo() { }
    }
    
    static class ClassWithOverriddenMethod2 extends ClassWithOverriddenMethod {
      // protected Number get(Number num) throws java.io.IOException { // // DOES NOT COMPILE - cannot "go back" to original signature (attempting to assign weaker access privileges; was public)
      public Integer get(Integer num) throws java.io.IOException { // overloads instead override
         return 3;
      }
      // int nothingToDo() { } // DOES NOT COMPILE: cannot override: return type int is not compatible with void
    }
    
    static class WithStaticMethod {
      protected static Number get(Number num) throws Exception { // if final then cannot be hidden by child class
         return 1;
      }
    }
    
    static class HidesStaticMethod extends WithStaticMethod {
       public static Integer get(Number num) throws java.io.IOException { 
         return 2;
      }
    }
   
   
    static abstract class AbstractClass { // note: abstract class does not have to declare "abstract" methods
      // private abstract void method(); // DOES NOT COMPILE:  error: illegal combination of modifiers: abstract and private
      // void abstract method(); // DOES NOT COMPILE
      abstract protected void  method_1(); // OK - abstract can be before
      protected abstract void method_2(); // OK - or after access modifier
      protected abstract Number get(Number num) throws java.io.IOException;
      
      AbstractClass(){
        method_1(); // compiles
      }
    }
   
    static abstract class AbstractClassImpl extends AbstractClass {
       public Integer get(Number num) throws java.net.SocketException { 
         return 2;
      }
    }
   
     public static void main(String[] args) throws Exception {
      System.out.println("CHAPTER 6");
      
      System.out.println("hiddenInstanceVariable");
      var hiddenInstanceVariable = new HiddenInstanceVariable();
      hiddenInstanceVariable.setNum(5);
      System.out.println(hiddenInstanceVariable.num); // prints 0 instead 5
      
      System.out.println("hiddenInheritedVariable");
      var hiddenInheritedVariable = new HiddenInheritedVariable();
      hiddenInheritedVariable.setNum(5);
      System.out.println(hiddenInheritedVariable.num); // prints 5 - OK, "setNum" was correctly overriden
      System.out.println(hiddenInheritedVariable.getNum()); // prints 0, because we assigned to this.num instead of super.num
      hiddenInheritedVariable.setSuperNum(6);
      System.out.println(hiddenInheritedVariable.getNum()); // prints 6, because we assigned to super.num (so there are two variables with the same name stored in object)
      System.out.println(((HiddenInstanceVariable) hiddenInheritedVariable).num); // prints 6, we can access super.num by casting to super.class

      System.out.println("NoDefaultConstructor");
      // new NoDefaultConstructor(); // DOES NOT COMPILE: error: constructor NoDefaultConstructor in class NoDefaultConstructor cannot be applied to given types;
      
      System.out.println("ExtendFromClassWithPrivateNoArgsConstructor");
      new ExtendFromClassWithPrivateNoArgsConstructor();
      
      System.out.println("InitializationOrderSubClass");
      new InitializationOrderSubClass();
      // prints
      // static superclass init
      // static subclass init
      // superclass init
      // superclass constructor
      // subclass init
      // subclass constructor

      
      System.out.println("ClassWithOverriddenMethod");
      System.out.println(new ClassWithOverriddenMethod().get(0)); // prints 2
      System.out.println(new ClassWithOverriddenMethod2().get(0)); // prints 2, so not overridden but method with "closer" param type was chosen
      
      System.out.println("HidesStaticMethod");
       System.out.println(HidesStaticMethod.get(0)); // prints 2
    }
}


