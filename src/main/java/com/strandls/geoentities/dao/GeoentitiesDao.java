/**
 * 
 */
package com.strandls.geoentities.dao;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strandls.geoentities.pojo.Geoentities;
import com.strandls.geoentities.util.AbstractDAO;

/**
 * @author Abhishek Rudra
 *
 */
public class GeoentitiesDao extends AbstractDAO<Geoentities, Long> {

	private final Logger logger = LoggerFactory.getLogger(GeoentitiesDao.class);

	/**
	 * @param sessionFactory
	 */
	@Inject
	protected GeoentitiesDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public Geoentities findById(Long id) {
		Geoentities result = null;
		Session session = sessionFactory.openSession();
		try {
			result = session.get(Geoentities.class, id);
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Geoentities> findByNameLike(String phrase) {
		Session session = sessionFactory.openSession();
		List<Geoentities> result = new ArrayList<Geoentities>();

		String qry = "from Geoentities where lower(placeName) like :phrase order by char_length(placeName) ASC";

		try {
			Query<Geoentities> query = session.createQuery(qry);
			phrase = "%" + phrase.toLowerCase() + "%";
			query.setParameter("phrase", phrase);
			query.setMaxResults(10);
			result = query.getResultList();

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}

		return result;

	}

}
