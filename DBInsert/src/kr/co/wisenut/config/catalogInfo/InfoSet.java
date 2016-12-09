package kr.co.wisenut.config.catalogInfo;


/**
 *
 * InfoSet
 *
 * <Source> - <CatalogInfo> - <Mapping> - fieldname[] 의 값들을 set
 *
 * @author  이준명
 *
 */
public class InfoSet {
    private String fieldName;
    private String subquery;
    private String value;
    private String autoIncrement;
    private String skip;
    private String type;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

	public String getSubquery() {
		return subquery;
	}

	public void setSubquery(String subquery) {
		this.subquery = subquery;
	}
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getAutoIncrement() {
		return autoIncrement;
	}

	public void setAutoIncrement(String autoIncrement) {
		this.autoIncrement = autoIncrement;
	}

	public String getSkip() {
		return skip;
	}

	public void setSkip(String skip) {
		this.skip = skip;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
