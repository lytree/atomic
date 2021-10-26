package top.yang.configuration;


import org.springframework.data.relational.core.sql.IdentifierProcessing;
import top.yang.string.StringUtils;

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
        return StringUtils.convertToUnderline(identifier);
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

}
