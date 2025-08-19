package com.strandls.geoentities.util;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import jakarta.persistence.NoResultException;

public abstract class AbstractDAO<T, K extends Serializable> {

	protected final SessionFactory sessionFactory;
	protected final Class<T> daoType;

	@SuppressWarnings("unchecked")
	protected AbstractDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		this.daoType = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	public T save(T entity) {
		return executeInTransaction(session -> {
			session.save(entity);
			return entity;
		});
	}

	public T update(T entity) {
		return executeInTransaction(session -> {
			session.update(entity);
			return entity;
		});
	}

	public T delete(T entity) {
		return executeInTransaction(session -> {
			session.delete(entity);
			return entity;
		});
	}

	public abstract T findById(K id);

	public List<T> findAll() {
		try (Session session = sessionFactory.openSession()) {
			String hql = "FROM " + daoType.getSimpleName();
			return session.createQuery(hql, daoType).list();
		}
	}

	public List<T> findAll(int limit, int offset) {
		try (Session session = sessionFactory.openSession()) {
			String hql = "FROM " + daoType.getSimpleName();
			return session.createQuery(hql, daoType).setFirstResult(offset).setMaxResults(limit).list();
		}
	}

	public T findByPropertyWithCondition(String property, Object value, String condition) {
		try (Session session = sessionFactory.openSession()) {
			String hql = "FROM " + daoType.getSimpleName() + " t WHERE t." + property + " " + condition + " :value";
			Query<T> query = session.createQuery(hql, daoType);
			query.setParameter("value", value);
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<T> getByPropertyWithCondition(String property, Object value, String condition, int limit, int offset) {
		try (Session session = sessionFactory.openSession()) {
			String hql = "FROM " + daoType.getSimpleName() + " t WHERE t." + property + " " + condition
					+ " :value ORDER BY t.id";
			Query<T> query = session.createQuery(hql, daoType).setParameter("value", value).setFirstResult(offset)
					.setMaxResults(limit);
			return query.getResultList();
		}
	}

	public List<T> fetchFilteredRecords(String attribute1, String attribute2, Object value1, Object value2,
			String condition, String orderBy) {

		try (Session session = sessionFactory.openSession()) {
			String hql = "FROM " + daoType.getSimpleName() + " t " + "WHERE t." + attribute1 + " " + condition
					+ " :value1 " + "AND t." + attribute2 + " " + condition + " :value2 " + "ORDER BY t." + orderBy
					+ " DESC";

			Query<T> query = session.createQuery(hql, daoType);
			query.setParameter("value1", value1);
			query.setParameter("value2", value2);
			return query.getResultList();
		}
	}

	// Generic transaction execution wrapper
	protected <R> R executeInTransaction(HibernateTransaction<R> action) {
		Transaction tx = null;
		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			R result = action.execute(session);
			tx.commit();
			return result;
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			throw e;
		}
	}

	@FunctionalInterface
	public interface HibernateTransaction<R> {
		R execute(Session session);
	}
}
