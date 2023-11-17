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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Cube implementation.
 *
 * @author Kyle K. Lin
 *
 * @param <T> Type of value.
 */
public class ListCube<T> implements Cube<T> {

    private final List<Data<T>> data;

    public ListCube(List<Data<T>> data) {
        this.data = data;
    }

    public ListCube(Stream<Data<T>> data) {
        this.data = data.collect(Collectors.toList());
    }

    @Override
    public long count() {
        return this.data.size();
    }

    @Override
    public List<Data<T>> data() {
        return new ArrayList<>(this.data);
    }

    @Override
    public Stream<T> values() {
        return this.data.stream().map(t -> t.value);
    }

    @Override
    public T single() {
        return !this.data.isEmpty() ? this.data.get(0).value : null;
    }

    @Override
    public Map<String, Set<String>> tags(String... tags) {
        return tags(Arrays.asList(tags));
    }

    @Override
    public Map<String, Set<String>> tags(List<String> tags) {
        Map<String, Set<String>> result = new TreeMap<>();
        this.data.forEach(d -> {
            d.getTags().forEach(e -> {
                String k = e.getKey();
                if (!tags.contains(k)) {
                    return;
                }
                Set<String> vs = result.get(k);
                if (vs == null) {
                    vs = new TreeSet<>();
                    result.put(k, vs);
                }
                vs.add(e.getValue());
            });
        });
        return result;
    }

    @Override
    public Map<String, List<Data<T>>> grouping(final String tagName) {
        return this.data.stream().collect(Collectors.groupingBy(d -> d.getTag(tagName)));
    }

    @Override
    public <R> Map<String, List<R>> grouping(String tagName, Function<Data<T>, R> f) {
        return this.data.stream().collect(
                Collectors.groupingBy(
                        d -> d.getTag(tagName),
                        Collectors.mapping(v -> f.apply(v), Collectors.toList())));
    }

    @Override
    public <R> Map<String, R> mapping(String tagName, BiFunction<Data<T>, R, R> f) {
        Map<String, R> result = new TreeMap<>();
        grouping(tagName).forEach((k, vs) -> {
            R r = null;
            for (Data<T> v : vs) {
                r = f.apply(v, r);
            }
            result.put(k, r);
        });
        return result;
    }

    @Override
    public Map<String, T> single(final String tagName) {
        TreeMap<String, T> result = new TreeMap<>();
        grouping(tagName).forEach((k, v) -> result.put(k, v.get(0).value));
        return result;
    }

    @Override
    public <R> Map<String, R> single(String tagName, Function<T, R> f) {
        TreeMap<String, R> result = new TreeMap<>();
        grouping(tagName).forEach((k, v) -> result.put(k, f.apply(v.get(0).value)));
        return result;
    }

    @Override
    public Cube<T> eq(String tagName, String tagValue) {
        return new ListCube<>(this.data.stream().filter(d -> tagValue.equals(d.getTag(tagName))));
    }

    @Override
    public Cube<T> neq(String tagName, String tagValue) {
        return new ListCube<>(this.data.stream().filter(d -> !tagValue.equals(d.getTag(tagName))));
    }

    @Override
    public Cube<T> filter(Function<Data<T>, Boolean> function) {
        return new ListCube<>(this.data.stream().filter(function::apply));
    }

    @Override
    public Map<String, Cube<T>> cubes(String tagName) {
        final TreeMap<String, Cube<T>> result = new TreeMap<>();
        Map<String, List<Data<T>>> raw = this.data.stream().collect(Collectors.groupingBy(d -> d.getTag(tagName)));
        raw.forEach((k, v) -> result.put(k, new ListCube<T>(v)));
        return result;
    }

    @Override
    public Map<String, Cube<T>> cubes(Function<Data<T>, String> f) {
        final TreeMap<String, Cube<T>> result = new TreeMap<>();
        Map<String, List<Data<T>>> raw = this.data.stream().collect(Collectors.groupingBy(f));
        raw.forEach((k, v) -> result.put(k, new ListCube<T>(v)));
        return result;
    }
}
