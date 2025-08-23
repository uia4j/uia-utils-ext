/*******************************************************************************
 * Copyright 2019 UIA
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package uia.utils.cube;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Kyle K. Lin
 *
 */
public class CubeTest {

    @Test
    public void testCase1() {
        CubeBuilder<String> b = new CubeBuilder<>();
        b.add("Kan Lin")
                .addTag("lastName", "Lin")
                .addTag("Sex", "M")
                .addTag("Job", "Engineer");
        b.add("Patrick Lin")
                .addTag("lastName", "Lin")
                .addTag("Sex", "M")
                .addTag("Job", "Project Manager");
        b.add("Qin Lin")
                .addTag("lastName", "Lin")
                .addTag("Sex", "F")
                .addTag("Job", "Student");
        b.add("Yue Lin")
                .addTag("lastName", "Lin")
                .addTag("Sex", "F")
                .addTag("Job", "Student");
        b.add("Charlotte Chang")
                .addTag("lastName", "Chang")
                .addTag("Sex", "F")
                .addTag("Job", "Engineer");
        b.add("Cathy Tsai")
                .addTag("lastName", "Tsai")
                .addTag("Sex", "F")
                .addTag("Job", "Sales");

        Cube<String> c = b.build();

        System.out.println(c.cubes("lastName"));
        System.out.println(c.eq("lastName", "Lin").cubes("Job"));

        Assert.assertEquals(4, c.filter(d -> d.value.contains("Lin")).values().count());
        Assert.assertEquals(1, c.filter(d -> d.value.contains("Lin")).filter(d -> d.value.contains("Kan")).values().count());

        Assert.assertEquals(6, c.values().count());
        Assert.assertEquals(3, c.grouping("lastName").size());
        Assert.assertEquals("Kan Lin", c.single());
        Assert.assertEquals(4, c.single("Job").size());

        Assert.assertEquals(4, c.eq("Sex", "F").values().count());
        Assert.assertEquals(4, c.neq("Job", "Student").values().count());
        Assert.assertEquals(2, c.eq("Sex", "F").neq("Job", "Student").values().count());

        Assert.assertEquals("Cathy Tsai", c.eq("Sex", "F").eq("Job", "Sales").single());
        Map<String, String> m1 = c.eq("Sex", "F").neq("Job", "Student").single("Job");
        Assert.assertTrue(m1.containsKey("Sales"));
        Assert.assertTrue(m1.containsKey("Engineer"));
        Assert.assertFalse(m1.containsKey("Student"));
    }

    @Test
    public void testCase2() {
        CubeBuilder<String> b1 = new CubeBuilder<>();
        b1.add("Kan Lin")
                .addTag("lastName", "Lin")
                .addTag("Sex", "M")
                .addTag("Job", "Engineer");
        b1.add("Patrick Lin")
                .addTag("lastName", "Lin")
                .addTag("Sex", "M")
                .addTag("Job", "Project Manager");

        Cube<String> c1 = b1.build();
        Assert.assertEquals(0, c1.eq("Sex", "F").values().count());
        Assert.assertEquals(0, c1.eq("Sex", "F").grouping("lastName").size());
        Assert.assertEquals(null, c1.eq("Sex", "F").single());

        CubeBuilder<String> b2 = new CubeBuilder<>();
        Cube<String> c2 = b2.build();
        Assert.assertNull(c2.single());
        Assert.assertEquals(0, c2.single("Job").size());
        Assert.assertEquals(0, c2.eq("Sex", "F").values().count());
        Assert.assertEquals(0, c2.eq("Sex", "F").grouping("lastName").size());
    }

    @Test
    public void testCase3() {
        CubeBuilder<String> b1 = new CubeBuilder<>();
        b1.add("sfc1")
                .addTag("op", "op1")
                .addTag("item", "item1")
                .addTag("mask", "mask1");
        b1.add("sfc2")
                .addTag("op", "op1")
                .addTag("item", "item2")
                .addTag("mask", "mask1");
        b1.add("sfc3")
                .addTag("op", "op2")
                .addTag("item", "item1")
                .addTag("mask", "mask2");
        b1.add("sfc4")
                .addTag("op", "op2")
                .addTag("item", "item2")
                .addTag("mask", "mask3");

        Cube<String> c1 = b1.build();
        System.out.println(c1.grouping("op"));
        System.out.println(c1.grouping("item"));
        System.out.println(c1.grouping("mask"));
    }

    @Test
    public void testCase4() {
        CubeBuilder<String> data = new CubeBuilder<>();
        data.add("1,1").addTag("site", "1").addTag("soft", "1").addTag("hard", "1");
        data.add("1,2").addTag("site", "2").addTag("soft", "1").addTag("hard", "1");
        data.add("1,3").addTag("site", "1").addTag("soft", "1").addTag("hard", "1");
        data.add("1,4").addTag("site", "2").addTag("soft", "2").addTag("hard", "2");
        data.add("1,5").addTag("site", "1").addTag("soft", "3").addTag("hard", "2");
        data.add("1,6").addTag("site", "2").addTag("soft", "4").addTag("hard", "4");

        Cube<String> dies = data.build();
        System.out.println(dies.tag("site"));
        System.out.println(dies.tag("soft"));
        System.out.println(dies.tag("hard"));
        dies.cubes("soft").forEach((k, v) -> {
            System.out.println(k + ":");
            System.out.println(" " + v.grouping("site"));
        });

    }
}
