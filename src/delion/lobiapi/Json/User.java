package delion.lobiapi.Json;

import com.google.gson.annotations.SerializedName;

public class User {
	public Long contacted_date;
	public String cover;
	@SerializedName("default")
	public Integer _default;
	public String description;
	public String icon;
	public String lat;
	public String lng;
	public String located_date;
	public String name;
	public Integer premium;
	public String uid;
	public Long followed_date;
	public Long following_date;
}
