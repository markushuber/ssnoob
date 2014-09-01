package org.sbaresearch.socialsnapshot.crawl.entity;

import java.util.HashMap;
import java.util.HashSet;

/**
 * @author Maurice Wohlkönig
 */
public class FbMetadata {

	private HashMap<String, String> connections;
	private HashSet<Field> fields;
	private String type;
	
	public HashMap<String, String> getConnections() {
		return this.connections;
	}

	public String getType() {
		return this.type;
	}

	@Override
	public String toString() {
		return "FbMetadata [connections=" + this.connections + ", fields=" + this.fields + "]";
	}

	private static class Field {

		String name;
		String description;

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Field other = (Field) obj;
			if (this.description == null) {
				if (other.description != null)
					return false;
			} else if (!this.description.equals(other.description))
				return false;
			if (this.name == null) {
				if (other.name != null)
					return false;
			} else if (!this.name.equals(other.name))
				return false;
			return true;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((this.description == null) ? 0 : this.description.hashCode());
			result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
			return result;
		}

		@Override
		public String toString() {
			return "Field [name=" + this.name + ", description=" + this.description + "]";
		}

	}
}
