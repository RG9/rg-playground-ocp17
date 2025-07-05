import java.util.*;

class Chapter9 {

  static class Foo {
    List<String> bar(List<String> a) {   System.out.println("Foo"); return null; }
  }
  
  static class BarOverride extends Foo {
    @Override
     List<String> bar(List<String> a) {   System.out.println("BarOverride"); return null; } // ok
    // ArrayList<String> bar(List<String> a) {  System.out.println("BarOverride"); return null; } // ok
    // List<? extends String> bar(List<String> a) {  System.out.println("BarOverride"); return null; } // DOES NOT COMPILE
    // Object bar(List<String> a) {   System.out.println("BarOverride"); return null; } // DOES NOT COMPILE
    // List bar(List<String> a) {   System.out.println("BarOverride"); return null; } // ok
    // --
    // List<String> bar(ArrayList<String> a) {   System.out.println("BarOverride"); return null; } // DOES NOT COMPILE
    // List<String> bar(List<? extends String> a) {   System.out.println("BarOverride"); return null; } // DOES NO COMPILE
    // List<String> bar(List<?> a) {   System.out.println("BarOverride"); return null; } // DOES NOT COMPILE
    // List<String> bar(List a) {   System.out.println("BarOverride"); return null; } // ok
  }
   
  
  static class BarOverwrite2 extends Foo {
    List<String> bar(ArrayList<String> a) {   System.out.println("BarOverwrite2"); return null; }
  }
  
  static record BothComparableAndComparator(int i) implements Comparable<BothComparableAndComparator>, Comparator<BothComparableAndComparator> {
    public int compareTo(BothComparableAndComparator that){ return this.i - that.i; } // MUST BE PUBLIC
    public int compare(BothComparableAndComparator b1, BothComparableAndComparator b2) { return b2.compareTo(b1); } // reverse
  }
  
