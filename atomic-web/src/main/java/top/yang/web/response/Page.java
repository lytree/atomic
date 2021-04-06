package top.yang.web.response;

import java.io.Serializable;
import java.util.List;

public class Page<T> implements Serializable {

    //当前默认为第一页
    public static final Integer pageNum = 1;
    //默认每页显示条件
    public static final Integer pageSize = 20;


    //判断当前页是否为空或是小于1
    public static Integer cpn(Integer pageNum) {
        if (null == pageNum || pageNum < 1) {
            pageNum = 1;
        }
        return pageNum;
    }


    // 页数（第几页）
    private long currentPage;

    // 查询数据库里面对应的数据有多少条
    private long total;// 从数据库查处的总记录数

    // 每页显示多少分页标签
    private int size;

    // 下页
    private int next;

    private List<T> list;

    // 最后一页
    private int last;

    private int lpage;

    private int rpage;

    //从哪条开始查
    private long start;

    //全局偏移量
    public int offSize = 2;

    public Page() {
        super();
    }

    /****
     *
     * @param currentPage 当前页
     * @param total 总记录数
     * @param pageSize 每页显示多少条
     */
    public void setCurrentPage(long currentPage, long total, long pageSize) {

        //如果整除表示正好分N页，如果不能整除在N页的基础上+1页
        int totalPages = (int) (total % pageSize == 0 ? total / pageSize : (total / pageSize) + 1);

        //总页数
        this.last = totalPages;

        //判断当前页是否越界,如果越界，我们就查最后一页
        if (currentPage > totalPages) {
            this.currentPage = totalPages;
        } else {
            this.currentPage = currentPage;
        }

        //计算起始页
        this.start = (this.currentPage - 1) * pageSize;
    }

    /****
     * 初始化分页
     * @param total
     * @param currentPage
     * @param pageSize
     */
    public void initPage(long total, int currentPage, int pageSize) {
        //总记录数
        this.total = total;
        //每页显示多少条
        this.size = pageSize;

        //计算当前页和数据库查询起始值以及总页数
        setCurrentPage(currentPage, total, pageSize);

        //分页计算
        int leftcount = this.offSize,    //需要向上一页执行多少次
                rightcount = this.offSize;

        //起点页
        this.lpage = currentPage;
        //结束页
        this.rpage = currentPage;

        //2点判断
        this.lpage = currentPage - leftcount;            //正常情况下的起点
        this.rpage = currentPage + rightcount;        //正常情况下的终点

        //页差=总页数和结束页的差
        int topdiv = this.last - rpage;                //判断是否大于最大页数

        /***
         * 起点页
         * 1、页差<0  起点页=起点页+页差值
         * 2、页差>=0 起点和终点判断
         */
        this.lpage = topdiv < 0 ? this.lpage + topdiv : this.lpage;

        /***
         * 结束页
         * 1、起点页<=0   结束页=|起点页|+1
         * 2、起点页>0    结束页
         */
        this.rpage = this.lpage <= 0 ? this.rpage + (this.lpage * -1) + 1 : this.rpage;

        /***
         * 当起点页<=0  让起点页为第一页
         * 否则不管
         */
        this.lpage = this.lpage <= 0 ? 1 : this.lpage;

        /***
         * 如果结束页>总页数   结束页=总页数
         * 否则不管
         */
        this.rpage = this.rpage > last ? this.last : this.rpage;
    }

    /****
     *
     * @param total   总记录数
     * @param currentPage    当前页
     * @param pagesize    每页显示多少条
     */
    public Page(long total, int currentPage, int pagesize) {
        initPage(total, currentPage, pagesize);
    }

    //上一页
    public long getUpper() {
        return currentPage > 1 ? currentPage - 1 : currentPage;
    }

    //总共有多少页，即末页
    public void setLast(int last) {
        this.last = (int) (total % size == 0 ? total / size : (total / size) + 1);
    }

    /****
     * 带有偏移量设置的分页
     * @param total
     * @param currentPage
     * @param pageSize
     * @param offSize
     */
    public Page(long total, int currentPage, int pageSize, int offSize) {
        this.offSize = offSize;
        initPage(total, currentPage, pageSize);
    }

    public long getNext() {
        return currentPage < last ? currentPage + 1 : last;
    }

    public void setNext(int next) {
        this.next = next;
    }

    public long getCurrentPage() {
        return currentPage;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getLast() {
        return last;
    }

    public long getLpage() {
        return lpage;
    }

    public void setLpage(int lpage) {
        this.lpage = lpage;
    }

    public long getRpage() {
        return rpage;
    }

    public void setRpage(int rpage) {
        this.rpage = rpage;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public void setCurrentPage(long currentPage) {
        this.currentPage = currentPage;
    }

    /**
     * @return the list
     */
    public List<T> getList() {
        return list;
    }

    /**
     * @param list the list to set
     */
    public void setList(List<T> list) {
        this.list = list;
    }

    public static void main(String[] args) {
        //总记录数
        //当前页
        //每页显示多少条
        int cpage = 17;
        Page page = new Page(1001, cpage, 50, 7);
        System.out.println("开始页:" + page.getLpage() + "__当前页：" + page.getCurrentPage() + "__结束页" + page.getRpage() + "____总页数：" + page.getLast());
    }
}