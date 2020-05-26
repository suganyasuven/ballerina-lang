/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.langlib.array;

import org.ballerinalang.jvm.scheduling.Strand;
import org.ballerinalang.jvm.values.ArrayValue;

import static org.ballerinalang.jvm.values.utils.ArrayUtils.checkIsArrayOnlyOperation;

/**
 * Native implementation of lang.array:unshift((any|error)[], (any|error)...).
 *
 * @since 1.0
 */
//@BallerinaFunction(
//        orgName = "ballerina", packageName = "lang.array", functionName = "unshift",
//        args = {@Argument(name = "arr", type = TypeKind.ARRAY), @Argument(name = "vals", type = TypeKind.ARRAY)},
//        isPublic = true
//)
public class Unshift {

    public static void unshift(Strand strand, ArrayValue arr, ArrayValue vals) {
        checkIsArrayOnlyOperation(arr.getType(), "unshift()");
        arr.unshift(vals);
    }
}
