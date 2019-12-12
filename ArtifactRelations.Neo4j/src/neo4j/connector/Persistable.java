package neo4j.connector;

import javax.inject.Inject;

import org.neo4j.ogm.cypher.ComparisonOperator;
import org.neo4j.ogm.cypher.Filter;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

import core.persistence.IPersistable;

public abstract class Persistable<T> implements IPersistable<T> {

    protected int DEPTH_LIST = 1;
    protected int DEPTH_ENTITY = 1;
    
    @Inject 
    private SessionFactory neo4jFactory;
    private Session session;

    @Override
    public void invalidateSession() {    	
    	session.clear();    	
    	session = null;
    }
    
    protected Session getSession() {
    	if (session == null) {
    		session = neo4jFactory.openSession();
    		//session = neo4jFactory.set
    	}
    	return session;
    }
    
    @Override
	public Iterable<T> findAll() {
        return getSession().loadAll(getEntityType());
    }

    @Override
    public T find(String id) {
        return getSession().load(getEntityType(), id, DEPTH_ENTITY);
    }
    
    public T find(String id, int depth) {
        return getSession().load(getEntityType(), id, depth);
    }
    
    public void purgeDatabase() {
        getSession().purgeDatabase();
    }

    @Override
    public void delete(String id) {
    	T entity = getSession().load(getEntityType(), id);
    	if (entity != null)
    		getSession().delete(entity);
    }
    
    @Override
    public void delete(T entity) {
    	getSession().delete(entity);
    }

    @Override
    public void push(T entity) {
    	getSession().save(entity, DEPTH_ENTITY);
    }
    
    @Override
    public T createOrUpdate(T entity) {
    	getSession().save(entity, DEPTH_ENTITY);
    	return entity;
    }
    
    @Override
    public Iterable<T> loadAllEntities(String propertyName, Object propertyValue, int depth) {	
    	Filter filter = new Filter(propertyName, ComparisonOperator.EQUALS, propertyValue);
    	return getSession().loadAll(getEntityType(), filter, depth);
    }
   

    abstract Class<T> getEntityType();
}