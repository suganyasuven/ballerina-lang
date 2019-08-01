import ballerina/java;

public function testIntVarargs_1() returns [int, int, int] {
    return [calculateSum(1, 3, 6), calculateSum(6), calculateSum()];
}

public function testIntVarargs_2() returns int {
    int[] array = [0, 1, 4];
    return calculateSum(...array);
}

public function testIntVarargs_3() returns [int, int] {
    return [calculateMax(9, 1, 3, 6), calculateMax(4, 6)];
}

public function testIntVarargs_4() returns int {
    int[] array = [0, 1, 4];
    return calculateMax(8, 3, ...array);
}

public function testLongVarargs() returns [int, int, int] {
    return [calculateLongSum(1, 3, 6), calculateLongSum(6), calculateLongSum()];
}

public function testGetSumOfIntArrays() returns int {
    handle array1 = getIntArray(2);
    handle array2 = getIntArray(3);
    handle array3 = getIntArray(4);
    return calculateSumOfIntArrays(array1, array2, array3);
}

public function testGetSumOfIntArraysWithAnnot() returns int {
    handle array1 = getIntArray(2);
    handle array2 = getIntArray(3);
    handle array3 = getIntArray(4);
    return calculateSumOfIntArraysWithAnnot(array1, array2, array3);
}

public function testJavaListVarargs() returns handle {
    handle arrayList1 = getArrayList(java:fromString("apples"), java:fromString("arranges"), java:fromString("grapes"));
    handle arrayList2 = getArrayList(java:fromString("pineapple"));
    handle arrayList3 = getArrayList(java:fromString("mangoes"));
    return mergeLists(arrayList1, arrayList2, arrayList3);
}

public function testPrimitiveVarargsWithGenerics() returns handle {
    return asList(3, 6, 9);
}

public function testPasingValueTypeToJavaObject() returns int {
    return toShort(4);
}

// ------------ External functions -------------

public function calculateSum(int... values) returns int = @java:Method {
    name:"getSum",
    class: "org.ballerinalang.test.javainterop.varargs.JavaVarargsTest"
} external;

public function calculateMax(int a, int b, int... values) returns int = @java:Method {
    name:"getMax",
    class: "org.ballerinalang.test.javainterop.varargs.JavaVarargsTest"
} external;

public function calculateLongSum(int... values) returns int = @java:Method {
    name:"getLongSum",
    class: "org.ballerinalang.test.javainterop.varargs.JavaVarargsTest"
} external;

public function getIntArray(int size) returns handle = @java:Method {
    name:"getIntArray",
    class: "org.ballerinalang.test.javainterop.varargs.JavaVarargsTest"
} external;

public function calculateSumOfIntArrays(handle... values) returns int = @java:Method {
    name:"getSumOfIntArrays",
    class: "org.ballerinalang.test.javainterop.varargs.JavaVarargsTest"
} external;

public function getArrayList(handle... values) returns handle = @java:Method {
    name:"getList",
    class:"org.ballerinalang.test.javainterop.varargs.JavaVarargsTest"
} external;

public function mergeLists(handle... values) returns handle = @java:Method {
    name:"merge",
    class:"org.ballerinalang.test.javainterop.varargs.JavaVarargsTest",
    paramTypes:[{elementClass:"java.util.List", dimensions:1}]
} external;

public function calculateSumOfIntArraysWithAnnot(handle... values) returns int = @java:Method {
    name:"getSumOfIntArrays",
    class: "org.ballerinalang.test.javainterop.varargs.JavaVarargsTest",
    paramTypes:[{elementClass:"int", dimensions:2}]
} external;

public function asList(int... values) returns handle = @java:Method {
    name:"asList",
    class: "java.util.Arrays"
} external;

public function toShort(int value) returns int = @java:Method {
    name:"toShort",
    class:"org.ballerinalang.test.javainterop.varargs.JavaVarargsTest"
} external;
