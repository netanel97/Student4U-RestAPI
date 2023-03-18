package entities;

public class ObjectDetails {

	private String detail;
	private String detail2;

	public ObjectDetails() {
		super();
	}

	public ObjectDetails(String detail, String detail2) {
		super();
		this.detail = detail;
		this.detail2 = detail2;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getDetail2() {
		return detail2;
	}

	public void setDetail2(String detail2) {
		this.detail2 = detail2;
	}

	@Override
	public String toString() {
		return "ObjectDetails [detail=" + detail + ", detail2=" + detail2 + "]";
	}
	
	

}
