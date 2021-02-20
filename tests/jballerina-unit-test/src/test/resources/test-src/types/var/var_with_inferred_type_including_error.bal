// Copyright (c) 2021 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

var [x, y] = foo();
function foo() returns [int, int|error] => [1, 1];

var [a, ...b] = bar();
function bar() returns [error, error...] => [error("one"), error("two"), error("three")];

var [x2, y2] = foo();

var [a2, ...b2] = bar();

var m = error("Error!");

function testVarInVarDeclWithTypeIncludingError() {
    var [x1, y1] = foo();

    var [a1, ...b1] = bar();

    var [x2, y2] = foo();

    var [a2, ...b2] = bar();

    var m1 = error("Error!");

    (any|error)[] arr = [y, a, m, y1, a1, m1];
}

error[] errs = let var e1 = error("one"), var e2 = error("two"), var e3 = [error("three")] in [e1, e2];

function testVarInLetVarWithTypeIncludingError() {
    error[] errs2 = let var e1 = error("one"), var e2 = {a: error("two")}, var e3 = error("three") in [e1, e3];
}

type MyError error<record {error err;}>;

function baz() returns record {[error|boolean, int] q; error r; MyError s; int t;} =>
    {q: [error("four"), 1], r: error("five"), s: error MyError("five", err = error("six")), t: 101};

string str = let var {q: [e, i], r, s: error MyError(err = err), t} = baz() in
    (<error> e).message() + r.message() + err.message() + t.toString();
string str2 = let var {q: [e, i], r, s: error MyError(err = err), t} = baz() in t.toString();

function testVarInBindingPatternWithTypeIncludingError() {
    string str3 = let var {q: [e, i], r, s: error MyError(err = err), t} = baz() in
        (<error> e).message() + r.message() + err.message() + t.toString();
    string str4 = let var {q: [e, i], r, s: error MyError(err = err), t} = baz() in t.toString();
}

function testForEachLoopWithError() {
    foreach var e in errs {

    }

    int tot = 0;
    foreach error e in errs {
        tot += 1;
    }

    tot = 0;
    foreach var error(m, c) in errs {
        tot += 1;
    }
}
