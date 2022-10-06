package top.lytree.text.replacer;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import top.lytree.base.Chain;

/**
 * 字符串替换链，用于组合多个字符串替换逻辑
 *
 * @author looly
 * 
 */
public class ReplacerChain extends StringReplacer implements Chain<StringReplacer, ReplacerChain> {

    private static final long serialVersionUID = 1L;

    private final List<StringReplacer> replacers = new LinkedList<>();

    /**
     * 构造
     *
     * @param stringReplacers 字符串替换器
     */
    public ReplacerChain(StringReplacer... stringReplacers) {
        for (StringReplacer stringReplacer : stringReplacers) {
            addChain(stringReplacer);
        }
    }

    @Override
    public Iterator<StringReplacer> iterator() {
        return replacers.iterator();
    }

    @Override
    public ReplacerChain addChain(StringReplacer element) {
        replacers.add(element);
        return this;
    }

    @Override
    protected int replace(CharSequence str, int pos, StringBuilder out) {
        int consumed = 0;
        for (StringReplacer stringReplacer : replacers) {
            consumed = stringReplacer.replace(str, pos, out);
            if (0 != consumed) {
                return consumed;
            }
        }
        return consumed;
    }

}
