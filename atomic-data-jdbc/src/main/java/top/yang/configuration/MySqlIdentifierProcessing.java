package top.yang.configuration;


import org.springframework.data.relational.core.sql.IdentifierProcessing;

class MySqlIdentifierProcessing implements IdentifierProcessing {

    private final Quoting quoting;
    private final LetterCasing letterCasing;

    public MySqlIdentifierProcessing(Quoting quoting, LetterCasing letterCasing) {
        this.quoting = quoting;
        this.letterCasing = letterCasing;
    }

    @Override
    public String quote(String identifier) {
        return quoting.apply(identifier);
    }

    @Override
    public String standardizeLetterCase(String identifier) {
        return letterCasing.apply(identifier);
    }

    enum LetterCasing {

        UPPER_CASE {
            @Override
            public String apply(String identifier) {
                return identifier.toUpperCase();
            }
        },

        LOWER_CASE {
            @Override
            public String apply(String identifier) {
                return identifier.toLowerCase();
            }
        },

        UNDERLINE_CASE {
            @Override
            public String apply(String identifier) {
                return convertToUnderline(identifier);
            }
        },
        AS_IS {
            @Override
            public String apply(String identifier) {
                return identifier;
            }
        };

        abstract String apply(String identifier);
    }

    /**
     * @param name 小驼峰命名法
     * @return 下划线格式
     */
    private static String convertToUnderline(String name) {
        StringBuilder builder = new StringBuilder(name.replace('.', '_'));
        for (int i = 1; i < builder.length() - 1; i++) {
            if (isUnderscoreRequired(builder.charAt(i - 1), builder.charAt(i),
                    builder.charAt(i + 1))) {
                builder.insert(i++, '_');
            }
        }
        return builder.toString().toUpperCase();
    }

    private static boolean isUnderscoreRequired(char before, char current, char after) {
        return Character.isLowerCase(before) && Character.isUpperCase(current)
                && Character.isLowerCase(after);
    }
}
