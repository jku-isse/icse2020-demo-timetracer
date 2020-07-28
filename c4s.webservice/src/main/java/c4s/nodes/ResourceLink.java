package c4s.nodes;

import org.neo4j.ogm.annotation.*;

@NodeEntity
public class ResourceLink{
	
	@Id
	public String href;
	@Property
	public String context;	
	@Property
	public String rel;
	@Property
	public String as;
	@Property
	public String linkType;
	@Property
	public String title;
	
	public String getContext() {
		return context;
	}
	
	public void setContext(String context) {
		this.context = context;
	}
	
	public String getHref() {
		return this.href;
	}
	
	public void setHref(String href) {
		this.href = href;		
	}
	
	public String getRel() {
		return rel;
	}
	
	public void setRel(String rel) {
		this.rel = rel;
	}
	
	public String getAs() {
		return as;
	}
	
	public void setAs(String as) {
		this.as = as;
	}
	
	public String getLinkType() {
		return linkType;
	}
	
	public void setLinkType(String linkType) {
		this.linkType = linkType;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getId() {
		return href;
	}
	
	@Deprecated
	public ResourceLink(){
		super();
	}
	
	@Override
	public String toString() {
		return "ResourceLink [" + title + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((href == null) ? 0 : href.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResourceLink other = (ResourceLink) obj;
		if (href == null) {
			if (other.href != null)
				return false;
		} else if (!href.equals(other.href))
			return false;
		return true;
	}
}
