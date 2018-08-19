/*******************************************************************************
 * Copyright 2018 UIA
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

public class CubeTest {

    @Test
    public void testNormal() {
        CubeBuilder<String> b = new CubeBuilder<String>();
        b.put("Kan Lin")
                .addTag("lastName", "Lin")
                .addTag("Sex", "M")
                .addTag("Job", "Engineer");
        b.put("Patrick Lin")
                .addTag("lastName", "Lin")
                .addTag("Sex", "M")
                .addTag("Job", "Project Manager");
        b.put("Qin Lin")
                .addTag("lastName", "Lin")
                .addTag("Sex", "F")
                .addTag("Job", "Student");
        b.put("Yue Lin")
                .addTag("lastName", "Lin")
                .addTag("Sex", "F")
                .addTag("Job", "Student");
        b.put("Charlotte Chang")
                .addTag("lastName", "Chang")
                .addTag("Sex", "F")
                .addTag("Job", "Engineer");
        b.put("Cathy Tsai")
                .addTag("lastName", "Tsai")
                .addTag("Sex", "F")
                .addTag("Job", "Sales");

        Cube<String> c = b.build();

        System.out.println(c.cubes("lastName"));
        System.out.println(c.select("lastName", "Lin").cubes("Job"));

        Assert.assertEquals(4, c.select(d -> d.value.contains("Lin")).values().count());
        Assert.assertEquals(1, c.select(d -> d.value.contains("Lin")).select(d -> d.value.contains("Kan")).values().count());

        Assert.assertEquals(6, c.values().count());
        Assert.assertEquals(3, c.valuesMapping("lastName").size());
        Assert.assertEquals("Kan Lin", c.single());
        Assert.assertEquals(4, c.singleMapping("Job").size());

        Assert.assertEquals(4, c.select("Sex", "F").values().count());
        Assert.assertEquals(4, c.selectNot("Job", "Student").values().count());
        Assert.assertEquals(2, c.select("Sex", "F").selectNot("Job", "Student").values().count());

        Assert.assertEquals("Cathy Tsai", c.select("Sex", "F").select("Job", "Sales").single());
        Map<String, String> m1 = c.select("Sex", "F").selectNot("Job", "Student").singleMapping("Job");
        Assert.assertTrue(m1.containsKey("Sales"));
        Assert.assertTrue(m1.containsKey("Engineer"));
        Assert.assertFalse(m1.containsKey("Student"));
    }

    @Test
    public void testSpecial() {
        CubeBuilder<String> b1 = new CubeBuilder<String>();
        b1.put("Kan Lin")
                .addTag("lastName", "Lin")
                .addTag("Sex", "M")
                .addTag("Job", "Engineer");
        b1.put("Patrick Lin")
                .addTag("lastName", "Lin")
                .addTag("Sex", "M")
                .addTag("Job", "Project Manager");

        Cube<String> c1 = b1.build();
        Assert.assertEquals(0, c1.select("Sex", "F").values().count());
        Assert.assertEquals(0, c1.select("Sex", "F").valuesMapping("lastName").size());
        Assert.assertEquals(null, c1.select("Sex", "F").single());

        CubeBuilder<String> b2 = new CubeBuilder<String>();
        Cube<String> c2 = b2.build();
        Assert.assertNull(c2.single());
        Assert.assertEquals(0, c2.singleMapping("Job").size());
        Assert.assertEquals(0, c2.select("Sex", "F").values().count());
        Assert.assertEquals(0, c2.select("Sex", "F").valuesMapping("lastName").size());
    }
}
