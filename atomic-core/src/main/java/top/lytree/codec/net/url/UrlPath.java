package top.lytree.codec.net.url;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import top.lytree.codec.RFC3986;
import top.lytree.collections.CollectionUtils;
import top.lytree.base.Assert;
import top.lytree.lang.StringUtils;

/**
 * URL中Path部分的封装
 *
 * @author looly
 *
 */
public class UrlPath {

    private List<String> segments;
    private boolean withEngTag;

    /**
     * 构建UrlPath
     *
     * @param pathStr 初始化的路径字符串
     * @param charset decode用的编码，null表示不做decode
     * @return UrlPath
     */
    public static UrlPath of(String pathStr, Charset charset) {
        final UrlPath urlPath = new UrlPath();
        urlPath.parse(pathStr, charset);
        return urlPath;
    }

    /**
     * 是否path的末尾加 /
     *
     * @param withEngTag 是否path的末尾加 /
     * @return this
     */
    public UrlPath setWithEndTag(boolean withEngTag) {
        this.withEngTag = withEngTag;
        return this;
    }

    /**
     * 获取path的节点列表
     *
     * @return 节点列表
     */
    public List<String> getSegments() {
        return this.segments;
    }

    /**
     * 获得指定节点
     *
     * @param index 节点位置
     * @return 节点，无节点或者越界返回null
     */
    public String getSegment(int index) {
        if (null == this.segments || index >= this.segments.size()) {
            return null;
        }
        return this.segments.get(index);
    }

    /**
     * 添加到path最后面
     *
     * @param segment Path节点
     * @return this
     */
    public UrlPath add(String segment) {
        addInternal(fixPath(segment), false);
        return this;
    }

    /**
     * 添加到path最前面
     *
     * @param segment Path节点
     * @return this
     */
    public UrlPath addBefore(String segment) {
        addInternal(fixPath(segment), true);
        return this;
    }

    /**
     * 解析path
     *
     * @param path    路径，类似于aaa/bb/ccc或/aaa/bbb/ccc
     * @param charset decode编码，null表示不解码
     * @return this
     */
    public UrlPath parse(String path, Charset charset) {
        if (StringUtils.isNotEmpty(path)) {
            // 原URL中以/结尾，则这个规则需保留，issue#I1G44J@Gitee
            if (StringUtils.endsWith(path, StringUtils.SLASH)) {
                this.withEngTag = true;
            }

            path = fixPath(path);
            if (StringUtils.isNotEmpty(path)) {
                final String[] split = StringUtils.split(path, '/');
                for (String seg : split) {
                    addInternal(URLDecoder.decode(seg, charset), false);
                }
            }
        }

        return this;
    }

    /**
     * 构建path，前面带'/'<br>
     * <pre>
     *     path = path-abempty / path-absolute / path-noscheme / path-rootless / path-empty
     * </pre>
     *
     * @param charset encode编码，null表示不做encode
     * @return 如果没有任何内容，则返回空字符串""
     */
    public String build(Charset charset) {
        if (CollectionUtils.isEmpty(this.segments)) {
            return StringUtils.EMPTY;
        }

        final StringBuilder builder = new StringBuilder();
        for (String segment : segments) {
            if (builder.length() == 0) {
                // 根据https://www.ietf.org/rfc/rfc3986.html#section-3.3定义
                // path的第一部分不允许有":"，其余部分允许
                // 在此处的Path部分特指host之后的部分，即不包含第一部分
                builder.append(StringUtils.SLASH).append(RFC3986.SEGMENT_NZ_NC.encode(segment, charset));
            } else {
                builder.append(StringUtils.SLASH).append(RFC3986.SEGMENT.encode(segment, charset));
            }
        }
        if (StringUtils.isEmpty(builder)) {
            // 空白追加是保证以/开头
            builder.append(StringUtils.SLASH);
        } else if (withEngTag && false == StringUtils.endsWith(builder, StringUtils.SLASH)) {
            // 尾部没有/则追加，否则不追加
            builder.append(StringUtils.SLASH);
        }

        return builder.toString();
    }

    @Override
    public String toString() {
        return build(null);
    }

    /**
     * 增加节点
     *
     * @param segment 节点
     * @param before  是否在前面添加
     */
    private void addInternal(String segment, boolean before) {
        if (this.segments == null) {
            this.segments = new LinkedList<>();
        }

        final String seg = StringUtils.toString(segment);
        if (before) {
            this.segments.add(0, seg);
        } else {
            this.segments.add(seg);
        }
    }

    /**
     * 修正路径，包括去掉前后的/，去掉空白符
     *
     * @param path 节点或路径path
     * @return 修正后的路径
     */
    private static String fixPath(String path) {
        Assert.notNull(path, "Path segment must be not null!");
        if ("/".contentEquals(path)) {
            return StringUtils.EMPTY;
        }

        String segmentStr = StringUtils.trim(path);
        segmentStr = StringUtils.removeStart(segmentStr, StringUtils.SLASH);
        segmentStr = StringUtils.removeEnd(segmentStr, StringUtils.SLASH);
        segmentStr = StringUtils.trim(segmentStr);
        return segmentStr;
    }
}
