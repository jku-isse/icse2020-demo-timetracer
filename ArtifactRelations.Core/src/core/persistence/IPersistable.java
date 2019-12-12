package core.persistence;


public interface IPersistable<T> {

	Iterable<T> findAll();
	
	Iterable<T> loadAllEntities(String propertyName, Object propertyValue, int depth);
	
	T find(String id);

	void delete(String id);

	//T createOrUpdate(T object);

	void push(T entity);
	
	void delete(T entity);

	T createOrUpdate(T entity);

	void invalidateSession();
		
}

