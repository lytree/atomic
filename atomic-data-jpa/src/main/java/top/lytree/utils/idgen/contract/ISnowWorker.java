/*
 * 版权属于：yitter(yitter@126.com)
 * 开源地址：https://github.com/yitter/idgenerator
 */
package top.lytree.utils.idgen.contract;

public interface ISnowWorker {
    long nextId() throws IdGeneratorException;
}
