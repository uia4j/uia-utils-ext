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

/**
 * Cube.
 * 
 * @author Kyle K. Lin
 *
 * @param <T> Type of value.
 */
public interface Cube<T> {

	/**
	 * Create cubes grouping by tag.
	 * @param tagName Tag name.
	 * @return Cubes.
	 */
    public Map<String, Cube<T>> cubes(String tagName);

    /**
     * Select subset of cube depending on function.
     * @param function Function.
     * @return Cube.
     */
    public Cube<T> select(Function<Data<T>, Boolean> function);

    /**
     * Select subset of cube depending on tag.
     * @param tagName Tag name.
     * @param tagValue Tag value.
     * @return Cube.
     */
    public Cube<T> select(String tagName, String tagValue);

    /**
     * Select subset of cube depending on tag.
     * @param tagName Tag name.
     * @param tagValue Tag value.
     * @return Cube.
     */
    public Cube<T> selectNot(String tagName, String tagValue);

    /**
     * Get all values in the cube.
     * @return Values.
     */
    public Stream<T> values();

    /**
     * Get values grouping by tag.
     * @param tagName Tag name.
     * @return Values.
     */
    public Map<String, List<T>> valuesMapping(final String tagName);

    /**
     * Get converted values grouping by tag.
     * @param tagName Tag name.
     * @param f Function used to convert value.
     * @param <R> Converted type.
     * @return Values.
     */
    public <R> Map<String, List<R>> valuesMapping(final String tagName, Function<T, R> f);

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
    public Map<String, T> singleMapping(final String tagName);

    /**
     * Get first value grouping by tag.
     * @param tagName Tag name.
     * @param f Function used to convert value.
     * @param <R> Converted type.
     * @return Value.
     */
    public <R> Map<String, R> singleMapping(final String tagName, Function<T, R> f);

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
    }
}
