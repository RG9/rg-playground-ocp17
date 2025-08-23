# module-info.java not included for compilation as we want to make it "automatic module"
javac -d products/out products/src/products/*.java

# note that "products" module name will be extracted from jar name
jar -cvf mods/products-0.1-SNAPSHOT.jar -C products/out  .