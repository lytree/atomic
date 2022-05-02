package top.yang.repository.impl;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import top.yang.annotations.ComplexCondition;
import top.yang.annotations.ComplexConditionSign;
import top.yang.annotations.Ignore;
import top.yang.annotations.NullHandler;
import top.yang.annotations.OrderBy;
import top.yang.annotations.OrderGroup;
import top.yang.exception.ExceptionUtils;
import top.yang.exceptions.UnknownComplexConditionClassException;
import top.yang.exceptions.UnknownComplexConditionSignException;
import top.yang.model.query.support.BaseQuery;
import top.yang.reflect.FieldUtils;
import top.yang.repository.BaseJpaRepository;
import top.yang.spring.exception.PojoInstanceFailException;
import top.yang.time.TemporalAccessorUtil;


public class BaseJpaRepositoryImpl<DOMAIN, ID> extends SimpleJpaRepository<DOMAIN, ID>
        implements BaseJpaRepository<DOMAIN, ID> {

    private final Logger logger = LoggerFactory.getLogger(BaseJpaRepositoryImpl.class);
    private final JpaEntityInformation<DOMAIN, ID> entityInformation;

    private final EntityManager entityManager;
    private Class pojoClass = null;

    public BaseJpaRepositoryImpl(JpaEntityInformation<DOMAIN, ID> entityInformation,
            EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityInformation = entityInformation;
        this.entityManager = entityManager;
        Class c = this.getClass();
        Type t = c.getGenericSuperclass();
        if (t instanceof ParameterizedType) {
            Type[] p = ((ParameterizedType) t).getActualTypeArguments();
            pojoClass = (Class) p[0];
        }
    }

    /**
     * Executes a count query and transparently sums up all values returned.
     *
     * @param query must not be {@literal null}.
     * @return count
     */
    private static long executeCountQuery(TypedQuery<Long> query) {

        Assert.notNull(query, "TypedQuery must not be null!");

        List<Long> totals = query.getResultList();
        long total = 0L;

        for (Long element : totals) {
            total += element == null ? 0 : element;
        }

        return total;
    }

    /**
     * Finds all domain by id list and the specified sort.
     *
     * @param ids  id list of domain must not be null
     * @param sort the specified sort must not be null
     * @return a list of domains
     */
    @Override
    public List<DOMAIN> findAllByIdIn(Collection<ID> ids, Sort sort) {
        Assert.notNull(ids, "The given Collection of Id's must not be null!");
        Assert.notNull(sort, "Sort info must nto be null");

        logger.debug("Customized findAllById method was invoked");

        if (!ids.iterator().hasNext()) {
            return Collections.emptyList();
        }

        if (entityInformation.hasCompositeId()) {
            List<DOMAIN> results = new ArrayList<>();
            ids.forEach(id -> super.findById(id).ifPresent(results::add));
            return results;
        }

        ByIdsSpecification<DOMAIN> specification = new ByIdsSpecification<>(entityInformation);
        TypedQuery<DOMAIN> query = super.getQuery(specification, sort);
        return query.setParameter(specification.parameter, ids).getResultList();
    }

    @Override
    public Page<DOMAIN> findAllByIdIn(Collection<ID> ids, Pageable pageable) {
        Assert.notNull(ids, "The given Collection of Id's must not be null!");
        Assert.notNull(pageable, "Page info must nto be null");

        if (!ids.iterator().hasNext()) {
            return new PageImpl<>(Collections.emptyList());
        }

        if (entityInformation.hasCompositeId()) {
            throw new UnsupportedOperationException(
                    "Unsupported find all by composite id with page info");
        }

        ByIdsSpecification<DOMAIN> specification = new ByIdsSpecification<>(entityInformation);
        TypedQuery<DOMAIN> query =
                super.getQuery(specification, pageable).setParameter(specification.parameter, ids);
        TypedQuery<Long> countQuery = getCountQuery(specification, getDomainClass())
                .setParameter(specification.parameter, ids);

        return pageable.isUnpaged()
                ? new PageImpl<>(query.getResultList())
                : readPage(query, getDomainClass(), pageable, countQuery);
    }

    /**
     * Deletes by id list.
     *
     * @param ids id list of domain must not be null
     * @return number of rows affected
     */
    @Override
    @Transactional
    public long deleteByIdIn(Collection<ID> ids) {

        logger.debug("Customized deleteByIdIn method was invoked");
        // Find all domains
        List<DOMAIN> domains = findAllById(ids);

        // Delete in batch
        deleteAllInBatch(domains);

        // Return the size of domain deleted
        return domains.size();
    }

    @Override
    public Page<DOMAIN> findByCondition(BaseQuery query, Pageable pageable) {

//        ValidatorWapper.from(page, "page").isNotNull().numerical().min(0);
//        ValidatorWapper.from(pageSize, "pageSize").isNotNull().numerical().min(0);

        // 将空字符串转换成null
        convertEmptyStringToNull(query);

        List<Sort.Order> orders = buildOrder(query);

        DOMAIN queryParams = generatePojo(this.pojoClass);

        BeanUtils.copyProperties(queryParams, query);

        Example<DOMAIN> example = Example.of(queryParams);

        return super.findAll(example, pageable);
    }

    @Override
    public DOMAIN findOneByCondition(BaseQuery query) {

        DOMAIN queryParams = generatePojo(this.pojoClass);

        // 将空字符串转换成null
        convertEmptyStringToNull(query);

        BeanUtils.copyProperties(queryParams, query);

        Example<DOMAIN> example = Example.of(queryParams);

        return (DOMAIN) super.findOne(example);
    }

    @Override
    public Page<DOMAIN> findByComplexCondition(BaseQuery queryReq, Pageable pageable) {

//        ValidatorWapper.from(page, "page").isNotNull().numerical().min(0);
//        ValidatorWapper.from(pageSize, "pageSize").isNotNull().numerical().min(0);

        // 将空字符串转换成null
        convertEmptyStringToNull(queryReq);

        List<Sort.Order> orders = buildOrder(queryReq);
        pageable.getSortOr(Sort.by(orders));
        Specification<DOMAIN> complexConditions = Specification.where(new CommonSpecification(queryReq));

        return super.findAll(complexConditions, pageable);
    }

    @Override
    public Long countByComplexCondition(BaseQuery queryReq) {

        // 将空字符串转换成null
        convertEmptyStringToNull(queryReq);

        Specification<DOMAIN> complexConditions = Specification.where(new CommonSpecification(queryReq));

        return super.count(complexConditions);

    }

    protected <S extends DOMAIN> Page<S> readPage(TypedQuery<S> query, Class<S> domainClass,
            Pageable pageable, TypedQuery<Long> countQuery) {

        if (pageable.isPaged()) {
            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }

        return PageableExecutionUtils.getPage(query.getResultList(), pageable,
                () -> executeCountQuery(countQuery));
    }

    private DOMAIN generatePojo(Class pojoClass) {

        try {
            return (DOMAIN) BeanUtils.instantiateClass(pojoClass);
        } catch (Exception e) {
            logger.error("pojo对象初始化异常,对象为：" + pojoClass.getSimpleName(), e);
            throw new PojoInstanceFailException();
        }

    }

    /**
     * @methodName: transformNullHandler
     * @description: 转换空值处理
     * @author: xiangfeng@biyouxinli.com.cn
     * @date: 2018/7/30
     **/
    private Sort.NullHandling transformNullHandler(String nullHandler) {
        if (NullHandler.FIRST.equals(nullHandler)) {
            return Sort.NullHandling.NULLS_FIRST;
        } else if (NullHandler.LAST.equals(nullHandler)) {
            return Sort.NullHandling.NULLS_LAST;
        } else {
            return Sort.NullHandling.NATIVE;
        }
    }

    /**
     * @methodName: buildOrder
     * @description: 构建排序对象
     * @author: xiangfeng@biyouxinli.com.cn
     * @date: 2018/7/30
     **/
    private List<Sort.Order> buildOrder(BaseQuery query) {
        List<Sort.Order> orders = new ArrayList<>();
        OrderGroup group = AnnotationUtils.findAnnotation(query.getClass(), OrderGroup.class);
        // 判断空指针
        if (null == group) {
            return orders;
        }

        for (int i = 0; i < group.orders().length; i++) {
            OrderBy orderBy = group.orders()[i];
            String target = orderBy.target();
            Sort.Direction direction = Sort.Direction.fromString(orderBy.orderSign());
            Sort.NullHandling nullHandling = transformNullHandler(orderBy.nullHandler());

            Sort.Order order = new Sort.Order(direction, target, nullHandling);
            orders.add(order);

        }
        return orders;

    }

    private void convertEmptyStringToNull(BaseQuery query) {
        Field[] fields = FieldUtils.getAllFields(query.getClass());

        Arrays.stream(fields).sequential().forEach(reflectField -> {
            try {
                if (String.class.equals(reflectField.getGenericType()) && ObjectUtils.isEmpty(reflectField.get(query))) {
                    reflectField.set(query, null);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });

    }

    private static final class ByIdsSpecification<DOMAIN> implements Specification<DOMAIN> {

        private static final long serialVersionUID = 1L;
        private final JpaEntityInformation<DOMAIN, ?> entityInformation;
        @Nullable
        ParameterExpression<Collection> parameter;

        ByIdsSpecification(JpaEntityInformation<DOMAIN, ?> entityInformation) {
            this.entityInformation = entityInformation;
        }

        @Override
        public Predicate toPredicate(Root<DOMAIN> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            Path<?> path = root.get(this.entityInformation.getIdAttribute());
            this.parameter = cb.parameter(Collection.class);
            return path.in(this.parameter);
        }
    }

    /**
     * @author xiangfeng@yintong.com.cn
     * @className: CommonSpecification
     * @description: 通用的复杂查询类
     * @date 2018/5/3
     */
    private final class CommonSpecification implements Specification<DOMAIN> {

        private final BaseQuery query;

        public CommonSpecification(BaseQuery query) {
            this.query = query;
        }

        @Override
        public Predicate toPredicate(Root<DOMAIN> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            Field[] fields = FieldUtils.getAllFields(query.getClass());

//            List<ReflectField> fields = Reflect.on(baseDto).fields();
            List<Predicate> predicates = new ArrayList<>();

            Arrays.stream(fields).filter(field -> {
                        try {
                            return null != FieldUtils.readField(field, query);
                        } catch (IllegalAccessException e) {
                            logger.error("未查找到 {} 字段 : {}", field.getName(), ExceptionUtils.getStackTrace(e));
                            e.printStackTrace();
                        }
                        return false;
                    })
                    .forEach(field -> {
                        try {
                            if (field.isAnnotationPresent(Ignore.class)) {
                                return;
                            }

                            ComplexCondition complexCondition = AnnotationUtils.findAnnotation(query.getClass(), ComplexCondition.class);

                            Object value = FieldUtils.readField(field, query);

                            if (null == value) {
                                return;
                            }
                            if (null == complexCondition) {
                                predicates.add(cb.equal(root.get(field.getName()), value));
                            } else {
                                Predicate predicate = buildComplexCondition(field, complexCondition, root, cb);
                                if (null != predicate) {
                                    predicates.add(predicate);

                                }
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    });

            query.where(cb.and(predicates.toArray(new Predicate[]{})));

            return query.getRestriction();
        }


        private Predicate buildComplexCondition(Field field, ComplexCondition complexCondition, Root<DOMAIN> root, CriteriaBuilder cb) throws IllegalAccessException {

            Object value = FieldUtils.readField(field, root);

            Predicate result = null;
            if (value instanceof TemporalAccessor) {
                Expression path = buildPath(complexCondition, field, root);
                result = buildResult(complexCondition, cb, path, new Timestamp(TemporalAccessorUtil.toEpochMilli((TemporalAccessor) value)));
            } else if (value instanceof Integer) {
                Expression path = buildPath(complexCondition, field, root);
                result = buildResult(complexCondition, cb, path, (Integer) value);
            } else if (value instanceof Long) {
                Expression path = buildPath(complexCondition, field, root);
                result = buildResult(complexCondition, cb, path, (Long) value);
            } else if (value instanceof List || value instanceof String) {
                Expression path = buildPath(complexCondition, field, root);
                if (null == path) {
                    result = null;
                } else {
                    result = buildExtendsResult(complexCondition, cb, path, value);
                }
            } else {
                throw new UnknownComplexConditionClassException();
            }
            return result;

        }

        /**
         * @className: buildResult
         * @description: 构建复杂条件表达式结果
         * @author xiangfeng@yintong.com.cn
         * @date 2018/5/3
         */
        private <Y extends Comparable<? super Y>> Predicate buildResult(ComplexCondition complexCondition, CriteriaBuilder cb, Expression<Y> path, Y value) {

            Predicate result = null;
            if (ComplexConditionSign.EQ.equals(complexCondition.sign())) {
                result = cb.equal(path, value);
            } else if (ComplexConditionSign.LT.equals(complexCondition.sign())) {
                result = cb.lessThan(path, value);
            } else if (ComplexConditionSign.LTEQ.equals(complexCondition.sign())) {
                result = cb.lessThanOrEqualTo(path, value);
            } else if (ComplexConditionSign.GT.equals(complexCondition.sign())) {
                result = cb.greaterThan(path, value);
            } else if (ComplexConditionSign.GTEQ.equals(complexCondition.sign())) {
                result = cb.greaterThanOrEqualTo(path, value);
            } else if (ComplexConditionSign.NOTEQ.equals(complexCondition.sign())) {
                result = cb.notEqual(path, value);
            } else {
                throw new UnknownComplexConditionSignException();
            }
            return result;

        }

        private Predicate buildExtendsResult(ComplexCondition complexCondition, CriteriaBuilder cb, Expression path, Object value) {
            Predicate result = null;
            if (ComplexConditionSign.IN.equals(complexCondition.sign())) {
                if (((List) value).isEmpty()) {
                    return null;
                }
                result = cb.in(path);
                for (int i = 0; i < ((List) value).size(); i++) {
                    Object o = ((List) value).get(i);
                    ((CriteriaBuilder.In) result).value(o);
                }
            } else if (ComplexConditionSign.LIKE.equals(complexCondition.sign())) {
                result = cb.like(path, value.toString());
            } else {
                throw new UnknownComplexConditionSignException();
            }

            return result;
        }

        /**
         * @className: buildPath
         * @description: 构建复杂条件表达式对象路径（名称）
         * @author xiangfeng@yintong.com.cn
         * @date 2018/5/3
         */
        private Expression buildPath(ComplexCondition complexCondition, Field field, Root<DOMAIN> root) throws IllegalAccessException {
            Expression<?> path;
            if (null == complexCondition.target()) {
                path = root.get(field.getName());
            } else {
                path = root.get(complexCondition.target());
            }
            if (field.getType().equals(List.class)) {
                //做空值校验
                if (((List) FieldUtils.readField(field, query)).isEmpty() || null == ((List) FieldUtils.readField(field, query)).get(0)) {
                    return null;
                } else {
                    Class type = ((List) FieldUtils.readField(field, query)).get(0).getClass();
                    path = path.as(type);
                }
            } else if (TemporalAccessor.class.isInstance(field.getType())) {

            } else {
                path = path.as(field.getType());
            }
            return path;
        }

    }
}

