import java.util.*;
import java.util.stream.*;
import java.util.function.*;
import java.text.*;
import java.text.NumberFormat.*;
import java.time.*;
import java.time.format.*;

class Chapter11 {
 
  static record ThrowsOnClosing(String name) implements AutoCloseable {
    public void close() throws Exception { // throws is optional!!
     System.out.println(" - closing java.lang.AutoCloseable: " + name); 
      throw new IllegalArgumentException("closing resource: " + name);
    }
  }
  
  static record ThrowsOnClosingClosable(String name) implements java.io.Closeable { // Closeable extends  AutoCloseable
    public void close() throws java.io.IOException { // throws is optional!!
     System.out.println(" - closing java.io.Closeable: " + name); 
      throw new IllegalArgumentException("closing resource: " + name);
    }
  }
 
  static void tryWithResources() 
   throws Exception // since AutoCloseable#close can throw Exception we must catch it or declared as throws; otherwise DOES NOT COMPILE
  {
    var res2 = new ThrowsOnClosing("res2"); // effectively final
    try(var res1 = new ThrowsOnClosing("res1"); 
        res2; // automatically close res2
        var res3 = new ThrowsOnClosingClosable("res3");
        ThrowsOnClosing res4 = null) { // null is allowed
      System.out.println(" - try block"); 
      throw new IllegalArgumentException("thrown inside try");
    } catch (IllegalArgumentException e) {
      System.out.println(" - catch block"); 
      throw new IllegalArgumentException("rethrowing " + e.getMessage(), e);
    } finally {
      System.out.println(" - finally block"); 
      // throw new IllegalArgumentException("throwing from here will hide any previous exception!!");
    }
  }
 
  public static void main(String[] args) throws Exception {
    System.out.println("CHAPTER 11");
 
    System.out.println("try-with-resources"); 
    try {
      tryWithResources();
    } catch (Exception e){
       System.out.println("Caught: " + e.getMessage());
       for (Throwable t: e.getCause().getSuppressed()) {
          System.out.println("Suppressed: "+t.getMessage());
       }
    } 
    
    System.out.println("formatting"); 
    System.out.println(new DecimalFormat("#,##.000").format(1234.5)); // prints 12,34.500
    System.out.println(new DecimalFormat(".###").format(1234.5)); // prints 1234.5
    // System.out.println(new DecimalFormat("#.#.#").format(1234.5)); // java.lang.IllegalArgumentException: Multiple decimal separators in pattern "#.#.#"
    System.out.println(new DecimalFormat("000.###").format(1)); // prints 001
    
    System.out.println(ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd 'at' HH:mm:ss z (Z)", Locale.ENGLISH)));
      // prints e.g. 2014-02-02 at 21:30:57 CEST (+0200)
      
    // System.out.println(DateTimeFormatter.ofPattern("hh o'clock")); // java.lang.IllegalArgumentException: Unknown pattern letter: o
    System.out.println(DateTimeFormatter.ofPattern("hh 'o''clock'").format(LocalTime.now())); // 12 o'clock (use ' to escape ')
    
    double amount = 120_400.02;
    System.out.println(NumberFormat.getCompactNumberInstance().format(amount)); // 120K
    System.out.println(NumberFormat.getCompactNumberInstance(new Locale("en"), Style.SHORT).format(amount)); // 120K
    System.out.println(NumberFormat.getCompactNumberInstance(new Locale("en"), Style.LONG).format(amount)); // 120 thousand
    System.out.println(NumberFormat.getCurrencyInstance().format(amount)); // $120,400.02
    System.out.println(NumberFormat.getCurrencyInstance(new Locale("pl")).format(amount)); // 120 400,02 ¤
    System.out.println(NumberFormat.getCurrencyInstance(new Locale("pl", "PL")).format(amount)); // 120 400,02 zł

    // Currency.getAvailableCurrencies().stream().forEach(c -> System.out.println(c.getSymbol()));
 
   System.out.println("locale"); 
   System.out.println(new Locale("ab")); // prints ab
   System.out.println(new Locale("cd", "GGGG")); // prints cd_GGGG
   System.out.println(new Locale("CD")); // prints cd
   System.out.println(new Locale("CD", "country")); // prints cd_COUNTRY
  }
 
}


