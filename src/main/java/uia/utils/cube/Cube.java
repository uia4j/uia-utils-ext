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

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Stream;

public interface Cube<T> {

    public Map<String, Cube<T>> cubes(String tagName);

    public Cube<T> select(Function<Data<T>, Boolean> function);

    public Cube<T> select(String tagName, String tagValue);

    public Cube<T> selectNot(String tagName, String tagValue);

    public Stream<T> values();

    public Map<String, List<T>> valuesMapping(final String tagName);

    public <R> Map<String, List<R>> valuesMapping(final String tagName, Function<T, R> f);

    public T single();

    public Map<String, T> singleMapping(final String tagName);

    public <R> Map<String, R> singleMapping(final String tagName, Function<T, R> f);

    public static class Data<T> {

        private final TreeMap<String, String> tags;

        public final T value;

        Data(T value) {
            this.tags = new TreeMap<String, String>();
            this.value = value;
        }

        public Data<T> addTag(String tagName, String tagValue) {
            this.tags.put(tagName, tagValue);
            return this;
        }

        public String getTag(String tagName) {
            return this.tags.get(tagName);
        }
    }
}
