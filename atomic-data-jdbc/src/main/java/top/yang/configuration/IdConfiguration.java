package top.yang.configuration;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.event.BeforeSaveCallback;
import top.yang.domain.pojo.BaseEntity;
import top.yang.reflect.FieldUtils;
import top.yang.reflect.ObjectUtils;
import top.yang.utils.SnowFlake;

/**
 * @author pride
 */
@Configuration
public class IdConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(IdConfiguration.class);
    @Autowired
    private SnowFlake snowFlake;

    /**
     * 处理id更新或创建时间
     *
     * @return
     */
    @Bean
    public BeforeSaveCallback<BaseEntity> setCreateTimes() {
        return (baseIdEntity, mutableAggregateChange) -> {
            if (baseIdEntity.getCreateTime() == null) {
                baseIdEntity.setCreateTime(LocalDateTime.now());
            }
            baseIdEntity.setUpdateTime(LocalDateTime.now());
            return baseIdEntity;
        };
    }

    /**
     * 处理id
     *
     * @return
     */
    @Bean
    public BeforeSaveCallback<BaseEntity> setId() {
        return (baseEntity, mutableAggregateChange) -> {
            generateSaveKey(baseEntity);
            return baseEntity;
        };
    }

    private void generateSaveKey(BaseEntity baseEntity) {
        List<Field> fieldsListWithAnnotation = FieldUtils.getFieldsListWithAnnotation(BaseEntity.class, Id.class);
        if (fieldsListWithAnnotation.size() == 1) {
            Field field = fieldsListWithAnnotation.get(0);
            try {
                if (field.getType().getTypeName().equals(Long.class.getTypeName())) {
                    if (ObjectUtils.isEmpty(FieldUtils.readField(field, baseEntity))) {
                        FieldUtils.writeField(field, baseEntity, snowFlake.nextId());
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                logger.error("主键id生成失败", e);
            }
        }
    }

}
