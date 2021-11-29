Running Directions for LINUX

mkdir simpleComputer
//	---> Put files in simpleComputer

// Make sure that the path settings in MemoryProxy.java are correct:
// That means that memoryClassPaths should direct to one folder above the package
// as shown below
String javaRT = "java";
String memoryClassPaths = "-cp /home/011/v/vy/vyw180000/";
String memoryClassFullName = "simpleComputer/MemoryProto";

cd simpleComputer 	//make sure you are in simpleComputer directory
javac MemoryProxy.java
javac MemoryProto.java

cd 	//Now you should be one level above the simpleComputer folder(package)
javac -cp . simpleComputer/SimpleComputerProto.java

java simpleComputer/SimpleComputerProto simpleComputer/sample1.txt 30
java simpleComputer/SimpleComputerProto simpleComputer/sample2.txt 30
java simpleComputer/SimpleComputerProto simpleComputer/sample3.txt 30
java simpleComputer/SimpleComputerProto simpleComputer/sample4.txt 30
java simpleComputer/SimpleComputerProto simpleComputer/sample5.txt 30
