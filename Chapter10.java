import java.util.*;
import java.util.stream.*;
import java.util.function.*;

class Chapter10 {
 
  public static void main(String[] args) throws Exception {
    System.out.println("CHAPTER 10");
 
    System.out.println("Infinite stream");
    Stream.iterate(1, i -> i + 1).limit(10)
        .forEach(i -> System.out.print(i +",")); // prints 1,2,3,4,5,6,7,8,9,10,
    System.out.println("");   
    
    Stream.iterate(1, i -> i <= 10, i -> i + 1)
        .forEach(i -> System.out.print(i +",")); // prints 1,2,3,4,5,6,7,8,9,10,   
    System.out.println("");    
    
    Stream.generate(() -> new Random().nextInt(10)).limit(10)
        .forEach(i -> System.out.print(i +",")); // prints random numbers 
    System.out.println("");  
    
    System.out.println("Streams are lazily evaluated!!");
    var ints = new ArrayList<Integer>();
    ints.add(1); 
    var intsStream = ints.stream();
    ints.add(2);
    System.out.println(intsStream.count()); // prints 2 !!
    
    System.out.println("There can be only one terminal operation!!");
    var stream = Stream.iterate(0, i -> i+1).limit(10);
    var m1 = stream.noneMatch(i -> i < 0);
    // var m2 = stream.anyMatch(i -> i > 5); // throws java.lang.IllegalStateException: stream has already been operated upon or closed
    var m2 = true;
    System.out.println(m1 + " | " + m2);
   
    var infiniteStream = Stream.generate(() -> "infinite");
    // System.out.println("infinite anyMatch: " + infiniteStream.anyMatch(String::isEmpty)); // program hangs 
    System.out.println("infinite allMatch: " + infiniteStream.allMatch(String::isEmpty));
    
    System.out.println("Ways to create primitive int stream");
    var intStreamEmpty = IntStream.empty();
    var intStreamOfValues = IntStream.of(1, 2, 3);
    var intStreamOfSingleValue = IntStream.of(1);
    var intStreamRange = IntStream.range(0, 10);
    var intStreamRangeClosed = IntStream.rangeClosed(0, 10);
    var intStreamIterateInfinite = IntStream.iterate(1, i -> i + 1);
    var intStreamGenerateInfinite = IntStream.generate(new Random()::nextInt);
    var intStreamUnboxed = Stream.of(1, 2, 3).mapToInt(Integer::intValue);
    
    System.out.println("Return types");
    OptionalInt optionalInt = IntStream.empty().findFirst();
    int sum = IntStream.empty().sum();
    OptionalDouble optionalDouble = IntStream.empty().average();
    if(optionalDouble.isPresent()) {
      System.out.println(optionalDouble.getAsDouble());
    }
    System.out.println(IntStream.empty().summaryStatistics().getAverage()); // prints 0.0
    
    // prints IntSummaryStatistics{count=3, sum=6, min=1, average=2.000000, max=3}
    System.out.println(intStreamOfValues.summaryStatistics());
  }
 
}


