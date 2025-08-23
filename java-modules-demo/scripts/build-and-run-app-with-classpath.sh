rm -rf app/out
#javac -cp mods/products-0.1-SNAPSHOT.jar -d app/out app/src/app/*.java
javac -cp mods/products-0.1-SNAPSHOT.jar -d app/out app/src/app/*.java app/module-info.java
jar -cvf mods/app.jar -C app/out  .
java -p mods -m app/app.App