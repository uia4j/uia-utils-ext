package uia.utils.cube;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ListCube<T> implements Cube<T> {

    private final List<Data<T>> data;

    ListCube(List<Data<T>> data) {
        this.data = data;
    }

    @Override
    public T single() {
        return this.data.size() > 0 ? this.data.get(0).value : null;
    }

    @Override
    public Stream<T> values() {
        return this.data.stream().map(t -> t.value);
    }

    @Override
    public Map<String, List<T>> valuesMapping(final String tagName) {
        return this.data.stream().collect(
                Collectors.groupingBy(
                        d -> d.getTag(tagName),
                        Collectors.mapping(v -> v.value, Collectors.toList())));
    }

    @Override
    public <R> Map<String, List<R>> valuesMapping(String tagName, Function<T, R> f) {
        return this.data.stream().collect(
                Collectors.groupingBy(
                        d -> d.getTag(tagName),
                        Collectors.mapping(v -> f.apply(v.value), Collectors.toList())));
    }

    @Override
    public Map<String, T> singleMapping(final String tagName) {
        TreeMap<String, T> result = new TreeMap<String, T>();
        valuesMapping(tagName).forEach((k, v) -> result.put(k, v.get(0)));
        return result;
    }

    @Override
    public <R> Map<String, R> singleMapping(String tagName, Function<T, R> f) {
        TreeMap<String, R> result = new TreeMap<String, R>();
        valuesMapping(tagName).forEach((k, v) -> result.put(k, f.apply(v.get(0))));
        return result;
    }

    @Override
    public Cube<T> select(String tagName, String tagValue) {
        return new StreamCube<T>(this.data.stream().filter(d -> tagValue.equals(d.getTag(tagName))));
    }

    @Override
    public Cube<T> selectNot(String tagName, String tagValue) {
        return new StreamCube<T>(this.data.stream().filter(d -> !tagValue.equals(d.getTag(tagName))));
    }

    @Override
    public Cube<T> select(Function<Data<T>, Boolean> function) {
        return new StreamCube<T>(this.data.stream().filter(function::apply));
    }

    @Override
    public Map<String, Cube<T>> cubes(String tagName) {
        final TreeMap<String, Cube<T>> result = new TreeMap<String, Cube<T>>();
        Map<String, List<Data<T>>> data = this.data.stream().collect(Collectors.groupingBy(d -> d.getTag(tagName)));
        data.forEach((k, v) -> result.put(k, new ListCube<T>(v)));
        return result;
    }
}
