/*
 * 版权属于：yitter(yitter@126.com)
 * 开源地址：https://github.com/yitter/idgenerator
 */
package top.lytree.utils.idgen.core;

import top.lytree.utils.idgen.contract.IdGeneratorException;
import top.lytree.utils.idgen.contract.IdGeneratorOptions;

public class SnowWorkerM2 extends SnowWorkerM1 {

    public SnowWorkerM2(IdGeneratorOptions options) {
        super(options);
    }

    @Override
    public long nextId() {
        synchronized (_SyncLock) {
            long currentTimeTick = GetCurrentTimeTick();

            if (_LastTimeTick == currentTimeTick) {
                if (_CurrentSeqNumber++ > MaxSeqNumber) {
                    _CurrentSeqNumber = MinSeqNumber;
                    currentTimeTick = GetNextTimeTick();
                }
            } else {
                _CurrentSeqNumber = MinSeqNumber;
            }

            if (currentTimeTick < _LastTimeTick) {
                throw new IdGeneratorException("Time error for {0} milliseconds", _LastTimeTick - currentTimeTick);
            }

            _LastTimeTick = currentTimeTick;
            long result = ((currentTimeTick << _TimestampShift) + ((long) WorkerId << SeqBitLength) + (int) _CurrentSeqNumber);

            return result;
        }

    }
}
