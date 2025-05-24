class Chapter7_Polymorphism {
 
    static class Parent {
      void print() { System.out.println("Parent#print"); }
      static void ping() { System.out.println("Parent#ping"); }
    }
    
    static class Child extends Parent {
       void print() { System.out.println("Child#print"); }
       static void ping() { System.out.println("Child#ping"); }
    }
   
    public static void main(String[] args) throws Exception {
      Parent p = new Child();
      p.print(); // prints "Child#print" as method overriding is one of property of polymorphism
      p.ping(); // prints "Parent#ping" as in case of static method hiding - method is determined by reference type
      Child c = (Child) p;
      c.print();  // prints "Child#print" 
      c.print(); // prints "Child#ping"
    }
}


