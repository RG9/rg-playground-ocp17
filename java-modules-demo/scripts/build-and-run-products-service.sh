# PL
javac --module-path mods -d products-pl/out products-pl/src/pl/products/*.java products-pl/module-info.java
jar -cvf mods/products-pl.jar -C products-pl/out .
