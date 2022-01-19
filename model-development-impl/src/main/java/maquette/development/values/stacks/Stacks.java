package maquette.development.values.stacks;

import com.google.common.collect.Lists;
import maquette.development.values.exceptions.UnknownStackType;

import java.util.List;
import java.util.Optional;

public class Stacks {

    private static final Stacks INSTANCE = new Stacks();

    List<Stack<?>> stacks;

    private Stacks() {
        var stacks = Lists.<Stack<?>>newArrayList();
        stacks.add(PythonStack.apply());

        this.stacks = List.copyOf(stacks);
    }

    public static Stacks apply() {
        return INSTANCE;
    }

    public Optional<Stack<?>> findStackByName(String name) {
        return stacks
            .stream()
            .filter(s -> s.getName().equals(name))
            .findFirst();
    }

    public Stack<?> getStackByName(String name) {
        return findStackByName(name).orElseThrow(); // TODO mw: Better exception
    }

    @SuppressWarnings("unchecked")
    public <T extends StackConfiguration> Stack<T> getStackByConfiguration(T config) {
        return stacks
            .stream()
            .filter(s -> s.getConfigurationType().isInstance(config))
            .map(s -> (Stack<T>) s)
            .findFirst()
            .orElseThrow(() -> UnknownStackType.apply(config));
    }

    public List<Stack<?>> getStacks() {
        return List.copyOf(stacks);
    }

}
