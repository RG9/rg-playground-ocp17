class Chapter4 {

     public static void main(String[] args){
      System.out.println("CHAPTER 4");
      
      // String and int concat
      int five = 5;
      String six = "6";
      System.out.println(1 + five + six); // prints 66
      
      // String substring
      var text = "abcdef";
      System.out.println(text.substring(0)); // prints abcdef
      System.out.println(text.substring(3)); // prints def
      // System.out.println(text.substring(7)); // throws java.lang.StringIndexOutOfBoundsException
      // System.out.println(text.substring(0, 7)); // throws java.lang.StringIndexOutOfBoundsException
      System.out.println(text.substring(0, 6)); // prints abcdef
      System.out.println(text.substring(1, 3)); // prints bc
      // --
      System.out.println(text.substring(1, 1)); // prints ""
      System.out.println(text.substring(6)); // prints ""
      System.out.println(text.substring(6, 6)); // prints ""
      // System.out.println(text.substring(-1)); // throws java.lang.StringIndexOutOfBoundsException
      // System.out.println(text.substring(1, 0)); throws java.lang.StringIndexOutOfBoundsException: begin 1, end 0,
      
      // String trim vs. stip
      text = " \u2000 \n abc \u2000 \t ";
      System.out.println("[" + text.trim() + "]"); // prints [  
 // abc  ]
      System.out.println("[" + text.strip() + "]");  // prints [abc]
      
      text = " \n abc \t ";
      System.out.println("[" + text.trim() + "]"); // prints [abc]
      
      // String indent
      // TODO
    }
}
