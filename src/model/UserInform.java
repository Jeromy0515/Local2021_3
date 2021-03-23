package model;

public class UserInform {
	public String whName;
	public String address;
	public int hallPrice;
	public String whType;
	public String mealType;
	public int mealPrice;
	public int people;
	public int ivNum;
	public String date;
	
	public UserInform(String whName,String address,int people,	int hallPrice,	String whType,	
			String mealType,int mealPrice,int ivNum,String date) {
		this.whName = whName;
		this.address = address;
		this.ivNum = ivNum;
		this.hallPrice = hallPrice;
		this.whType = whType;
		this.mealType = mealType;
		this.mealPrice = mealPrice;
		this.people = people;
		this.date = date;
	}

}
