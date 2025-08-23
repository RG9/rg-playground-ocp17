rm -rf app/out
javac --module-path mods -d app/out app/src/app/*.java app/module-info.java
jar -cvf mods/app.jar -C app/out  .
java -p mods -m app/app.App