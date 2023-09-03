package top.lytree.spring.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author PrideYang
 */
public abstract class BaseHandlerFactory<T> {

    private final Map<String, T> handlers = new HashMap<>();

    public void register(String key, T handler) {
        handlers.put(key, handler);
    }

    public T get(String key) {
        return handlers.get(key);
    }

    public T exist(String key) {
        if (handlers.containsKey(key)) {
            return handlers.get(key);
        } else {
            return getDefaultHandler();
        }
    }

    public void del(String key) {
        this.handlers.remove(key);
    }


    public Set<String> keys() {
        return this.handlers.keySet();
    }

    protected T getDefaultHandler() {
        return null;
    }

}
