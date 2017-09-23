package uia.utils.cube;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamCube<T> implements Cube<T> {

    private final Stream<Data<T>> data;

    StreamCube(Stream<Data<T>> data) {
        this.data = data;
    }

    @Override
    public T single() {
        Optional<Data<T>> opt = this.data.findFirst();
        return opt.isPresent() ? opt.get().value : null;
    }

    @Override
    public Stream<T> values() {
        return this.data.map(t -> t.value);
    }

    @Override
    public Map<String, List<T>> valuesMapping(final String tagName) {
        return this.data.collect(
                Collectors.groupingBy(
                        d -> d.getTag(tagName),
                        Collectors.mapping(v -> v.value, Collectors.toList())));
    }

    @Override
    public Map<String, T> singleMapping(final String tagName) {
        TreeMap<String, T> result = new TreeMap<String, T>();
        valuesMapping(tagName).forEach((k, v) -> result.put(k, v.get(0)));
        return result;
    }

    @Override
    public Cube<T> select(String tagName, String tagValue) {
        return new StreamCube<T>(this.data.filter(d -> tagValue.equals(d.getTag(tagName))));
    }

    @Override
    public Cube<T> selectNot(String tagName, String tagValue) {
        return new StreamCube<T>(this.data.filter(d -> !tagValue.equals(d.getTag(tagName))));
    }

    @Override
    public Cube<T> select(Function<Data<T>, Boolean> function) {
        return new StreamCube<T>(this.data.filter(function::apply));
    }
}