  public static void main(String[] args) throws Exception {
    System.out.println("CHAPTER 9");
     
    // VALID COLLECTION DEFINITIONS
    // HashSet<Number> hs = new HashSet<Integer>(); incompatible types: HashSet<Integer> cannot be converted to HashSet<Number>
    HashSet<? extends Number> hs = new HashSet<Integer>();
    Map<String, ? extends Number> hm = new HashMap<String, Integer>();
    // HashSet<? super Number> hs = new HashSet<Integer>(); error: incompatible types: HashSet<Integer> cannot be converted to HashSet<? super Number>
    HashSet<? super Number> hs2 = new HashSet<Number>();
    HashSet<? super ClassCastException> set = new HashSet<Exception>();
    
    // List<> list = new ArrayList<String>(); // error: illegal start of type
    // List<Object> values = new HashSet<Object>(); // incompatible types: HashSet<Object> cannot be converted to List<Object>
    //  List<Object> objects = new ArrayList<? extends Object>(); // : error: unexpected type <? extends Object>
    List<Object> objects = new ArrayList(); // warn:  uses unchecked or unsafe operation
    List<String> obj1 = new ArrayList();
    List obj2 = new ArrayList<String>();
    var obj3 = new ArrayList();
    
    // COLLECTION AS PARAMETER AND RETURN TYPE
    new BarOverride().bar(List.of());
    
    System.out.println("COLLECTION METHODS");
    Collection<Integer> collection = new ArrayList<>();
    collection.add(1);
    System.out.println(collection);
    collection.addAll(List.of(2, 3, 4, 5, 6, 7, 8, 9, 10));
    System.out.println(collection);
    // --
    System.out.println("contains 2: "+ collection.contains(2));
    System.out.println("contains 22: "+ collection.contains(22));
    System.out.println("contains 1, 2: "+ collection.containsAll(List.of(1, 2)));
    System.out.println("contains 0, 1, 2: "+ collection.containsAll(List.of(0, 1, 2)));
    // --
    collection.remove(3);
    System.out.println(collection);
    collection.removeAll(List.of(1, 2));
    System.out.println(collection);
    collection.retainAll(List.of(4, 5, 6)); // remove all elements that are not int the collection
    System.out.println(collection);
    collection.removeIf(integer -> integer == 4);
    System.out.println(collection);
    collection.clear();
    System.out.println(collection);
    
    System.out.println("QUEUE SPECIFIC METHODS");
    Queue<Integer> queue = new LinkedList<>();
    System.out.println("add: " + queue.add(1));
    System.out.println(queue);
    System.out.println("offer: " + queue.offer(2)); // in contrary to add(..) doesn't throw IllegalStateException when queue reached its capacity
    System.out.println(queue);
    // --
    System.out.println(queue.peek()); // head of queue
    System.out.println(queue.element()); // in contrary to peek() doesn't throw exception when queue is empty
    // --
    System.out.println("remove:  " + queue.remove()); // removes head
    System.out.println(queue);
    System.out.println(queue.peek());
    System.out.println(queue.element());
    System.out.println("poll: " + queue.poll()); // in contrary to remove() doesn't throw exception when queue is empty
    System.out.println(queue);
    System.out.println(queue.peek());
    // System.out.println(queue.element()); // throws NoSuchElementException
    System.out.println("poll: " + queue.poll());
    // System.out.println("remove:  " + queue.remove()); // throws NoSuchElementException
    
    System.out.println("MAP METHODS");
    Map<Integer, String> map = new HashMap<>();
    System.out.println("put 1a: prev value: " + map.put(1, "1a")); // prints null as prev value
    System.out.println("put 2a, prev value: " + map.put(1, "1b")); // prints 1a as prev value
    map.putAll(Map.of(1, "1c", 2, "2")); 
    map.putIfAbsent(1, "1d");
    map.putIfAbsent(3, "3");
    System.out.println(map); // prints {1=1c, 2=2, 3=3}
    // --
    // map.merge(1, null, (v1, v2) -> null); NPE
    map.merge(1, "", (v1, v2) -> null); // removes key 1
    System.out.println(map); // prints {2=2, 3=3}
    map.merge(2, "2m", (v1, v2) -> v1); // no change - keeps old value
    System.out.println(map); // prints {2=2, 3=3}
    map.merge(2, "2m", (v1, v2) -> v2);
    System.out.println(map); // prints {2=2m, 3=3}
    map.put(2, null);
    System.out.println(map); // {2=null, 3=3}
    map.merge(2, "2m", (v1, v2) -> v1); // merge function is not called if existing value is NULL !!!
    System.out.println(map); // prints {2=2m, 3=3}
    // ...
    
    System.out.println("LIST");
    List<Integer> list = new ArrayList<>(List.of(1, 2, 3, 4, 5));
    System.out.println(list);
    list.remove(0); // removes element with index 0
    System.out.println(list);
    list.remove(new Integer(0)); // no element "0", haven't removed anything
    System.out.println(list);
    

    // IMMUTABLE LIST
    List<Integer> ints = List.of(1, 2);
    // ints.add(3); // Exception in thread "main" java.lang.UnsupportedOperationException
    // ints.remove(1); // Exception in thread "main" java.lang.UnsupportedOperationException

    System.out.println("COMPARATOR");
    String[] values = { "A", "1", "a" };
    Arrays.sort(values);
    System.out.println(Arrays.toString(values)); // prints [1, A, a]
    
    Arrays.sort(values, new Comparator<String>() {
      public int compare(String a, String b) {
        return b.compareTo(a); // reverse
      }
    });
    // or 
    // Arrays.sort(values, (a, b) -> b.compareTo(a));
    // Arrays.sort(values, Comparator.reverseOrder());
    System.out.println("reverse: " + Arrays.toString(values)); // prints [a, A, 1]
    
    Arrays.sort(values, new Comparator<String>() {
      public int compare(String a, String b) {
        return a.compareTo(b);
      }
    });
    // or 
    // Arrays.sort(values, Comparator.naturalOrder());
    System.out.println(Arrays.toString(values)); // prints [1, A, a]
    
    var treeset = new TreeSet<BothComparableAndComparator>(new BothComparableAndComparator(123));
    treeset.add(new BothComparableAndComparator(455));
    treeset.add(new BothComparableAndComparator(300));
    System.out.println(treeset); // prints 455, 300
  }
 
}


