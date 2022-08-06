package top.yang.configuration;

import org.springframework.data.jdbc.mybatis.NamespaceStrategy;

public class MybatisNamespaceStrategy implements NamespaceStrategy {

    private final String prefix;
    private final String suffix;

    public MybatisNamespaceStrategy(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    @Override
    public String getNamespace(Class<?> domainType) {
        if (prefix.endsWith(".")) {
            return prefix.concat(domainType.getName()).concat(suffix);
        } else {
            return prefix.concat(".").concat(domainType.getName()).concat(suffix);
        }

    }
}
