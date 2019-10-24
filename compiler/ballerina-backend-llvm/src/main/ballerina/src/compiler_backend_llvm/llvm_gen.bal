// Copyright (c) 2019 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
//
// WSO2 Inc. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

import ballerina/llvm;
import ballerina/bir;
import ballerina/io;

function genPackage(bir:Package pkg, string targetObjectFilePath, boolean dumpLLVMIR) {
    var mod = createModule(pkg.org, pkg.name, pkg.versionValue);
    genFunctions(mod, pkg.functions);
    io:print(mod);
}

function createModule(bir:Name orgName, bir:Name pkgName, bir:Name ver) returns llvm:LLVMModuleRef {
    var moduleName = orgName.value + pkgName.value + ver.value;
    return llvm:llvmModuleCreateWithName(moduleName);
}

function genFunctions(llvm:LLVMModuleRef mod, bir:Function?[] funcs) {
    var builder = llvm:llvmCreateBuilder();

    map<FuncGenrator> funcGenrators = mapFuncsToNameAndGenrator(mod, builder, funcs);
    genPrintfDeclration(mod);
    io:println("Hello genFunctions !");
}

function readFileFully(string path) returns byte[] = external;

function genPrintfDeclration(llvm:LLVMModuleRef mod) {
    llvm:LLVMTypeRef[] pointer_to_char_type = [llvm:llvmPointerType(llvm:llvmInt8Type(), 0)];
    io:println("pointer_to_char_type : ");
    io:print(pointer_to_char_type);
    llvm:LLVMTypeRef printfType = llvm:llvmFunctionType1(llvm:llvmInt32Type(), pointer_to_char_type, 1, 1);
    llvm:LLVMValueRef printfRef = llvm:llvmAddFunction(mod, "printf", printfType);
}

function mapFuncsToNameAndGenrator(llvm:LLVMModuleRef mod, llvm:LLVMBuilderRef builder, bir:Function?[] funcs)
             returns map<FuncGenrator> {
    map<FuncGenrator> genrators = {};
    foreach var func in funcs {
        if (!(func is ())) {
            FuncGenrator funcGen = new(func, mod, builder);
            genrators[func.name.value] = funcGen;
        }
    }
    return genrators;
}

function genBType(bir:BType? bType) returns llvm:LLVMTypeRef {
    if (bType is bir:BTypeInt) {
        return llvm:llvmInt64Type();
    } else if (bType is bir:BTypeBoolean) {
        return llvm:llvmInt1Type();
    } else if (bType is bir:BTypeNil) {
        return llvm:llvmVoidType();
    } else if (bType is ()) {
        return llvm:llvmVoidType();
    }
    typedesc<any> T = typeof bType;
    error err = error( "Undefined type :" + T.toString());
    panic err;
}
