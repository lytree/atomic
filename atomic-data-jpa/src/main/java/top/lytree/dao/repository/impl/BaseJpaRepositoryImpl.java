package top.lytree.dao.repository.impl;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import top.lytree.annotations.ComplexCondition;
import top.lytree.annotations.NullHandler;
import top.lytree.annotations.OrderBy;
import top.lytree.annotations.OrderGroup;
import top.lytree.exceptions.UnknownComplexConditionClassException;
import top.lytree.bean.FieldUtils;
import top.lytree.oss.model.exception.exception.ServerException;
import top.lytree.oss.model.exception.result.ServerCode;
import top.lytree.oss.model.request.support.BaseQuery;
import top.lytree.dao.repository.BaseJpaRepository;


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
    public Page<DOMAIN> pageByCondition(BaseQuery query, Pageable pageable) {

        List<Sort.Order> orders = buildOrder(query);
        pageable.getSortOr(Sort.by(orders));
        DOMAIN queryParams = generatePojo(this.pojoClass);

        BeanUtils.copyProperties(queryParams, query);

        Example<DOMAIN> example = Example.of(queryParams);

        return super.findAll(example, pageable);
    }

    @Override
    public List<DOMAIN> findAllByCondition(BaseQuery query) {

        List<Sort.Order> orders = buildOrder(query);
        Sort sort = Sort.by(orders);
        DOMAIN queryParams = generatePojo(this.pojoClass);

        BeanUtils.copyProperties(queryParams, query);

        Example<DOMAIN> example = Example.of(queryParams);

        return findAll(example, sort);
    }

    @Override
    public DOMAIN findOneByCondition(BaseQuery query) {

        DOMAIN queryParams = generatePojo(this.pojoClass);

        BeanUtils.copyProperties(queryParams, query);

        Example<DOMAIN> example = Example.of(queryParams);

        return findOne(example).orElse(null);
    }

    @Override
    public List<DOMAIN> findAllByComplexCondition(BaseQuery query) {

        List<Sort.Order> orders = buildOrder(query);
        Sort sort = Sort.by(orders);
        Specification<DOMAIN> complexConditions = Specification.where(new CommonSpecification(query));
        return findAll(complexConditions, sort);
    }

    @Override
    public Page<DOMAIN> pageByComplexCondition(BaseQuery queryReq, Pageable pageable) {

        List<Sort.Order> orders = buildOrder(queryReq);
        pageable.getSortOr(Sort.by(orders));
        Specification<DOMAIN> complexConditions = Specification.where(new CommonSpecification(queryReq));
        return super.findAll(complexConditions, pageable);
    }

    @Override
    public Long countByComplexCondition(BaseQuery queryReq) {

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
            throw new ServerException(ServerCode.POJO_INSTANCE_FAIL);
        }

    }

    private Sort.NullHandling transformNullHandler(String nullHandler) {
        if (NullHandler.FIRST.equals(nullHandler)) {
            return Sort.NullHandling.NULLS_FIRST;
        } else if (NullHandler.LAST.equals(nullHandler)) {
            return Sort.NullHandling.NULLS_LAST;
        } else {
            return Sort.NullHandling.NATIVE;
        }
    }

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

    private final class CommonSpecification implements Specification<DOMAIN> {

        private final BaseQuery baseQuery;

        public CommonSpecification(BaseQuery query) {
            this.baseQuery = query;
        }

        @Override
        public Predicate toPredicate(Root<DOMAIN> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            List<Predicate> predicates = new ArrayList<>();
            for (Field field : baseQuery.getClass().getDeclaredFields()) {
                field.setAccessible(Boolean.TRUE);
                ComplexCondition condition = field.getAnnotation(ComplexCondition.class);
                if (ObjectUtils.isEmpty(condition)) {
                    //如果没有注解 忽略此字段 不进行构建处理
                    continue;
                }
                //如果没有输入实体字段 默认为当前属性字段的名称
                String nameStr = condition.target();
                if (StringUtils.hasText(nameStr)) {
                    nameStr = field.getName();
                }
                String[] names = StringUtils.split(nameStr, ".");
                assert names != null;
                Path expression = root.get(names[0]);
                for (int i = 1; i < names.length; i++) {
                    expression = expression.get(names[i]);
                }
                //in 和or 中需要的一个中间变量 用来将filter.value放入数组
                Object[] objects = new Object[1];
                Object val = ReflectionUtils.getField(field, baseQuery);

                switch (condition.sign()) {
                    case EQ:
                        if (ObjectUtils.isEmpty(val)) {
                            continue;
                        }
                        predicates.add(cb.equal(expression, val));
                        break;
                    case LIKE:
                        if (ObjectUtils.isEmpty(val)) {
                            continue;
                        }
                        predicates.add(cb.like(expression, "%" + val + "%"));
                        break;
                    case NOT_LIKE:
                        if (ObjectUtils.isEmpty(val)) {
                            continue;
                        }
                        predicates.add(cb.notLike(expression, "%" + val + "%"));
                        break;
                    case GT:
                        if (ObjectUtils.isEmpty(val)) {
                            continue;
                        }
                        predicates.add(cb.greaterThan(expression, (Comparable) val));
                        break;
                    case LT:
                        if (ObjectUtils.isEmpty(val)) {
                            continue;
                        }
                        predicates.add(cb.lessThan(expression, (Comparable) val));
                        break;
                    case GTE:
                        if (ObjectUtils.isEmpty(val)) {
                            continue;
                        }
                        predicates.add(cb.greaterThanOrEqualTo(expression, (Comparable) val));
                        break;
                    case LTE:
                        if (ObjectUtils.isEmpty(val)) {
                            continue;
                        }
                        predicates.add(cb.lessThanOrEqualTo(expression, (Comparable) val));
                        break;
                    case IN:
                        if (ObjectUtils.isEmpty(val)) {
                            continue;
                        }
                        //因为spring data jpa 本身没有对数组进行判断 传入数组的话会失败 所以在此进行是否是数组的判断
                        //因为expression。in参数是不定参数  理论上是可以传入数组 但是直接传入object不能判断是否为数组
                        //把他当成一个参数 而不是需要的数组参数
                        Object filterValue = val;
                        if (filterValue.getClass().isArray()) {
                            objects = (Object[]) filterValue;
                        } else {
                            objects[0] = filterValue;
                        }
                        predicates.add(expression.in(objects));
                        break;
                    case NEQ:
                        if (ObjectUtils.isEmpty(val)) {
                            continue;
                        }
                        predicates.add(cb.notEqual(expression, val));
                        break;
                    case IS_NULL:
                        predicates.add(cb.isNull(expression));
                    case IS_NOT_NULL:
                        predicates.add(cb.isNull(expression));
                    default:
                        throw new UnknownComplexConditionClassException();
                }
            }

            query.where(cb.and(predicates.toArray(new Predicate[]{})));

            return query.getRestriction();
        }
    }
}

