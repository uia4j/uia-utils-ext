UIA Utils extension
================

[![Download](https://api.bintray.com/packages/uia4j/maven/uia-utils-ext/images/download.svg)](https://bintray.com/uia4j/maven/uia-utils-ext/_latestVersion)
[![Build Status](https://travis-ci.org/uiaj4/uia-utils-ext.svg?branch=master)](https://travis-ci.org/uia4j/uia-utils-ext)
[![codecov](https://codecov.io/gh/uia4j/uia-utils-ext/branch/master/graph/badge.svg)](https://codecov.io/gh/uia4j/uia-utils-ext)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/4466fb1ecba5481691e8befeacb27d32)](https://www.codacy.com/app/gazer2kanlin/uia-utils-ext?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=uia4j/uia-utils-ext&amp;utm_campaign=Badge_Grade)
[![License](https://img.shields.io/github/license/uia4j/uia-utils-ext.svg)](LICENSE)


Some useful solution for system integration. Need JDK 1.8 or above.

The package incldues 2 sub packages:

* Cube - Multi dimension to query and view data.
* States - __State machine pattern__ implementation.

Remark: `uia.utis.dao` in 0.3.x is moved to new porject [uia-dao](https://github.com/uia4j/uia-dao).

## Cube
### key classes
* CubeBuilder
* Cube

```java
CubeBuilder builder = new CubeBuilder();
builder.put("Kyle Lin")
       .addTag("firstName", "Kan")
       .addTag("lasttName", "Lin")
       .addTag("sex", "M")
       .addTag("job", "Engineer");
builder.put("Patrick Lin")
       .addTag("firstName", "Wei")
       .addTag("lasttName", "Lin")
       .addTag("sex", "M")
       .addTag("job", "Manager");
builder.put("Avril Lin")
       .addTag("firstName", "Qin")
       .addTag("lasttName", "Lin")
       .addTag("sex", "F")
       .addTag("job", "Student");
builder.put("Amber Lin")
       .addTag("firstName", "Yue")
       .addTag("lasttName", "Lin")
       .addTag("sex", "F")
       .addTag("job", "Student");
builder.put("Charlotte Chang")
       .addTag("firstName", "Chiayu")
       .addTag("lasttName", "Chang")
       .addTag("sex", "F")
       .addTag("job", "Engineer");
builder.put("Cathy Tsa")
       .addTag("firstName", "Chiahwei")
       .addTag("lasttName", "Tsai")
       .addTag("sex", "F")
       .addTag("job", "Sales");

Cube cube = builder.build();
cube.select("job", "Student"); // Avril, Amber
cube.select("sex", "F").valuesMapping("job"); // Student, Engineer, Sales
```

## States
### key classes
* StateMachine
* AbstractStateWorker

```java
public class MyWokrer extends AbstractStateWorker {

  @Override
  protected void initial() {
    this.stateMachine.register("STATE1")
        .addEvent("E2", (c, a) -> return "STATE2")
        .addEvent("E3", (c, a) -> return "STATE3");
    this.stateMachine.register("STATE2")
        .addEvent("E3", (c, a) -> return "STATE3");
    this.stateMachine.register("STATE3");
  }
}

MyWokrer worker = new MyWokrer();
worker.changeState("STATE1");
worker.run("E2", value);  // STATE2
worker.run("E3", value);  // STATE3
worker.run("E2", value);  // STATE3
```

## Copyright and License

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
