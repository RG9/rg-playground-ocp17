class Chapter7_Records {
 
    // record SameName(String name) { static int name = 0; } // DOES NOT COMPILE: record component name is already defined in record SameName
    
    record CompactConstructor(int i, String s) { 
      CompactConstructor {  
       i=2; s="b"; // can modify implicit constructor params
        // this.i = 3; // but not record field - DOES NOT COMPILE: error: cannot assign a value to final variable i
      }
    }
    
    record CustomConstructor(int i, String s) { 
      CustomConstructor() {  
        this(23, "custom"); // must invoke default all args constructor, otherwise DOES NOT COMPILE: error: constructor is not canonical, so its first statement must invoke another constructor of class CustomConstructor
      }
    }
    
    public static void main(String[] args) throws Exception {
      System.out.println( new CompactConstructor(1, "a") );
      
      System.out.println( new CustomConstructor() );
      
    }
}


