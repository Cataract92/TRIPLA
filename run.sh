java -jar lib/jflex-1.6.1.jar -package flex -d src/flex res/tripla.flex
java -jar lib/java-cup-11b-2015.03.26.jar -package cup -parser Parser -symbols Symbols -destdir src/cup/ res/tripla.cup
