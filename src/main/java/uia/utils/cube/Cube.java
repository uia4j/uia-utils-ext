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

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Cube.
 *
 * @author Kyle K. Lin
 *
 * @param <T> Type of value.
 */
public interface Cube<T> {

    public long count();

    /**
     * Get all data in the cube.
     * @return Values.
     */
    public List<Data<T>> data();

    /**
     * Get all values in the cube.
     * @return Values.
     */
    public Stream<T> values();

    /**
     * Create cubes grouping by tag.
     * @param tagName Tag name.
     * @return Cubes.
     */
    public Map<String, Cube<T>> cubes(String tagName);

    public Map<String, Cube<T>> cubes(Function<Data<T>, String> f);

    public Map<String, Set<String>> tags(String... tags);

    public Map<String, Set<String>> tags(List<String> tags);

    /**
     * Get values grouping by tag.
     * @param tagName Tag name.
     * @return Values.
     */
    public Map<String, List<Data<T>>> grouping(final String tagName);

    public <R> Map<String, List<R>> grouping(final String tagName, Function<Data<T>, R> f);

    public <R> Map<String, R> mapping(final String tagName, BiFunction<Data<T>, R, R> f);

    /**
     * Filter the cube depending on function.
     * @param function Function.
     * @return Cube.
     */
    public Cube<T> filter(Function<Data<T>, Boolean> function);

    /**
     * Select subset of cube depending on tag.
     * @param tagName Tag name.
     * @param tagValue Tag value.
     * @return Cube.
     */
    public Cube<T> eq(String tagName, String tagValue);

    /**
     * Select subset of cube depending on tag.
     * @param tagName Tag name.
     * @param tagValue Tag value.
     * @return Cube.
     */
    public Cube<T> neq(String tagName, String tagValue);

    /**
     * Get first value in cube.
     * @return Value.
     */
    public T single();

    /**
     * Get first value grouping by tag.
     * @param tagName Tag name.
     * @return Values.
     */
    public Map<String, T> single(final String tagName);

    /**
     * Get first value grouping by tag.
     * @param tagName Tag name.
     * @param f Function used to convert value.
     * @param <R> Converted type.
     * @return Value.
     */
    public <R> Map<String, R> single(final String tagName, Function<T, R> f);

    /**
     * Data.
     *
     * @author Kyle K. Lin
     *
     * @param <T> Type of value.
     */
    public class Data<T> {

        private final TreeMap<String, String> tags;

        /**
         * Value.
         */
        public final T value;

        Data(T value) {
            this.tags = new TreeMap<String, String>();
            this.value = value;
        }

        public Set<Entry<String, String>> getTags() {
            return this.tags.entrySet();
        }

        /**
         * Add tag.
         * @param tagName Tag name.
         * @param tagValue Tag value.
         * @return Data.
         */
        public Data<T> addTag(String tagName, String tagValue) {
            this.tags.put(tagName, tagValue);
            return this;
        }

        /**
         * Get tag value.
         * @param tagName Tag name.
         * @return Value.
         */
        public String getTag(String tagName) {
            return this.tags.get(tagName);
        }

        @Override
        public String toString() {
            return this.value.toString();
        }
    }
}
