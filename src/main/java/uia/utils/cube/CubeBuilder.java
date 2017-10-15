/*******************************************************************************
 * Copyright 2017 UIA
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

import java.util.ArrayList;

import uia.utils.cube.Cube.Data;

/**
 * Cube builder.
 * 
 * @author Kyle K. Lin
 *
 * @param <T> Type of value.
 */
public class CubeBuilder<T> {

    private final ArrayList<Data<T>> data;

    /**
     * Constructor.
     */
    public CubeBuilder() {
        this.data = new ArrayList<Data<T>>();
    }

    /**
     * Build a cube.
     * @return Cube.
     */
    public Cube<T> build() {
        return new ListCube<T>(this.data);
    }

    /**
     * Add a new value.
     * @param value Value.
     * @return Data.
     */
    public Data<T> put(T value) {
        Data<T> data = new Data<T>(value);
        this.data.add(data);
        return data;
    }
}
