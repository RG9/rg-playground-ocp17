class Chapter2 {

     public static void main(String[] args){
     // chapters notes
     
     // `f` is required when defining float
     // float y = 2.1; // error: incompatible types: possible lossy conversion from double to float
     float y = 2;
     y = 2.1f;
     y = 2.1F;
     
     // integral are promoted to floating
     var z = (double) 1.0 + (short) 1;
     System.out.println(((Object)z).getClass().getName());
     
     // doesn't compile if assigned value is outside of range of data type
     // byte bb = 200; // error: incompatible types: possible lossy conversion from int to byte
     byte bb = (byte) 200; // = -56; we can cast but then will be overflow
      System.out.println("bb = " + bb);
     
     // instanceof
     Number num = 3; 
     boolean b = num instanceof Number; 
     // b = num instanceof String; // error: incompatible types: Number cannot be converted to String
     b = num instanceof Object; 
     b = num instanceof Double;
     b = num instanceof java.util.concurrent.atomic.AtomicInteger; 
    /*
    Object (java.lang)
      Number (java.lang)
        Float (java.lang)
        BigDecimal (java.math)
        AtomicLong (java.util.concurrent.atomic)
        Long (java.lang)
        Double (java.lang)
        AtomicInteger (java.util.concurrent.atomic)
        Short (java.lang)
        BigInteger (java.math)
        Byte (java.lang)
        Striped64 (java.util.concurrent.atomic)
        Integer (java.lang)
    */
    
      b = null instanceof String;
      b = null instanceof java.util.concurrent.atomic.AtomicInteger;
     
      // review questions
      
      // 1. which operators can be used with boolean
      boolean b1 = true == true;
      b1 = !b1;
      b1 = (boolean) false;
     
      // 2. which data types can be used to assign result of addition
      byte a2 = 1;
      short s2 = 10;
      // short ss2 = a2 + s2; // error: incompatible types: possible lossy conversion from int to short
      int i2 = a2 + s2;
      long l2 = a2 + s2; 
      double d2 = a2 + s2;
      
      // 3.
      long l3 = 10;
      // int i3 = 2 * l3;  // error: incompatible types: possible lossy conversion from long to int
      int i3 = 2 * (int) l3; // answer B: cast to int
      short s3 = 10; // answer C: use short instead long
      i3 = 2 * s3;
      i3 = (int)(2 * l3); // answer D: cast whole operation to int
      long ll3 = 2 * l3; // answer F: assign to long
      
      // 4. XOR 
       System.out.println("XOR true ^ true = " + (true ^ true));
       System.out.println("XOR false ^ false = " + (false ^ false));
       System.out.println("XOR true ^ false = " + (true ^ false));
       System.out.println("XOR false ^ true = " + (false ^ true));
       // 5. Operations precedence
       
       // 6. multi cast
       // why not answer E (doesn't compile because of multi cast)
       byte b6 = 3;
       double d6 = (long)(int)(short)b6;
       d6 = (short)(int)b6;
       d6 = (float)(int)b6;
       d6 = (short)(float)b6;
       // b6 = (long)(int)(short)d6; // error: incompatible types: possible lossy conversion from long to byte
       b6 = (byte)(float)(short)d6;
       // b6 = (byte)(boolean)(int)d6; // error: incompatible types: double cannot be converted to boolean
       b6 = (byte)(float)(short)(float)(int)(int)d6;
       // why answer F
       float f6 = 3;
       // long l6 = (int)d6+f6; // error: incompatible types: possible lossy conversion from float to long
       // long l6 = d6+(int)f6; // error: incompatible types: possible lossy conversion from double to long
       long l6 = (int) (d6 + f6);
       
       // 8. post-increment, post-decrement
       // int a8 = 1++; // error: unexpected type
       int a8 = 1; // error: unexpected type
       int b8 = a8++;
       System.out.println("a8="+a8 + ", b8="+b8);
       b8 = a8--;
       System.out.println("a8="+a8 + ", b8="+b8);
       b8 = ++a8;
       System.out.println("a8="+a8 + ", b8="+b8);
       
       // 9. ternary
       int a9 = 1, b9 = 4, c9  = 2;
       int r9 = a9> b9 ? b9 < c9 ? b9 : 2 : 1;
       // can be written as
       int rr9 = a9> b9 ? (b9 < c9 ? b9 : 2) : 1;
       System.out.println("r9==rr9 = " + (r9 == rr9));
       
       // 10.
       short s10 = 1, ss10 = 3;
       // short sss10 = (byte) s10 + (byte) ss10; // error: incompatible types: possible lossy conversion from int to short
       short sss10 = (short)((byte) s10 + (byte) ss10); // any number operation is always promoted to int, so we have to cast to short
       sss10 = (byte)((byte) s10 + (byte) ss10); // or smaller type
       
       // 14.
       // void v14 = 0; // answer A: error: illegal start of expression
       // boolean b14 = true == 1; // answer B: error: incomparable types: boolean and int
       java.util.function.Predicate<Boolean> shouldEvaluate14 = shouldEvaluate -> {
          if(shouldEvaluate) { System.out.println("right side evaluated despite left being false"); }
          else { throw new IllegalStateException("should not evaluate"); }
          return shouldEvaluate;
       };
       boolean b14 = false & shouldEvaluate14.test(true);
       b14 = false && shouldEvaluate14.test(false);
      // b14 = 0; //  answer F: error: incompatible types: int cannot be converted to boolean
     
     // 15.
      // "? :" is only ternary operator in java 
      
      // 16.
      double d16 = 1.0;
      System.out.println("We can check equality of double and float: " + (d16 == 1.0f));
      System.out.println("We can check equality of double and int: " + (d16 == 1));
      System.out.println("We can check equality of double and long: " + (d16 == 1L));
      float f16 = 1.0f;
      System.out.println("We can check equality of float and double: " + (f16 == d16));
      System.out.println("We can check equality of float and int: " + (f16 == 1));
      System.out.println("We can check equality of float and long: " + (f16 == 1L));
      
      // 19. overflow
      System.out.println("byte max: " + Byte.MAX_VALUE);
      System.out.println("byte min: " + Byte.MIN_VALUE);
      System.out.println("overflow of byte: " + (byte)(Byte.MAX_VALUE + 2)+  " is handled by adding to min value "  + (byte)Byte.MIN_VALUE);
      // byte b19 = 129; // error: incompatible types: possible lossy conversion from int to byte
      byte b19 = (byte) 129;
      System.out.println("overflow of byte when assigning 129: " + b19);
      
      // 21. bitwise complement
      int i21 = ~~8;
      System.out.println("bitwise complement of 8 is calculated by negation and substracting 1 = " + (~i21));
    }
}
